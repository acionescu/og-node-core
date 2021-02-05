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
