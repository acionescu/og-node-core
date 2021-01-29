package net.segoia.ogeg.services.storage.agents;

import java.io.IOException;
import java.io.OutputStream;

import net.segoia.event.eventbus.streaming.StreamControllerContext;
import net.segoia.event.eventbus.streaming.WriteStreamController;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedData;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedEvent;
import net.segoia.util.data.storage.Storage;
import net.segoia.util.data.storage.StorageEntityExistsException;
import net.segoia.util.data.storage.StorageException;

public class StorageWriteController extends WriteStreamController {
    private OutputStream outputStream;
    private Storage mainDataStore;
    private String storageKey;

    public StorageWriteController(Storage mainDataStore, String storageKey) {
	super();
	this.mainDataStore = mainDataStore;
	this.storageKey = storageKey;
    }

    @Override
    public void init(StreamControllerContext controllerContext) {
	/* get source peer */
	String sourcePeerId = controllerContext.getStreamContext().getSourcePeerId();

	try {
	    /* create the leaf file */
	    outputStream = mainDataStore.createLeaf(storageKey);

	} catch (StorageEntityExistsException e) {

	    controllerContext.sendToPeer(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(storageKey, StorageManagerConstants.STORAGE_ENTITY_EXISTS)),
		    sourcePeerId);
	    throw new RuntimeException("Failed create storage for key "+storageKey, e);
	} catch (StorageException e) {
	    e.printStackTrace();
	    controllerContext.sendToPeer(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(storageKey, StorageManagerConstants.STORAGE_PATH_INVALID)),
		    sourcePeerId);
	    throw new RuntimeException("Failed create storage for key "+storageKey, e);
	}

	/* all seems well, init */
	super.init(controllerContext);

    }

    @Override
    protected void write(byte[] data) throws Exception {
	outputStream.write(data);
    }

    @Override
    public void terminate() {
	try {
	    controllerContext.logger().debug("Closing output stream for file "+storageKey);
	    outputStream.close();
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }

}
