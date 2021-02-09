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

import net.segoia.util.data.storage.DataStore;

public class DataStoreContext {
    private DataStore dataStore;

    /* relative path of this store to the main store */
    private String relativePath;

    public DataStoreContext() {
	super();
	// TODO Auto-generated constructor stub
    }

    public DataStoreContext(DataStore dataStore) {
	super();
	this.dataStore = dataStore;
    }

    public String getRelativePath() {
	return relativePath;
    }

    public void setRelativePath(String relativePath) {
	this.relativePath = relativePath;
    }

    public DataStore getDataStore() {
	return dataStore;
    }

    public void setDataStore(DataStore dataStore) {
	this.dataStore = dataStore;
    }

}
