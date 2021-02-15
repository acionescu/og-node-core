package net.segoia.ogeg.services.storage.events;

import java.util.Arrays;
import java.util.List;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;

@EventDataType(@EventType(value="STORAGE:MOVE:ENTITIES", className="StorageMoveEntitiesRequestEvent"))
public class StorageMoveEntitiesRequest {
    private List<String> sourcePaths;
    private String destinationPath;

    public StorageMoveEntitiesRequest(List<String> sourcePaths, String destinationPath) {
	super();
	this.sourcePaths = sourcePaths;
	this.destinationPath = destinationPath;
    }

    public StorageMoveEntitiesRequest(String sourcePaths, String destinationPath) {
	super();
	this.sourcePaths = Arrays.asList(new String[] { sourcePaths });
	this.destinationPath = destinationPath;
    }

    public StorageMoveEntitiesRequest() {
	super();
	// TODO Auto-generated constructor stub
    }

    public List<String> getSourcePaths() {
	return sourcePaths;
    }

    public void setSourcePaths(List<String> sourcePaths) {
	this.sourcePaths = sourcePaths;
    }

    public String getDestinationPath() {
	return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
	this.destinationPath = destinationPath;
    }

}
