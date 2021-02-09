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
package net.segoia.ogeg.services.storage.agents;

import java.util.Map;

import net.segoia.util.data.storage.DataStoreMetadata;

public class StorageManagerAgentConfig {
    /**
     * The name of the root storage directory
     */
    private String storageDir = "ognodestorage";

    /**
     * Maximum allowed file size in bytes <br>
     * Defaults to 1GB
     */
    private int maxFileSize = 1024 * 1024 * 1024;
    
    /**
     * The maximum storage hierarchy depth ( how many nested directories ar allowed )
     */
    private int maxStorageDepth=10;
    
    /**
     * Maximum characters length of a key
     */
    private int maxKeyLength=100;

    /**
     * The main datastores that this agent manages
     */
    private Map<String, DataStoreMetadata> dataStores;

    public String getStorageDir() {
	return storageDir;
    }

    public void setStorageDir(String storageDir) {
	this.storageDir = storageDir;
    }

    public int getMaxFileSize() {
	return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
	this.maxFileSize = maxFileSize;
    }

    public Map<String, DataStoreMetadata> getDataStores() {
        return dataStores;
    }

    public void setDataStores(Map<String, DataStoreMetadata> dataStores) {
        this.dataStores = dataStores;
    }

    public int getMaxStorageDepth() {
        return maxStorageDepth;
    }

    public void setMaxStorageDepth(int maxStorageDepth) {
        this.maxStorageDepth = maxStorageDepth;
    }

    public int getMaxKeyLength() {
        return maxKeyLength;
    }

    public void setMaxKeyLength(int maxKeyLength) {
        this.maxKeyLength = maxKeyLength;
    }
    
    
}
