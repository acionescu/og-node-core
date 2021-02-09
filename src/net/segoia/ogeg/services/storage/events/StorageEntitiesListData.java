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

import java.util.List;

import net.segoia.util.data.storage.StorageEntityInfo;

public class StorageEntitiesListData {
    /**
     * The path of the storage listed
     */
    private String path;
    private List<StorageEntityInfo> entities;
    /**
     * The offset specified in the request
     */
    private int offset;
    /**
     * The total number of entities in the storage at the specified path
     */
    private int totalCount;

    public StorageEntitiesListData() {
	super();
	// TODO Auto-generated constructor stub
    }

    public StorageEntitiesListData(String path, List<StorageEntityInfo> entities, int totalCount) {
	super();
	this.path = path;
	this.entities = entities;
	this.totalCount = totalCount;
    }

    public StorageEntitiesListData(String path, List<StorageEntityInfo> entities, int totalCount, int offset) {
	super();
	this.path = path;
	this.entities = entities;
	this.totalCount = totalCount;
	this.offset = offset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<StorageEntityInfo> getEntities() {
        return entities;
    }

    public void setEntities(List<StorageEntityInfo> entities) {
        this.entities = entities;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
