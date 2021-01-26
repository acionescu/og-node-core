package net.segoia.ogeg.services.storage.agents;

import net.segoia.event.eventbus.peers.vo.GenericErrorResponse;
import net.segoia.event.eventbus.peers.vo.RejectionReason;
import net.segoia.ogeg.services.storage.events.StorageErrorEvent;

public class StorageManagerConstants {
    public static final String STORAGE_APP_ID="STORAGE";
    
//    /**
//     * The id of the metadata storage - this doens't have to match the actual name of the storage
//     */
//    public static final String METADATA_STORE_ID="metadata";
//    /**
//     * Users data store id
//     */
//    public static final String USERS_DATA_STORE_ID="usersdata";
//    /**
//     * Local node data store id
//     */
//    public static final String LOCAL_DATA_STORE_ID="local";
    
    
    
    
    /* errors */
    
    public static final RejectionReason DATA_TOO_LARGE=new RejectionReason(6001, "Data is too large.");
    public static final RejectionReason STORAGE_ENTITY_EXISTS=new RejectionReason(6002, "Storage entity already exists");
    public static final RejectionReason STORAGE_ENTITY_KEY_MISSING=new RejectionReason(6003, "Storage entity key missing");
    public static final RejectionReason STORAGE_PATH_INVALID=new RejectionReason(6004, "Storage path invalid");
    public static final RejectionReason STORAGE_PATH_DEPTH_EXCEEDED=new RejectionReason(6005, "Storage path depth exceeded");
    public static final RejectionReason STORAGE_KEY_TOO_LONG=new RejectionReason(600, "Storage path depth exceeded");
    
    
    public static final StorageErrorEvent builPathInvalidErrorEvent(String eventType) {
	return new StorageErrorEvent(new GenericErrorResponse(eventType, STORAGE_PATH_INVALID));
    }

}
