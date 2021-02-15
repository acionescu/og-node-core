package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;

@EventDataType(@EventType(value = "STORAGE:RENAME:ENTITY", className = "StorageRenameEntityRequestEvent"))
public class StorageRenameEntityRequest {
    private String sourcePath;
    private String newName;

    public StorageRenameEntityRequest() {
	super();
	// TODO Auto-generated constructor stub
    }

    public StorageRenameEntityRequest(String sourcePath, String newName) {
	super();
	this.sourcePath = sourcePath;
	this.newName = newName;
    }

    public String getSourcePath() {
	return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
	this.sourcePath = sourcePath;
    }

    public String getNewName() {
	return newName;
    }

    public void setNewName(String newName) {
	this.newName = newName;
    }

}
