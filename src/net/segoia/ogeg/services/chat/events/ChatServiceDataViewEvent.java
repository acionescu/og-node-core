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

import net.segoia.event.eventbus.CustomEvent;
import net.segoia.event.eventbus.EventType;

@EventType("PEER:CHAT:SERVICE_VIEW")
public class ChatServiceDataViewEvent extends CustomEvent<ChatServiceDataView>{
    public static final String ET="PEER:CHAT:SERVICE_VIEW";

    public ChatServiceDataViewEvent(ChatServiceDataView data) {
	super(ET, data);
    }
    
    public ChatServiceDataViewEvent() {
	super(ET);
    }
}
