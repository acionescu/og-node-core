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
package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;

@EventDataType(@EventType(value = "STORAGE:RENAME:ENTITY", className = "StorageRenameEntityRequestEvent"))
public class StorageRenameEntityRequest {
    private String sourcePath;
    private String newName;

    public StorageRenameEntityRequest() {
	super();
	// TODO Auto-generated constructor stub
    }

    public StorageRenameEntityRequest(String sourcePath, String newName) {
	super();
	this.sourcePath = sourcePath;
	this.newName = newName;
    }

    public String getSourcePath() {
	return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
	this.sourcePath = sourcePath;
    }

    public String getNewName() {
	return newName;
    }

    public void setNewName(String newName) {
	this.newName = newName;
    }

}
