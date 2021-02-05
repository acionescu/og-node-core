package net.segoia.ogeg.services.storage.events;

import java.util.List;

public class StorageEntitiesDeleteRequest {
    /**
     * The paths of files to be deleted
     */
    private List<String> paths;

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
    
    
}
