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

import java.util.Arrays;
import java.util.List;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;

@EventDataType(@EventType(value="STORAGE:MOVE:ENTITIES", className="StorageMoveEntitiesRequestEvent"))
public class StorageMoveEntitiesRequest {
    private List<String> sourcePaths;
    private String destinationPath;

    public StorageMoveEntitiesRequest(List<String> sourcePaths, String destinationPath) {
	super();
	this.sourcePaths = sourcePaths;
	this.destinationPath = destinationPath;
    }

    public StorageMoveEntitiesRequest(String sourcePaths, String destinationPath) {
	super();
	this.sourcePaths = Arrays.asList(new String[] { sourcePaths });
	this.destinationPath = destinationPath;
    }

    public StorageMoveEntitiesRequest() {
	super();
	// TODO Auto-generated constructor stub
    }

    public List<String> getSourcePaths() {
	return sourcePaths;
    }

    public void setSourcePaths(List<String> sourcePaths) {
	this.sourcePaths = sourcePaths;
    }

    public String getDestinationPath() {
	return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
	this.destinationPath = destinationPath;
    }

}
