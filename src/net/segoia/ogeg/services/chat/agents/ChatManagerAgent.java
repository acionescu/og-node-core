/**
 * og-node-core - The core resources of an Open Groups node
 * Copyright (C) 2020  Adrian Cristian Ionescu - https://github.com/acionescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.segoia.ogeg.services.chat.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.segoia.event.eventbus.CustomEventContext;
import net.segoia.event.eventbus.Event;
import net.segoia.event.eventbus.EventHeader;
import net.segoia.event.eventbus.app.EventNodeControllerContext;
import net.segoia.event.eventbus.peers.GlobalEventNodeAgent;
import net.segoia.event.eventbus.peers.LocalAgentEventNodeContext;
import net.segoia.event.eventbus.peers.events.PeerLeftEvent;
import net.segoia.event.eventbus.peers.vo.PeerInfo;
import net.segoia.event.eventbus.streaming.StreamsManager;
import net.segoia.event.eventbus.streaming.events.EndStreamEvent;
import net.segoia.event.eventbus.streaming.events.PeerStreamData;
import net.segoia.event.eventbus.streaming.events.PeerStreamEndedData;
import net.segoia.event.eventbus.streaming.events.PeerStreamEndedEvent;
import net.segoia.event.eventbus.streaming.events.PeerStreamStartedEvent;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedData;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedEvent;
import net.segoia.event.eventbus.streaming.events.StartStreamRequest;
import net.segoia.event.eventbus.streaming.events.StartStreamRequestEvent;
import net.segoia.event.eventbus.streaming.events.StreamContext;
import net.segoia.event.eventbus.streaming.events.StreamData;
import net.segoia.event.eventbus.streaming.events.StreamInfo;
import net.segoia.event.eventbus.streaming.events.StreamPacketData;
import net.segoia.event.eventbus.streaming.events.StreamPacketEvent;
import net.segoia.event.eventbus.vo.services.EventNodePublicServiceDesc;
import net.segoia.ogeg.services.chat.events.ChatConfig;
import net.segoia.ogeg.services.chat.events.ChatErrorData;
import net.segoia.ogeg.services.chat.events.ChatErrorEvent;
import net.segoia.ogeg.services.chat.events.ChatInitData;
import net.segoia.ogeg.services.chat.events.ChatInitEvent;
import net.segoia.ogeg.services.chat.events.ChatJoinRequest;
import net.segoia.ogeg.services.chat.events.ChatJoinRequestEvent;
import net.segoia.ogeg.services.chat.events.ChatJoinedEvent;
import net.segoia.ogeg.services.chat.events.ChatLeaveRequestEvent;
import net.segoia.ogeg.services.chat.events.ChatLeftEvent;
import net.segoia.ogeg.services.chat.events.ChatMessage;
import net.segoia.ogeg.services.chat.events.ChatMessageEvent;
import net.segoia.ogeg.services.chat.events.ChatPeerData;
import net.segoia.ogeg.services.chat.events.ChatServiceDataView;
import net.segoia.ogeg.services.chat.events.ChatServiceDataViewEvent;
import net.segoia.ogeg.services.chat.events.RemoteChatPeerData;
import net.segoia.ogeg.services.core.events.ServiceNodeData;
import net.segoia.ogeg.services.core.events.SyncWithServiceNodeEvent;
import net.segoia.util.data.SetMap;
import net.segoia.util.logging.Logger;
import net.segoia.util.logging.MasterLogManager;

public class ChatManagerAgent extends GlobalEventNodeAgent {
    private Logger logger = MasterLogManager.getLogger("ChatManagerAgent");

    private ChatManagerAgentConfig config = new ChatManagerAgentConfig();
    /**
     * Keeps active chats by chat key
     */
    private Map<String, Chat> chats;

    /**
     * Keeps the chats where at least one participant is a remote one communicating through the gateway represented by
     * the key of this map
     */
    private SetMap<String, String> chatsForGateways = new SetMap<>();

    private StreamsManager streamsManager;

    public ChatManagerAgent() {
	super();
	// TODO Auto-generated constructor stub
    }

    @Override
    protected void agentInit() {
	// TODO Auto-generated method stub
	chats = new HashMap<>();

	/* init streams manager */
	streamsManager = new StreamsManager();
	streamsManager.setConfig(config.getStreamsManagerConfig());
	streamsManager.init(new EventNodeControllerContext(context, null));

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.segoia.event.eventbus.peers.EventNode#registerHandlers()
     */
    @Override
    protected void registerHandlers() {

	context.addEventHandler(PeerLeftEvent.class, (c) -> {

	    PeerLeftEvent event = c.getEvent();
	    String peerId = (String) event.getData().getPeerId();

	    /* remove as interop node */
	    context.removeInteropPeerId(peerId);

	    if (chatsForGateways.containsKey(peerId)) {
		/* if this is a gateway peer, remove remote peers using it */
		removePeersForGateway(peerId, event);
	    } else {
		removePeerFromAllChats(peerId);
	    }

	});

	context.addEventHandler(ChatJoinRequestEvent.class, (c) -> {
	    ChatJoinRequestEvent event = c.event();
	    ChatJoinRequest request = event.getData();
	    String chatKey = request.getChatKey();

	    String newPeerId = event.from();

	    String alias = request.getAlias();
	    if (alias == null) {
		alias = newPeerId;
	    }

	    if (chatKey == null) {
		/* create a random chat */
		chatKey = context.genereateNewSessionId(chats);
	    }

	    Chat chatByKey = getChatByKey(chatKey, false);
	    
	    if(chatByKey != null && chatByKey.isFull()) {
		context.forwardTo(new ChatErrorEvent(new ChatErrorData(ChatErrors.ROOM_CAPACITY_REACHED,
			ChatErrors.ROOM_CAPACITY_REACHED.getMessage())), newPeerId);
		return;
	    }
	    
	    if (chatByKey != null && chatByKey.hasPeerWithAlias(alias)) {
		/* There's already a peer with this alias. Send an error to the peer. */
		context.forwardTo(new ChatErrorEvent(new ChatErrorData(ChatErrors.JOIN_REJECTED,
			"Peer with alias " + alias + " already on chat " + chatKey)), newPeerId);
		if (context.isDebugEnabled()) {
		    context.debug("Rejectin join request on chat " + chatKey + " for alias " + alias
			    + ". Alias already taken.");
		}
		
		/* discard this request */
		return;
	    }
	    
	    /* check if max available rooms reached */
	    if(chatByKey==null && (chats.size() >= config.getMaxAllowedRooms())) {
		/* no more rooms available */
		context.forwardTo(new ChatErrorEvent(new ChatErrorData(ChatErrors.CHAT_ROOMS_CAPACITY_REACHED,
			ChatErrors.CHAT_ROOMS_CAPACITY_REACHED.getMessage())), newPeerId);
		
		if (context.isDebugEnabled()) {
		    context.debug("Room capacity reached."+chats.size());
		}
		return;
	    }

	    ChatPeerData chatPeerData = new ChatPeerData(chatKey, newPeerId, true, alias);
	    ChatJoinedEvent chatJoinedEvent = new ChatJoinedEvent(chatPeerData);
	    chatJoinedEvent.addNoForward(newPeerId);

	    Chat chat = getChatByKey(chatKey);
	    Set<String> localParticipantsSnapshot = chat.getLocalParticipants();
	    /* notify the current local participants of the new peer */
	    context.forwardTo(chatJoinedEvent, localParticipantsSnapshot);

	    /* add the new peer to the participants set */

	    chat.addLocalParicipant(chatPeerData);

	    List<ChatPeerData> chatParticipants = createParticipantsCopy(getChatParticipants(chatKey));
	    ChatInitEvent chatInitEvent = new ChatInitEvent(new ChatInitData(chatKey, chatParticipants,chat.getConfig()));
	    /* send a chat init event to the new peer */
	    context.forwardTo(chatInitEvent, newPeerId);

	    /* post the chat joined on local bus */
	    context.postEvent(new ChatJoinedEvent(chatPeerData));

	});

	context.addEventHandler(ChatLeaveRequestEvent.class, (c) -> {
	    ChatLeaveRequestEvent event = c.event();
	    removePeerFromChat(event.getData().getChatKey(), event.from(), true, event);
	});

	context.addEventHandler(ChatMessageEvent.class, (c) -> {

	    ChatMessageEvent event = c.event();
	    ChatMessage data = event.getData();
	    String chatKey = data.getChatKey();

	    Chat chat = getChatByKey(chatKey, false);

	    if (chat == null) {
		logger.warn("Got message for invalid chat " + chatKey);
		return;
	    }

	    Set<String> partnersSnapshot = chat.getLocalParticipants();

	    String senderId = event.from();

	    RemoteChatPeerData remotePeerData = chat.getRemoteParticipant(event.getLastRelay());

	    if (remotePeerData != null) {
		/* this is a remote peer message */

	    } else {
		/* direct peer message */
		/* check if the sender is in the chat */
		if (!partnersSnapshot.contains(senderId)) {
		    logger.warn("Peer " + senderId + " sent rogue message on chat " + chatKey);
		    return;
		}

		/* don't sent the event to the sender */
		event.addNoForward(senderId);

	    }

	    if (context.isDebugEnabled()) {
		context.debug("forwarding chat message to local partners" + partnersSnapshot);
	    }
	    /* forward the message to local participants */
	    context.forwardTo(event, partnersSnapshot);

	    if (remotePeerData != null) {
		/* this is a remote peer message, add no forward for the receiving gateway */
		event.addNoForward(remotePeerData.getGatewayPeerId());

	    }
	    /* forward message to remote participants */
	    context.forwardTo(event, chat.getRemotePeersGateways());

	});

	/* handling streams */

	context.addEventHandler(StartStreamRequestEvent.class, (c) -> {
	    handleStartStreamRequest(c);
	});

	context.addEventHandler(EndStreamEvent.class, (c) -> {
	    handleEndStream(c);
	});

	context.addEventHandler(StreamPacketEvent.class, (c) -> {
	    handleStreamPacket(c);
	});

	context.addEventHandler(PeerStreamStartedEvent.class, (c) -> {
	    handlePeerStreamStarted(c);
	});

	context.addEventHandler(PeerStreamEndedEvent.class, (c) -> {
	    handlePeerStreamEnded(c);
	});

	/* interoperability */
	context.addEventHandler(ChatJoinedEvent.class, (c) -> {

	    ChatJoinedEvent event = c.getEvent();

	    EventHeader header = event.getHeader();
	    String channel = header.getChannel();
	    if (LocalAgentEventNodeContext.LOCAL.equals(channel)) {
		/* forward this to interop peers */
		context.forwardToInteropPeers(event);

		return;
	    }

	    /* we've got this from a direct peer */
	    if (header.getRelayedBy().size() == 1) {
		addRemoteChatPeer(c);
	    }
	});

	context.addEventHandler(ChatLeftEvent.class, (c) -> {
	    ChatLeftEvent event = c.getEvent();
	    EventHeader header = event.getHeader();
	    String channel = header.getChannel();
	    if (LocalAgentEventNodeContext.LOCAL.equals(channel)) {
		/* forward this to interop peers */
		context.forwardToInteropPeers(event);

		return;
	    }

	    /* we've got this from a direct peer */
	    if (header.getRelayedBy().size() == 1) {
		removeRemotePeer(c);
	    }
	});

	/* sent state to other service nodes */
	context.addEventHandler(SyncWithServiceNodeEvent.class, (c) -> {
	    Collection<EventNodePublicServiceDesc> ourServices = context.getAgentPublicServices().values();
	    SyncWithServiceNodeEvent event = c.getEvent();

	    if (!context.isEventLocal(event)) {
		if (context.isDebugEnabled()) {
		    context.debug("Rejecting remote service sync event " + event.toJson());
		}

		/* allow only local events */
		return;
	    }

	    /* we need the cause event from the actual peer */
	    Event causeEvent = event.getCauseEvent();

	    if (causeEvent == null) {
		context.debug("No cause event. Rejecting service sync event " + event.toJson());
		return;
	    }

	    /* get peer id */
	    String peerId = causeEvent.from();

	    ServiceNodeData data = event.getData();
	    if (data != null) {
		List<EventNodePublicServiceDesc> remoteServices = data.getServices();
		EventNodePublicServiceDesc serviceMatch = context.getNodeContext().getServicesManager()
			.getServiceMatch("chatService", ourServices, remoteServices);
		if (serviceMatch != null) {
		    /* we're both providing compatible chat services */

		    /* add this peer as interop peer */
		    context.addInteropPeerId(peerId);

		    Map<String, ChatInitData> activeChatsData = getActiveChatsData();
		    if (activeChatsData.size() == 0) {
			/* don't send a syncying event if we have no chats */
			return;
		    }

		    ChatServiceDataView chatServiceDataView = new ChatServiceDataView(activeChatsData);
		    /* send sync event */
		    context.forwardTo(new ChatServiceDataViewEvent(chatServiceDataView), peerId);
		}
	    } else if (context.isDebugEnabled()) {
		context.debug("Discarding sync event with null data");
	    }
	});

	context.addEventHandler(ChatServiceDataViewEvent.class, (c) -> {
	    ChatServiceDataViewEvent event = c.getEvent();
	    String gatewayPeerId = event.from();
	    if (!gatewayPeerId.equals(event.getLastRelay())) {
		/* allow only direct peers */
		return;
	    }

	    ChatServiceDataView data = event.getData();
	    if (data == null) {
		return;
	    }
	    Map<String, ChatInitData> chatsData = data.getChats();
	    if (chatsData == null) {
		return;
	    }

	    for (ChatInitData cid : chatsData.values()) {
		for (ChatPeerData cpd : cid.getParticipants()) {
		    addRemoteChatPeer(gatewayPeerId, cpd, event);
		}
	    }

	});
    }

    private boolean isPeerValidForChat(String peerId, String chatKey) {
	Chat chat = getChatByKey(chatKey, false);

	if (chat == null) {
	    return false;
	}

	RemoteChatPeerData remotePeerData = chat.getRemoteParticipant(peerId);

	if (remotePeerData != null) {
	    /* this is a remote peer message */
	    return true;

	} else {
	    /* direct peer message */
	    Set<String> partnersSnapshot = chat.getLocalParticipants();

	    /* check if the sender is in the chat */

	    return partnersSnapshot.contains(peerId);
	}
    }

    private void handleStartStreamRequest(CustomEventContext<StartStreamRequestEvent> c) {
	StartStreamRequestEvent event = c.getEvent();
	StartStreamRequest data = event.getData();

	context.logDebug("Handling start streqm request");

	if (data == null) {
	    return;
	}

	StreamInfo streamInfo = data.getStreamInfo();
	if (streamInfo == null) {
	    return;
	}

	String streamId = streamInfo.getStreamId();

	String peerId = event.from();

	
	String appId = streamInfo.getAppId();
	if (!ChatConstants.CHAT_APP_ID.equals(appId)) {
	    /* if this wasn not sent to this app, disregard it */
	    return;
	}
	
	/* mark the event as handled by us */
	event.setHandled();

	String chatKey = streamInfo.getAppTopicId();
	if (chatKey == null) {
	    context.forwardTo(
		    new StartStreamRejectedEvent(new StartStreamRejectedData(streamId, ChatConstants.CHAT_KEY_MISSING)),
		    peerId);
	    return;
	}

	Chat chat = getChatByKey(chatKey, false);

	if (chat == null) {
	    /* no chat for key */
	    context.forwardTo(
		    new StartStreamRejectedEvent(new StartStreamRejectedData(streamId, ChatConstants.CHAT_KEY_UNKNOWN)),
		    peerId);

	    if (context.isDebugEnabled()) {
		context.logDebug("Discarding start stream request. No chat for key " + chatKey);
	    }
	    return;
	}
	/* verify that this event was sent by a valid chat peer */
	if (!chat.getLocalParticipants().contains(peerId)) {
	    /* the peer is not in the chat */
	    context.forwardTo(new StartStreamRejectedEvent(
		    new StartStreamRejectedData(streamId, ChatConstants.INVALID_CHAT_USER)), peerId);

	    if (context.isDebugEnabled()) {
		context.logDebug("Discarding start stream request. Peer " + peerId + " not present in chat " + chatKey);
	    }
	    return;
	}
	
	if(!chat.getConfig().isStreamingAllowed()) {
	    context.forwardTo(new StartStreamRejectedEvent(
		    new StartStreamRejectedData(streamId, ChatConstants.STREAMING_NOT_ALLOWED)), peerId);
	    return;
	}
	
	if(chat.maximumStreamsReached()) {
	    context.forwardTo(new StartStreamRejectedEvent(
		    new StartStreamRejectedData(streamId, ChatConstants.MAX_STREAMS_REACHED)), peerId);
	    return;
	}
	
	if (context.isDebugEnabled()) {
	    context.logDebug("Delegating start stream event to streams manager");
	}
	/* delegate to the streams manager */
	streamsManager.processEvent(c);

    }

    private void handleEndStream(CustomEventContext<EndStreamEvent> c) {
	streamsManager.processEvent(c);
    }

    private void handlePeerStreamEnded(CustomEventContext<PeerStreamEndedEvent> c) {
	PeerStreamEndedEvent event = c.getEvent();
	PeerStreamEndedData data = event.getData();
	PeerStreamData peerStreamData = data.getPeerStreamData();
	StreamData streamData = peerStreamData.getStreamData();
	StreamInfo streamInfo = streamData.getStreamInfo();
	String chatKey = streamInfo.getAppTopicId();

	if (chatKey == null) {
	    return;
	}

	Chat chat = getChatByKey(chatKey, false);

	if (chat == null) {
	    return;
	}

	EventHeader header = event.getHeader();
	String channel = header.getChannel();

	String sourcePeerId = peerStreamData.getSourcePeerId();

	String chatPeerId = sourcePeerId;
	String gatewayPeerId = null;

	if (LocalAgentEventNodeContext.LOCAL.equals(channel)) {

	} else if (header.getRelayedBy().size() == 1) {
	    /* from a direct peer */

	    gatewayPeerId = event.getLastRelay();
	    /* get local id for remote peer */
	    String localId = context.getIdForRemotePeerByPath(gatewayPeerId, sourcePeerId);
	    if (localId == null) {
		return;
	    }
	    chatPeerId = localId;

	    context.logDebug("removing stream " + streamData.getStreamSessionId() + " for chat peer " + chatPeerId);
	    /* remove the stream for this remote peer */
	    streamsManager.removeStream(streamData.getStreamSessionId(), data.getReason(), c);

	} else {
	    return;
	}

	/* remove stream from peer */
	ChatPeerData chatPeerData = chat.getParticipants().get(chatPeerId);
	if (chatPeerData != null) {
	    chatPeerData.setStreamData(null);
	}

	PeerStreamEndedEvent globalStreamEndedEvent = event;

	if (gatewayPeerId != null) {
	    globalStreamEndedEvent = new PeerStreamEndedEvent(
		    new PeerStreamEndedData(new PeerStreamData(chatPeerId, streamData), data.getReason()));
	    globalStreamEndedEvent.addNoForward(gatewayPeerId);
	}

	/* forward to interop peers */
	context.forwardTo(globalStreamEndedEvent, chat.getRemotePeersGateways());

	if (gatewayPeerId == null) {

	    event.addNoForward(sourcePeerId);
	}

	/* forward to local participants */
	context.forwardTo(event, chat.getLocalParticipants());

    }

    private void handleStreamPacket(CustomEventContext<StreamPacketEvent> c) {
	StreamPacketEvent event = c.getEvent();
	StreamPacketData data = event.getData();
	String streamSessionId = data.getStreamSessionId();

	StreamContext sc = streamsManager.getStreamDataBySession(streamSessionId);
	if (sc == null) {
//	    if (context.isDebugEnabled()) {
//		context.debug(this.context.getId()+ " Discarding stream packet. No session for " + streamSessionId);
//	    }
	    return;
	}

	String chatKey = sc.getStreamInfo().getAppTopicId();

	/* use app topic to identify the chat */
	Chat chat = getChatByKey(chatKey, false);

	if (chat == null) {
	    /* not valid. discard */
	    return;
	}

	Set<String> partnersSnapshot = chat.getLocalParticipants();

	String senderId = event.from();

	RemoteChatPeerData remotePeerData = chat.getRemoteParticipant(event.getLastRelay());

	ChatPeerData peerData;

	if (remotePeerData != null) {
	    /* this is a remote peer message */
	    peerData = remotePeerData.getPeerData();

	} else {
	    /* direct peer message */

	    peerData = chat.getParticipants().get(senderId);
	    if (peerData == null) {
		return;
	    }

//	    /* don't sent the event to the sender */
	    event.addNoForward(senderId);

	}

	/* store first chunk of stream data */
	StreamData streamData = peerData.getStreamData();
	if (streamData != null && streamData.getStartData() == null) {
	    streamData.setStartData(data.getData());
	}

//	if (context.isDebugEnabled()) {
//	    context.debug("forwarding chat message to local partners" + partnersSnapshot);
//	}
	/* forward the message to local participants */
	context.forwardTo(event, partnersSnapshot);

	if (remotePeerData != null) {
	    /* this is a remote peer message, add no forward for the receiving gateway */
	    event.addNoForward(remotePeerData.getGatewayPeerId());

	}
	/* forward message to remote participants */
	context.forwardTo(event, chat.getRemotePeersGateways());

    }

    private void handlePeerStreamStarted(CustomEventContext<PeerStreamStartedEvent> c) {
	PeerStreamStartedEvent event = c.getEvent();
	EventHeader header = event.getHeader();
	PeerStreamData peerStreamData = event.getData();
	String sourcePeerId = peerStreamData.getSourcePeerId();
	StreamData streamData = peerStreamData.getStreamData();
	String streamSessionId = streamData.getStreamSessionId();

	String channel = header.getChannel();

	String chatPeerId = sourcePeerId;
	String gatewayPeerId = null;

	if (LocalAgentEventNodeContext.LOCAL.equals(channel)) {
	    if (!streamsManager.isStreamSessionValid(streamSessionId)) {
		return;
	    }

	}
	/* from a direct peer */
	else if (header.getRelayedBy().size() == 1) {
	    gatewayPeerId = event.getLastRelay();
	    /* get local id for remote peer */
	    String localId = context.getIdForRemotePeerByPath(gatewayPeerId, sourcePeerId);
	    if (localId == null) {
		return;
	    }
	    chatPeerId = localId;

	}

	/* use the topic as the chat key */
	String chatKey = streamData.getStreamInfo().getAppTopicId();
	if (chatKey == null) {
	    return;
	}

	Chat chat = getChatByKey(chatKey, false);

	if (chat == null) {
	    return;
	}

	/* add stream data for the peer */
	ChatPeerData chatPeerData = chat.getParticipants().get(chatPeerId);
	if (chatPeerData == null) {
	    return;
	}
	chatPeerData.setStreamData(streamData);

	PeerStreamStartedEvent globalPeerStreamStartedEvent = event;

	if (gatewayPeerId != null) {
	    /* if this is a remote event, create a new peer strea started event with our local id for the remote peer */
	    globalPeerStreamStartedEvent = new PeerStreamStartedEvent(new PeerStreamData(chatPeerId, streamData));

	    /* don't send back to the gateway */
	    globalPeerStreamStartedEvent.addNoForward(gatewayPeerId);
	    /* associate this stream session to the remote peer */
	    streamsManager.addRemotePeerStream(chatPeerId, streamData);
	}
	/* forward to gateway peers */
	context.forwardTo(globalPeerStreamStartedEvent, chat.getRemotePeersGateways());

	if (gatewayPeerId == null) {
	    event.addNoForward(sourcePeerId);
	}

	/* forward to local participants */
	context.forwardTo(event, chat.getLocalParticipants());

    }

    private Map<String, ChatInitData> getActiveChatsData() {
	Map<String, ChatInitData> activeChatsMap = new HashMap<>();
	for (String chatKey : chats.keySet()) {
	    Chat c = getChatByKey(chatKey);
	    Map<String, ChatPeerData> cp = c.getParticipants();
	    activeChatsMap.put(chatKey, new ChatInitData(chatKey, new ArrayList(cp.values()),c.getConfig()));
	}
	return activeChatsMap;
    }

    private void removeRemotePeer(CustomEventContext<ChatLeftEvent> c) {
	ChatLeftEvent event = c.getEvent();
	ChatPeerData data = event.getData();

	/* get local id for remote peer */
	String localId = context.getIdForRemotePeerByPath(event.from(), data.getPeerId());
	if (localId != null) {
	    removePeerFromChat(data.getChatKey(), localId, true, event);
	}

    }

    private String buildLocalIdForRemotePeer(String gatewayPeerId, String remotePeerId) {
	return gatewayPeerId + ":" + remotePeerId;
    }

    private void addRemoteChatPeer(String gatewayPeerId, ChatPeerData peerData, Event triggerEvent) {
	String chatKey = peerData.getChatKey();
	String alias = peerData.getAlias();

	Chat chatByKey = getChatByKey(chatKey, false);
	if (chatByKey != null && chatByKey.hasPeerWithAlias(alias)) {
	    if (context.isDebugEnabled()) {
		context.debug(
			"Discarding peer join on chat " + chatKey + " for alias " + alias + ". Alias already taken.");
	    }
	    /* discard this request */
	    return;

	}

	/* add a remote peer if none exists */
	String localPeerId = context.addRemotePeer(gatewayPeerId, new PeerInfo(peerData.getPeerId(), "", null),
		triggerEvent);
//	String localPeerId = buildLocalIdForRemotePeer(gatewayPeerId, peerData.getPeerId());

	/* create a peer data with the local peer id */
	ChatPeerData localPeerData = new ChatPeerData(chatKey, localPeerId, false, alias);

	ChatJoinedEvent chatJoinedEvent = new ChatJoinedEvent(localPeerData);
	/* notify the current local participants of the new peer */
	context.forwardTo(chatJoinedEvent, getChatByKey(chatKey).getLocalParticipants());

	/* add the peer to the chat local view, specifying the source o the event as a gateway peer */
	getChatByKey(chatKey).addRemoteParicipant(new RemoteChatPeerData(localPeerData, gatewayPeerId));
	chatsForGateways.add(gatewayPeerId, chatKey);

	/* post chat joined event for the remote peer that we added */
	ChatJoinedEvent globalChatJoinedEvent = new ChatJoinedEvent(localPeerData);
	/* make sure this message is not sent to the gateway */
	globalChatJoinedEvent.addNoForward(gatewayPeerId);
	triggerEvent.setAsCauseFor(globalChatJoinedEvent);
	context.postEvent(globalChatJoinedEvent);
    }

    private void addRemoteChatPeer(CustomEventContext<ChatJoinedEvent> c) {
	ChatJoinedEvent event = c.getEvent();

	ChatPeerData peerData = event.getData();

	/* add a remote peer if none exists */
	String gatewayPeerId = event.from();
	addRemoteChatPeer(gatewayPeerId, peerData, event);

    }

    private List<String> setCopy(Collection<String> source) {
	return new ArrayList((Collection<String>) ((HashSet<String>) source).clone());
    }

    private List<ChatPeerData> createParticipantsCopy(Collection<ChatPeerData> source) {
	return new ArrayList<ChatPeerData>(source);
    }

    /**
     * Remove a peer from chat
     * 
     * @param chatKey
     * @param peerId
     * @param cleanUp
     * @return - true if the chat is empty ( has no more participants ) false otherwise
     */
    private boolean removePeerFromChat(String chatKey, String peerId, boolean cleanUp, Event causeEvent) {
	Chat chat = getChatByKey(chatKey, false);
	if (chat == null) {
	    return false;
	}

	boolean peerRemoved = false;

	RemoteChatPeerData remotePeer = chat.removeRemoteParticipant(peerId);
	String gatewayPeerId = null;
	if (remotePeer != null) {
	    gatewayPeerId = remotePeer.getGatewayPeerId();
	    /* unregister from this remote peer */
	    context.unregisterFromPeer(peerId);
	    peerRemoved = true;
	} else {
	    ChatPeerData removedParticipant = chat.removeLocalParticipant(peerId);
	    if (removedParticipant != null) {
		peerRemoved = true;
	    }
	}

	if (!peerRemoved) {
	    context.debug("Peer not valid " + peerId);
	    /* if this wasn't a valid participant just return */
	    return false;
	}

	Set<String> ccp = chat.getLocalParticipants();

	boolean removed = false;
	if (ccp.size() > 0) {
	    /* notify local chat partners that the peer left */
	    ChatLeftEvent chatLeftEvent = new ChatLeftEvent(chatKey, peerId);
	    context.forwardTo(chatLeftEvent, ccp);
	} else {
	    if (cleanUp && chat.getParticipants().size() == 0) {
		/* this chat has no participants, remove it */
		removeChat(chatKey);
		removed = true;
	    }
	}

	/* post a global chat left event */
	ChatLeftEvent globalChatLeftEvent = new ChatLeftEvent(chatKey, peerId);
	if (causeEvent != null) {
	    causeEvent.setAsCauseFor(globalChatLeftEvent);
	}

	if (gatewayPeerId != null) {
	    /* if this is a remote peer, make sure we don't send the event back to the gateway */
	    globalChatLeftEvent.addNoForward(gatewayPeerId);
	}
	context.postEvent(globalChatLeftEvent);
	return removed;
    }

    private void removePeerFromAllChats(String peerId) {
	if (context.isDebugEnabled()) {
	    context.debug("Removing peer " + peerId + " from chats.");
	}
	Set<String> chatsToRemove = new HashSet<>();
	chats.keySet().forEach((chatkey) -> {
	    boolean toRemove = removePeerFromChat(chatkey, peerId, false, null);
	    if (toRemove) {
		chatsToRemove.add(chatkey);
	    }
	});

	/* remove unused chats */
	chatsToRemove.forEach((chatKey) -> {
	    removeChat(chatKey);
	});
    }

    private void removePeersForGateway(String gatewayPeerId, Event causeEvent) {
	Set<String> gwChats = chatsForGateways.remove(gatewayPeerId);
	if (gwChats == null) {
	    return;
	}
	context.getLogger().debug("Removing remote peers for gateway " + gatewayPeerId);
	gwChats.forEach((chatKey) -> {
	    Chat c = getChatByKey(chatKey, false);
	    if (c != null) {
		Set<String> peersForGateway = c.getPeersForGateway(gatewayPeerId);
		if (peersForGateway != null) {
		    HashSet<String> toRemove = new HashSet<>(peersForGateway);
		    toRemove.forEach((rid) -> {
			removePeerFromChat(chatKey, rid, true, causeEvent);
		    });
		}
	    }
	});
    }

    private Collection<ChatPeerData> getChatParticipants(String chatKey) {
	return getChatByKey(chatKey).getParticipants().values();
    }

    private Chat getChatByKey(String chatKey, boolean create) {
	Chat chat = chats.get(chatKey);
	if (chat == null && create) {
	    chat = createChatForKey(chatKey);
	    chats.put(chatKey, chat);
	}
	return chat;
    }

    private Chat getChatByKey(String chatKey) {
	return getChatByKey(chatKey, true);
    }

    private Chat createChatForKey(String chatKey) {
	context.logDebug("Creating chat for key "+chatKey);
	Chat newChat = new Chat(chatKey);
	Map<String, ChatConfig> configsForChats = config.getConfigsForChats();
	if (configsForChats != null) {
	    /* if there's a predefined configuration for this chat, use that */
	    ChatConfig chatConfig = configsForChats.get(chatKey);
	    if (chatConfig != null) {
		newChat.setConfig(chatConfig);
	    }
	    else {
		/* copy default config */
		ChatConfig ncc = newChat.getConfig();
		ChatConfig dcc = config.getDefaultChatConfig();
		ncc.setMaxRoomCapacity(dcc.getMaxRoomCapacity());
		ncc.setMaxStreamsAllowed(dcc.getMaxStreamsAllowed());
		ncc.setStreamingAllowed(dcc.isStreamingAllowed());
	    }
	}
	return newChat;
    }

    private Chat removeChat(String chatKey) {
	context.logDebug("Removing chat for key "+chatKey);
	return chats.remove(chatKey);
    }

    @Override
    public void terminate() {
	// TODO Auto-generated method stub

    }

    @Override
    protected void config() {
	// TODO Auto-generated method stub

    }

}
