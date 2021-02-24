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

import net.segoia.event.eventbus.peers.events.GenericResponseEvent;
import net.segoia.event.eventbus.peers.vo.ErrorData;
import net.segoia.event.eventbus.peers.vo.GenericErrorResponse;
import net.segoia.event.eventbus.peers.vo.GenericResponse;
import net.segoia.event.eventbus.peers.vo.GenericResponseData;
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
    
    
    
    /* upload */
    /* errors */
    
    public static final RejectionReason DATA_TOO_LARGE=new RejectionReason(6001, "Data is too large.");
    public static final RejectionReason STORAGE_ENTITY_EXISTS=new RejectionReason(6002, "Storage entity already exists");
    public static final RejectionReason STORAGE_ENTITY_KEY_MISSING=new RejectionReason(6003, "Storage entity key missing");
    public static final RejectionReason STORAGE_PATH_INVALID=new RejectionReason(6004, "Storage path invalid");
    public static final RejectionReason STORAGE_PATH_DEPTH_EXCEEDED=new RejectionReason(6005, "Storage path depth exceeded");
    public static final RejectionReason STORAGE_KEY_TOO_LONG=new RejectionReason(6006, "Storage key too long");
    
    /*success*/
    public static final GenericResponseData STORAGE_UPLOAD_SUCCEEDED=new GenericResponseData(6000, "Upload succeeded");
    
    public static final RejectionReason STORAGE_DOWNLOAD_PATH_MISSING=new RejectionReason(6007, "Storage download path is missing");
    public static final int STORAGE_DOWNLOAD_PATH_NOT_FOUND_CODE=6008;
    
    
    /* delete */
    
    public static final RejectionReason STORAGE_DELETE_PATH_MISSING=new RejectionReason(6021, "Storage delete path is missing");
    public static final RejectionReason STORAGE_DELETE_FORBIDDEN=new RejectionReason(6022, "Storage delete forbidden");
    public static final RejectionReason STORAGE_DELETE_FAILED=new RejectionReason(6023, "Storage delete failed");
    
    
    public static final GenericResponseData STORAGE_DELETE_SUCCEEDED=new GenericResponseData(6020, "Delete succeeded");
    
    public static final RejectionReason STORAGE_FOLDER_NAME_MISSING=new RejectionReason(6031, "Folder name missing");
    public static final RejectionReason STORAGE_FOLDER_CREATION_ERROR=new RejectionReason(6032, "Invalid folder name or path");
    
    public static final GenericResponseData STORAGE_FOLDER_CREATION_SUCCEEDED=new GenericResponseData(6030, "Folder created");
    
    
    /* move */
    
    public static final RejectionReason STORAGE_SOURCE_PATHS_MISSING=new RejectionReason(6041, "Storage source paths missing");
    public static final RejectionReason STORAGE_SOURCE_PATH_INVALID=new RejectionReason(6042, "Storage source path invalid");
    
    public static final RejectionReason STORAGE_DESTINATION_PATH_MISSING=new RejectionReason(6043, "Storage destination path missing");
    public static final RejectionReason STORAGE_DESTINATION_PATH_INVALID=new RejectionReason(6044, "Storage destination path invalid");
    public static final RejectionReason STORAGE_MOVE_FAILED=new RejectionReason(6045, "Storage move failed");
    public static final RejectionReason STORAGE_MOVE_FORBIDDEN=new RejectionReason(6046, "Storage move forbidden");
    public static final GenericResponseData STORAGE_MOVE_SUCCEEDED=new GenericResponseData(6040, "Move suceeded");
    
    
    /* rename */
    public static final GenericResponseData STORAGE_RENAME_SUCCEEDED=new GenericResponseData(6050, "Rename suceeded");
    public static final RejectionReason STORAGE_RENAME_FAILED=new RejectionReason(6051, "Rename failed");
    public static final RejectionReason STORAGE_RENAME_FORBIDDEN=new RejectionReason(6052, "Storage rename forbidden");
    
    /* doc edit */
    
    public static final RejectionReason DOC_START_EDIT_FAILED=new RejectionReason(6061, "Failed to open document for edit.");
    
    public static final StorageErrorEvent buildStorageErrorEvent(String eventType, ErrorData reason) {
	return new StorageErrorEvent(new GenericErrorResponse(eventType, reason));
    }
    
    public static final GenericResponseEvent buildStorageSuccessEvent(String eventType, GenericResponseData respData) {
   	return new GenericResponseEvent(new GenericResponse(eventType, respData));
       }
    
    public static final StorageErrorEvent buildPathInvalidErrorEvent(String eventType) {
	return new StorageErrorEvent(new GenericErrorResponse(eventType, STORAGE_PATH_INVALID));
    }

    public static final RejectionReason buildDownloadPathNotFoundErrorEvent(String path) {
	return new RejectionReason(STORAGE_DOWNLOAD_PATH_NOT_FOUND_CODE, "Path not found "+path);
    }
}
