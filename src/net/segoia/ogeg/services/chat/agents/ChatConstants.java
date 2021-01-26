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

import net.segoia.event.eventbus.peers.vo.RejectionReason;

public class ChatConstants {
    public static final String CHAT_APP_ID="CHAT";
//    public static final String APP_ID_PARAM="appId";
//    public static final String CHAT_KEY_PARAM="chatKey";
    
    
    /* stream rejection reasons */
//    public static final RejectionReason APP_ID_MISSING=new RejectionReason(101, "appId event param missing");
//    public static final RejectionReason APP_ID_UNKNOWN=new RejectionReason(102, "appId unknown");
    public static final RejectionReason CHAT_KEY_MISSING=new RejectionReason(103, "chatKey event param missing");
    public static final RejectionReason CHAT_KEY_UNKNOWN=new RejectionReason(104, "chatKey unknown");
    public static final RejectionReason INVALID_CHAT_USER=new RejectionReason(105, "peer is not present in chat");
    public static final RejectionReason STREAMING_NOT_ALLOWED=new RejectionReason(110, "Streaming is not allowed in this chat room.");
    public static final RejectionReason MAX_STREAMS_REACHED=new RejectionReason(120, "Maximum streams per room reached. Wait for a peer to stop broadcasting and try again");
    
    
}
