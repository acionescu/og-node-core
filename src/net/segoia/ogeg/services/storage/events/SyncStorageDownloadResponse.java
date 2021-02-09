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

import java.io.InputStream;
import java.util.Map;

import net.segoia.event.eventbus.peers.vo.ErrorData;
import net.segoia.util.data.storage.StorageEntity;

/**
 * This is the synchronous response af a sync download response
 * 
 * @author adi
 *
 */
public class SyncStorageDownloadResponse {
    /**
     * Not null if an error occurred and the request cannot be fulfilled
     */
    private ErrorData error;
    /**
     * Not null if the request succeeded
     */
    private Map<String, StorageEntity> storageEntities;

    public SyncStorageDownloadResponse(Map<String, StorageEntity> storageEntities) {
	super();
	this.storageEntities = storageEntities;
    }

    public SyncStorageDownloadResponse(ErrorData error) {
	super();
	this.error = error;
    }

    public SyncStorageDownloadResponse() {
	super();
	// TODO Auto-generated constructor stub
    }

    public ErrorData getError() {
	return error;
    }

    public void setError(ErrorData error) {
	this.error = error;
    }

    public Map<String, StorageEntity> getStorageEntities() {
        return storageEntities;
    }

    public void setStorageEntities(Map<String, StorageEntity> storageEntities) {
        this.storageEntities = storageEntities;
    }

   
}
