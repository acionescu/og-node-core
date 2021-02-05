package net.segoia.ogeg.services.storage.agents;

import java.util.HashMap;
import java.util.Map;

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
	    DefaultDataStore ds = new DefaultDataStore(fileStorage.createStorage(v.getKey()));
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

}
