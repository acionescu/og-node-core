package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;

@EventDataType(@EventType(value = "DOC:START:EDIT", className = "StorageStartDocEditRequestEvent"))
public class StorageStartDocEditRequest {
    /**
     * The path of the document to edit
     */
    private String path;

    public StorageStartDocEditRequest() {
	super();
    }

    public StorageStartDocEditRequest(String path) {
	super();
	this.path = path;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }
}
