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
import java.util.Map;

import net.segoia.event.eventbus.streaming.StreamsManagerConfig;
import net.segoia.ogeg.services.chat.events.ChatConfig;

public class ChatManagerAgentConfig {
    private StreamsManagerConfig streamsManagerConfig=new StreamsManagerConfig();
    /**
     * Maximum allowed chat rooms
     */
    private int maxAllowedRooms=20;
    
    private ChatConfig defaultChatConfig=new ChatConfig();
    
    /**
     * Predefined chats and their configs
     */
    private Map<String,ChatConfig> configsForChats=new HashMap<>();

    public StreamsManagerConfig getStreamsManagerConfig() {
	return streamsManagerConfig;
    }

    public void setStreamsManagerConfig(StreamsManagerConfig streamsManagerConfig) {
	this.streamsManagerConfig = streamsManagerConfig;
    }

    public int getMaxAllowedRooms() {
        return maxAllowedRooms;
    }

    public void setMaxAllowedRooms(int maxAllowedRooms) {
        this.maxAllowedRooms = maxAllowedRooms;
    }

    public Map<String, ChatConfig> getConfigsForChats() {
        return configsForChats;
    }

    public void setConfigsForChats(Map<String, ChatConfig> configsForChats) {
        this.configsForChats = configsForChats;
    }

    public ChatConfig getDefaultChatConfig() {
        return defaultChatConfig;
    }

    public void setDefaultChatConfig(ChatConfig defaultChatConfig) {
        this.defaultChatConfig = defaultChatConfig;
    }
    
}
