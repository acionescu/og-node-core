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

public class ChatErrors {
    public static final ChatError JOIN_REJECTED=new ChatError(10, "Chat join rejected");
    public static final ChatError CHAT_ROOMS_CAPACITY_REACHED=new ChatError(20, "No more chat rooms available. Try again later.");
    public static final ChatError ROOM_CAPACITY_REACHED=new ChatError(30, "The room is full. Try again later.");
}
