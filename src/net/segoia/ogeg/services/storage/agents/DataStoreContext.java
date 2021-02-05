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
