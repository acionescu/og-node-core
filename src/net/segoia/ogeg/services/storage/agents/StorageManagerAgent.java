package net.segoia.ogeg.services.storage.agents;

import java.io.OutputStream;
import java.util.Arrays;

import net.segoia.event.eventbus.CustomEventContext;
import net.segoia.event.eventbus.app.EventNodeControllerContext;
import net.segoia.event.eventbus.peers.GlobalEventNodeAgent;
import net.segoia.event.eventbus.streaming.CustomStreamControllerContext;
import net.segoia.event.eventbus.streaming.StreamConstants;
import net.segoia.event.eventbus.streaming.StreamsManager;
import net.segoia.event.eventbus.streaming.WriteStreamController;
import net.segoia.event.eventbus.streaming.events.EndStreamEvent;
import net.segoia.event.eventbus.streaming.events.PeerStreamEndedData;
import net.segoia.event.eventbus.streaming.events.PeerStreamEndedEvent;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedData;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedEvent;
import net.segoia.event.eventbus.streaming.events.StartStreamRequest;
import net.segoia.event.eventbus.streaming.events.StartStreamRequestEvent;
import net.segoia.event.eventbus.streaming.events.StreamInfo;
import net.segoia.event.eventbus.streaming.events.StreamPacketEvent;
import net.segoia.ogeg.services.storage.events.ListStorageEntitiesRequest;
import net.segoia.ogeg.services.storage.events.ListStorageEntitiesRequestEvent;
import net.segoia.ogeg.services.storage.events.StorageEntitiesListData;
import net.segoia.ogeg.services.storage.events.StorageEntitiesListDataEvent;
import net.segoia.util.data.storage.DataStore;
import net.segoia.util.data.storage.StorageEntity;
import net.segoia.util.data.storage.StorageEntityExistsException;
import net.segoia.util.data.storage.StorageException;

public class StorageManagerAgent extends GlobalEventNodeAgent {
    private StorageManagerAgentConfig config = new StorageManagerAgentConfig();

    private StorageManagerAgentContext agentContext;

    private StreamsManager streamsManager;

    @Override
    protected void agentInit() {

    }

    @Override
    protected void config() {
	/* init agent context from config */
	agentContext = new StorageManagerAgentContext(config);

	streamsManager = new StreamsManager(new EventNodeControllerContext(context, null));

    }

    @Override
    public void terminate() {
	// TODO Auto-generated method stub

    }

    @Override
    protected void registerHandlers() {
	context.addEventHandler(ListStorageEntitiesRequestEvent.class, (c) -> {
	    handleListStorageEntities(c);
	});

	context.addEventHandler(StartStreamRequestEvent.class, (c) -> {
	    handleStartStreamRequest(c);
	});

	context.addEventHandler(StreamPacketEvent.class, (c) -> {
	    handleStreamPacket(c);
	});

	context.addEventHandler(EndStreamEvent.class, (c) -> {
	    handleStreamEnd(c);
	});

	context.addEventHandler(PeerStreamEndedEvent.class, (c) -> {
	    handlePeerStreamEnded(c);
	});

    }

    protected void handlePeerStreamEnded(CustomEventContext<PeerStreamEndedEvent> c) {
	PeerStreamEndedEvent event = c.getEvent();
	if (!context.isEventLocal(event)) {
	    /* we're only interested in local events */
	    return;
	}
	PeerStreamEndedData data = event.getData();

	if (data.getReason().getCode() != StreamConstants.PEER_LEFT.getCode()) {
	    /* we're tracking cases when that user left in a middle of a stream upload, so we can cleanup */
	    return;
	}

	StreamInfo streamInfo = data.getPeerStreamData().getStreamData().getStreamInfo();

	String appId = streamInfo.getAppId();
	if (!StorageManagerConstants.STORAGE_APP_ID.equals(appId)) {
	    /* if the stream is not manged by us, do nothing */
	    return;
	}

	if(context.isDebugEnabled()) {
	    context.debug("Deleting file "+streamInfo.getStreamId() +" as peer left withouth finishing the upload.");
	}
	
	/* remove the unfinished file */
	agentContext.getMainDataStore().delete(streamInfo.getStreamId());
    }

    protected void handleListStorageEntities(CustomEventContext<ListStorageEntitiesRequestEvent> c) {
	ListStorageEntitiesRequestEvent event = c.getEvent();
	ListStorageEntitiesRequest data = event.getData();

	if (!context.testEventDataPresent(event)) {
	    return;
	}

	String path = data.getPath();
	System.out.println("listing path " + path);
	/* if the path is empty use main data store, otherwise get the datastore indicated by the path */
	DataStore targetDataStore = agentContext.getMainDataStore();

	String peerId = event.from();
	if (path != null && !path.isEmpty()) {

	    try {
		targetDataStore = (DataStore) targetDataStore.getStorage(path);
	    } catch (StorageException e) {
		/* send an invalid path error */
		context.forwardTo(StorageManagerConstants.builPathInvalidErrorEvent(event.getEt()), peerId);
		return;
	    }
	}

	StorageEntity[] entitiesArray = targetDataStore.listEntities();
	System.out.println("entities for path " + entitiesArray);
	// TODO check list conditions for each entities

	StorageEntitiesListDataEvent responseEvent = new StorageEntitiesListDataEvent(
		new StorageEntitiesListData(path, Arrays.asList(entitiesArray), entitiesArray.length));

	context.forwardTo(responseEvent, peerId);

    }

    /**
     * Handles file upload requests
     * 
     * @param c
     */
    protected void handleStartStreamRequest(CustomEventContext<StartStreamRequestEvent> c) {
	StartStreamRequestEvent event = c.getEvent();
	StartStreamRequest data = event.getData();

	if (data == null) {
	    return;
	}

	StreamInfo streamInfo = data.getStreamInfo();
	if (streamInfo == null) {
	    return;
	}

	String appId = streamInfo.getAppId();
	if (!StorageManagerConstants.STORAGE_APP_ID.equals(appId)) {
	    /* if this was not sent to this app, disregard it */
	    return;
	}

	/* mark the event as handled by us */
	event.setHandled();

	String streamId = streamInfo.getStreamId();
	String peerId = event.from();
	long dataSize = streamInfo.getTotalDataLength();

	/* test that the upload size doesn't exceed the maximum allowed size */

	if (dataSize > config.getMaxFileSize()) {
	    context.forwardTo(new StartStreamRejectedEvent(
		    new StartStreamRejectedData(streamId, StorageManagerConstants.DATA_TOO_LARGE)), peerId);
	    return;
	}

	/* test that the key is defined */
	if (streamId == null) {
	    context.forwardTo(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(streamId, StorageManagerConstants.STORAGE_ENTITY_KEY_MISSING)),
		    peerId);
	    return;
	}

	/* get the path for the key */
	DataStore mainDataStore = agentContext.getMainDataStore();
	String[] storagePath = mainDataStore.extractHierarchy(streamId);

	/* test that the storage path depth is not exceeded */
	if (storagePath.length > config.getMaxStorageDepth()) {
	    context.forwardTo(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(streamId, StorageManagerConstants.STORAGE_PATH_DEPTH_EXCEEDED)),
		    peerId);
	    return;
	}

	/* get the leaf key name */
	String leafKey = storagePath[storagePath.length - 1];

	/* test that the name does not exceed max key length */

	if (leafKey.length() > config.getMaxKeyLength()) {
	    context.forwardTo(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(streamId, StorageManagerConstants.STORAGE_KEY_TOO_LONG)),
		    peerId);
	    return;
	}

	/* if the file exists, send error */
	if (mainDataStore.exists(streamId)) {
	    context.forwardTo(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(streamId, StorageManagerConstants.STORAGE_ENTITY_EXISTS)),
		    peerId);
	    return;
	}

	/* if file creation succeeded, create a stream controller */

	WriteStreamController writeStreamController = new StorageWriteController(mainDataStore, streamId);

	/* add controller as local data on the event */

	c.addLocalData(new CustomStreamControllerContext(writeStreamController));

	/* delegate event to streams manager */

	streamsManager.processEvent(c);

    }

    public void handleStreamPacket(CustomEventContext<StreamPacketEvent> c) {
	streamsManager.processEvent(c);
    }

    public void handleStreamEnd(CustomEventContext<EndStreamEvent> c) {
	streamsManager.processEvent(c);
    }

}
