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
package net.segoia.ogeg.services.core.agents;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.segoia.event.conditions.Condition;
import net.segoia.event.conditions.StrictChannelMatchCondition;
import net.segoia.event.conditions.TrueCondition;
import net.segoia.event.eventbus.CustomEventContext;
import net.segoia.event.eventbus.Event;
import net.segoia.event.eventbus.EventsRepository;
import net.segoia.event.eventbus.FilteringEventBus;
import net.segoia.event.eventbus.peers.GlobalEventNodeAgent;
import net.segoia.event.eventbus.peers.LocalAgentEventNodeContext;
import net.segoia.event.eventbus.peers.core.EventTransceiver;
import net.segoia.event.eventbus.peers.events.NewPeerEvent;
import net.segoia.event.eventbus.peers.events.PeerLeftEvent;
import net.segoia.event.eventbus.peers.vo.NodeInfo;
import net.segoia.event.eventbus.peers.vo.PeerInfo;
import net.segoia.event.eventbus.peers.vo.bind.ConnectToPeerRequest;
import net.segoia.event.eventbus.services.EventNodeServiceContext;
import net.segoia.event.eventbus.services.EventNodeServicesManager;
import net.segoia.event.eventbus.vo.services.EventNodePublicServiceDesc;
import net.segoia.event.eventbus.vo.services.EventNodeServiceDefinition;
import net.segoia.eventbus.web.ws.v0.WsClientEndpointTransceiver;
import net.segoia.ogeg.node.NodeTypes;
import net.segoia.ogeg.node.vo.core.PeerInteropContext;
import net.segoia.ogeg.node.vo.core.PeerNodeConfig;
import net.segoia.ogeg.node.vo.core.PeerNodeContext;
import net.segoia.ogeg.node.vo.core.PeerNodeDef;
import net.segoia.ogeg.node.vo.core.PeerNodeSettings;
import net.segoia.ogeg.services.chat.events.ChatJoinedEvent;
import net.segoia.ogeg.services.chat.events.ChatLeftEvent;
import net.segoia.ogeg.services.core.events.ServiceNodeData;
import net.segoia.ogeg.services.core.events.ServiceNodeDataEvent;
import net.segoia.ogeg.services.core.events.SyncWithServiceNodeEvent;


/**
 * An agent to handle this node's interoperability with other nodes
 * @author adi
 *
 */
public class NodeInteroperabilityAgent extends GlobalEventNodeAgent {
    public static final String AGENT_ID = "INTEROP";
    private NodeInteroperabilityConfig config;

    /**
     * The nodes to connect to
     */
    private Map<String, PeerNodeContext> upstreamNodes = new HashMap<>();

    /**
     * Peers that act as service providers and/or consumers
     */
    private Map<String, PeerInteropContext> interopPeers = new HashMap<>();

    @Override
    protected void agentInit() {
	EventsRepository.getInstance().load();
	if (config == null) {
	    return;
	}

	List<PeerNodeConfig> upstreamNodes = config.getUpstreamNodes();

	if (upstreamNodes != null && !upstreamNodes.isEmpty()) {
	    /* connect to defined upstream nodes */
	    for (PeerNodeConfig nc : upstreamNodes) {
		addUpstreamNode(nc);
	    }
	}
    }

    @Override
    protected void config() {

    }

    private boolean allowConsumer(CustomEventContext<? extends Event> c) {
	return config.getServiceConsumerCondition().test(c);
    }

    private boolean allowProvider(CustomEventContext<? extends Event> c) {
	return config.getServiceProviderCondition().test(c);
    }

    private void addUpstreamNode(PeerNodeConfig peerConfig) {
	try {
	    PeerNodeDef nodeDef = peerConfig.getNodeDef();
	    WsClientEndpointTransceiver clientEndpoint = new WsClientEndpointTransceiver(new URI(nodeDef.getUri()),
		    nodeDef.getChannel());
	    PeerNodeContext peerNodeContext = new PeerNodeContext(peerConfig, clientEndpoint);
	    upstreamNodes.put(peerConfig.getId(), peerNodeContext);

	    /* do further handling of this new peer node */
	    handleNewUpstreamNode(peerNodeContext);

	} catch (URISyntaxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void handleNewUpstreamNode(PeerNodeContext peerNodeContext) {
	PeerNodeConfig peerConfig = peerNodeContext.getPeerConfig();
	PeerNodeSettings nodeSettings = peerConfig.getNodeSettings();
	if (nodeSettings != null && nodeSettings.isAutoConnect()) {
	    context.getLogger().info(AGENT_ID + ": connecting to node " + peerConfig.getId());
	    connectToNode(peerNodeContext.getClientEndpoint(), peerConfig.getId());
	}
    }

    private void connectToNode(EventTransceiver transceiver, String peerAlias) {
	context.registerToPeer(new ConnectToPeerRequest(transceiver, peerAlias));
    }

    @Override
    protected void registerHandlers() {

	/* we're only going to allow interoperability on secure channels */
	Condition secureChannelCond = new StrictChannelMatchCondition("WSS_V1");

	FilteringEventBus secureEventBus = context.getEventBusForCondition(secureChannelCond);

	secureEventBus.addEventHandler(NewPeerEvent.class, (c) -> {
	    NewPeerEvent event = c.getEvent();
	    PeerInfo data = event.getData();
	    String peerId = data.getPeerId();

	    NodeInfo nodeInfo = data.getNodeInfo();
	    if (nodeInfo != null && NodeTypes.SERVICE_NODE.equals(nodeInfo.getNodeType())) {
		boolean allowConsumer = allowConsumer(c);
		boolean allowProducer = allowProvider(c);

		if (!interopPeers.containsKey(peerId) && (allowConsumer || allowProducer)) {
		    context.getLogger().info("New service peer " + peerId);
		    interopPeers.put(peerId, new PeerInteropContext());

		    /* allow event forwarding */
		    context.getPeerManager(peerId).getConfig().setEventsForwardingCondition(new TrueCondition());

		    if (allowConsumer) {
			/* advertise the services we're offering */

			List<EventNodePublicServiceDesc> allowedServices = new ArrayList<>();

			EventNodeServicesManager servicesManager = context.getServicesManager();
			for (EventNodeServiceContext sCon : servicesManager.getServices().values()) {
			    EventNodeServiceDefinition serviceDef = sCon.getServiceDef();

			    if (serviceDef.getConsumerCondition().test(c)) {
				/* add only services that this peer is allowed to access */
				EventNodePublicServiceDesc serviceDesc = serviceDef.getServiceDesc();
				allowedServices.add(serviceDesc);
			    }

			}
			if (allowedServices.size() > 0) {
			    /* send a service node data event only if the peer is able to access at least one service */
			    ServiceNodeData serviceNodeData = new ServiceNodeData(allowedServices);
			    context.forwardTo(new ServiceNodeDataEvent(serviceNodeData), peerId);
			}
		    }
		}
	    }
	});

	secureEventBus.addEventHandler(PeerLeftEvent.class, (c) -> {
	    PeerLeftEvent event = c.getEvent();
	    PeerInfo data = event.getData();
	    String peerId = data.getPeerId();
	    interopPeers.remove(peerId);
	    String peerAlias = data.getAlias();
	    context.logDebug("Interop peer left " + peerAlias + " -> " + peerId);

	    if (peerAlias != null) {
		PeerNodeContext peerNodeContext = upstreamNodes.get(peerAlias);
		PeerNodeSettings nodeSettings = peerNodeContext.getPeerConfig().getNodeSettings();
		if (peerNodeContext != null && nodeSettings.isAutoReconnect()) {
		    context.logDebug("Reconnecting to peer " + peerAlias);
		    connectToNode(peerNodeContext.getClientEndpoint(), peerAlias);
		}
	    }
	});

	secureEventBus.addEventHandler(ServiceNodeDataEvent.class, (c) -> {
	    /* listen for services advertised by peers */
	    ServiceNodeDataEvent event = c.getEvent();
	    PeerInteropContext peerInteropContext = interopPeers.get(event.from());
	    if (peerInteropContext != null) {
		ServiceNodeData data = event.getData();
		peerInteropContext.setNodeServiceData(data);
		if (context.isDebugEnabled()) {
		    context.logDebug("Processing service node data " + event.toJson());
		}

		if (allowProvider(c)) {
		    context.debug("post sync with service node event");
		    /* if this provider is allowed, generate a local node sync event */
		    SyncWithServiceNodeEvent syncEvent = new SyncWithServiceNodeEvent(data);
		    /* set service node event as cause for the sync event */
		    event.setAsCauseFor(syncEvent);		    
		    context.postEvent(syncEvent);
		} else if (context.isDebugEnabled()) {
		    context.debug(
			    "Rejecting service node data due to condition " + config.getServiceProviderCondition());
		}
	    }

	});

//	/* chat stuff */
//	context.addEventHandler(ChatJoinedEvent.class, (c) -> {
//	    ChatJoinedEvent event = c.getEvent();
//	    String lastRelay = event.getLastRelay();
//
//	    if (!LocalAgentEventNodeContext.LOCAL.equals(event.getHeader().getChannel())) {
//		return;
//	    }
//
//	    for (String peerId : interopPeers.keySet()) {
//		/* check that we didn't get this event via one of the peers */
//		if (peerId.equals(lastRelay)) {
//		    continue;
//		}
//
//		/* check that we don't have a noForward for this peer */
//		if (!event.isNoForward(peerId)) {
//		    context.getLogger().debug("forwarding chat joined " + event.toJson());
//		    /* forward this event to other peers */
//		    context.forwardTo(event, peerId);
//		}
//	    }
//	});
//
//	context.addEventHandler(ChatLeftEvent.class, (c) -> {
//	    ChatLeftEvent event = c.getEvent();
//	    String lastRelay = event.getLastRelay();
//
//	    if (!LocalAgentEventNodeContext.LOCAL.equals(event.getHeader().getChannel())) {
//		return;
//	    }
//
//	    for (String peerId : interopPeers.keySet()) {
//		/* check that we didn't get this event via one of the peers */
//		if (peerId.equals(lastRelay)) {
//		    continue;
//		}
//
//		/* check that we don't have a noForward for this peer */
//		if (!event.isNoForward(peerId)) {
//		    context.getLogger().debug("forwarding chat left " + event.toJson());
//		    /* forward this event to other peers */
//		    context.forwardTo(event, peerId);
//		}
//	    }
//	});
    }

    @Override
    public void terminate() {
	// TODO Auto-generated method stub

    }

}
