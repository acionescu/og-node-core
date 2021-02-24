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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.segoia.util.data.storage.DataStore;
import net.segoia.util.data.storage.DataStoreMetadata;
import net.segoia.util.data.storage.DefaultDataStore;
import net.segoia.util.data.storage.FileStorage;
import net.segoia.util.data.storage.StorageException;

public class StorageManagerAgentContext {
    private StorageManagerAgentConfig agentConfig;

    private DataStore mainDataStore;

    /**
     * The main data stores that this agent manages
     */
    private Map<String, DataStore> dataStores;

    private Map<String, DataStoreContext> dataStoreContexts;
    
    /**
     * Documents that are currently being edited
     */
    private Map<String, DocEditContext> editingDocs=new HashMap<>();
    
    public StorageManagerAgentContext(StorageManagerAgentConfig agentConfig) {
	super();
	this.agentConfig = agentConfig;
	init();
    }

    /**
     * Initialize internal structure from the configuration
     */
    protected void init() {
	/* create a file storage at the specified root dir */
	FileStorage fileStorage = new FileStorage(agentConfig.getStorageDir());

	/* wrap it as a data store */
	mainDataStore = new DefaultDataStore(fileStorage);

	Map<String, DataStoreMetadata> definedDatastores = agentConfig.getDataStores();

	/* create actual datastores from the defined ones */
	if (definedDatastores == null) {
	    return;
	}

	dataStores = new HashMap<>();
	dataStoreContexts = new HashMap<>();

	definedDatastores.forEach((k, v) -> {
	    /* create a datastore under the root datastore */
	    DefaultDataStore ds;
	    try {
		ds = new DefaultDataStore(fileStorage.createStorage(v.getKey()));
	    } catch (StorageException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		return;
	    }
	    dataStores.put(k, ds);

	    DataStoreContext dataStoreContext = new DataStoreContext(ds);
	    try {
		dataStoreContext.setRelativePath(mainDataStore.getRelativePath(v.getKey()));
	    } catch (StorageException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    dataStoreContexts.put(k, dataStoreContext);

	    System.out.println("creating datastore " + v.getKey());
	});
    }
    
    public void addEditingDoc(String key, DocEditContext docContext) {
	editingDocs.put(key, docContext);
    }
    
    public DocEditContext getEditingDoc(String key) {
	return editingDocs.get(key);
    }
    
    public DocEditContext removeEditingDoc(String key) {
	return editingDocs.remove(key);
    }
    
    
    public DocEditPeerContext removePeerFromDoc(String peerId, String docKey) {
	DocEditContext docEditContext = editingDocs.get(docKey);
	if(docEditContext != null) {
	    return docEditContext.removePeer(peerId);
	}
	return null;
    }
    
    public Collection<String> removePeerFromAllDocs(String peerId) {
	Set<String> removedFrom = new HashSet<>();
	for(String docKey : editingDocs.keySet()) {
	    DocEditContext docEditContext = editingDocs.get(docKey);
	    if(docEditContext.hasPeer(peerId)) {
		
		    /* just remove the peer */
		    docEditContext.removePeer(peerId);
		    removedFrom.add(docKey);
	    }
	}
	return removedFrom;
    }

    public DataStore getDataStoreForId(String id) {
	return dataStores.get(id);
    }

    public DataStoreMetadata getDataStoreMetadata(String id) {
	return agentConfig.getDataStores().get(id);
    }

    public StorageManagerAgentConfig getAgentConfig() {
	return agentConfig;
    }

    public void setAgentConfig(StorageManagerAgentConfig agentConfig) {
	this.agentConfig = agentConfig;
    }

    public Map<String, DataStore> getDataStores() {
	return dataStores;
    }

    public void setDataStores(Map<String, DataStore> dataStores) {
	this.dataStores = dataStores;
    }

    public DataStore getMainDataStore() {
	return mainDataStore;
    }

    public Map<String, DataStoreContext> getDataStoreContexts() {
	return dataStoreContexts;
    }

    public Map<String, DocEditContext> getEditingDocs() {
        return editingDocs;
    }
    
    

}
