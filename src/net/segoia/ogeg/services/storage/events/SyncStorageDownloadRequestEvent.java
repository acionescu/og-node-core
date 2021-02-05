package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.CustomEvent;
import net.segoia.event.eventbus.EventType;

@EventType("STORAGE:REQUEST:SYNC_DOWNLOAD")
public class SyncStorageDownloadRequestEvent extends CustomEvent<StorageDownloadRequest> {
    public static final String ET = "STORAGE:REQUEST:SYNC_DOWNLOAD";

    public SyncStorageDownloadRequestEvent(StorageDownloadRequest data) {
	super(ET, data);
    }
    
    public SyncStorageDownloadRequestEvent() {
	super(ET);
    }

    @Override
    public StorageDownloadRequest getData() {
	// TODO Auto-generated method stub
	return super.getData();
    }

    @Override
    public void setData(StorageDownloadRequest data) {
	// TODO Auto-generated method stub
	super.setData(data);
    }

}
