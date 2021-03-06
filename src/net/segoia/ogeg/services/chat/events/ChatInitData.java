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
package net.segoia.ogeg.services.chat.events;

import java.util.List;

public class ChatInitData extends ChatData{
    private List<ChatPeerData> participants;
    private ChatConfig chatConfig;
    
    public ChatInitData(String chatKey, List<ChatPeerData> participants) {
	super(chatKey);
	this.participants = participants;
    }
    
    public ChatInitData(String chatKey, List<ChatPeerData> participants, ChatConfig chatConfig) {
	super(chatKey);
	this.participants = participants;
	this.chatConfig = chatConfig;
    }

    public ChatInitData() {
	super();
	// TODO Auto-generated constructor stub
    }



    /**
     * @return the participants
     */
    public List<ChatPeerData> getParticipants() {
        return participants;
    }

    /**
     * @param participants the participants to set
     */
    public void setParticipants(List<ChatPeerData> participants) {
        this.participants = participants;
    }
    
    
}
