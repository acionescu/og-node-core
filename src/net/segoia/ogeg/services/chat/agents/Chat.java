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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.segoia.ogeg.services.chat.events.ChatConfig;
import net.segoia.ogeg.services.chat.events.ChatPeerData;
import net.segoia.ogeg.services.chat.events.RemoteChatPeerData;
import net.segoia.util.data.SetMap;

public class Chat {
    private String chatKey;
    private ChatConfig config;
    
    private Map<String, ChatPeerData> participants = new LinkedHashMap<>();
    private Map<String, ChatPeerData> participantsByAlias=new HashMap<>();
    private Set<String> localParticipants = new HashSet<>();
    private Map<String, RemoteChatPeerData> remoteParticipants = new LinkedHashMap<>();

    private SetMap<String, String> remotePeersGateways = new SetMap<>();

    public Chat(String chatKey) {
	super();
	this.chatKey = chatKey;
    }

    public void addParticipant(ChatPeerData peerData) {
	String alias = peerData.getAlias();
	if(hasPeerWithAlias(alias)) {
	    throw new RuntimeException("Peer with alias "+alias+" already present o chat "+chatKey);
	}
	participantsByAlias.put(alias, peerData);
	participants.put(peerData.getPeerId(), peerData);
	
    }
    
    public boolean hasPeerWithAlias(String alias) {
	return participantsByAlias.containsKey(alias);
    }
    
    public ChatPeerData getParticipantByAlias(String alias) {
	return participantsByAlias.get(alias);
    }

    public void addLocalParicipant(ChatPeerData peerData) {
	addParticipant(peerData);
	localParticipants.add(peerData.getPeerId());
    }

    public void addRemoteParicipant(RemoteChatPeerData peerData) {
	ChatPeerData chatPeerData = peerData.getPeerData();
	addParticipant(chatPeerData);
	String peerId = chatPeerData.getPeerId();
	remoteParticipants.put(peerId, peerData);
	remotePeersGateways.add(peerData.getGatewayPeerId(), peerId);
    }
    
    public ChatPeerData removeParticipant(String peerId) {
	ChatPeerData peerData = participants.remove(peerId);
	if(peerData != null) {
	    participantsByAlias.remove(peerData.getAlias());
	}
	return peerData;
    }

    public ChatPeerData removeLocalParticipant(String peerId) {
	localParticipants.remove(peerId);
	return removeParticipant(peerId);
    }

    public RemoteChatPeerData removeRemoteParticipant(String peerId) {
	
	RemoteChatPeerData remotePeer = remoteParticipants.remove(peerId);
	if (remotePeer != null) {
	    remotePeersGateways.removeValueForKey(remotePeer.getGatewayPeerId(), peerId);
	    removeParticipant(peerId);
	}
	return remotePeer;
    }
    
    public Set<String> getPeersForGateway(String gatewayId){
	return remotePeersGateways.get(gatewayId);
    }
    
    public Set<String> getRemotePeersGateways(){
	return remotePeersGateways.keySet();
    }

    public RemoteChatPeerData getRemoteParticipant(String peerId) {
	return remoteParticipants.get(peerId);
    }

    /**
     * @return the chatKey
     */
    public String getChatKey() {
	return chatKey;
    }

    public Map<String, ChatPeerData> getParticipants() {
	return participants;
    }

    public void setParticipants(Map<String, ChatPeerData> participants) {
	this.participants = participants;
    }

    public Set<String> getLocalParticipants() {
	return localParticipants;
    }

    public void setLocalParticipants(Set<String> localParticipants) {
	this.localParticipants = localParticipants;
    }

    public Map<String, RemoteChatPeerData> getRemoteParticipants() {
	return remoteParticipants;
    }

    public void setRemoteParticipants(Map<String, RemoteChatPeerData> remoteParticipants) {
	this.remoteParticipants = remoteParticipants;
    }

    public ChatConfig getConfig() {
        return config;
    }

    public void setConfig(ChatConfig config) {
        this.config = config;
    }

}
