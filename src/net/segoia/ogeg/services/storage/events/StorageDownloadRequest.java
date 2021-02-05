package net.segoia.ogeg.services.storage.events;

import java.util.Arrays;
import java.util.List;

public class StorageDownloadRequest {
    /**
     * The paths of files to be downloaded
     */
    private List<String> paths;
    
    public StorageDownloadRequest(String... paths) {
	this(Arrays.asList(paths));
    }
    

    public StorageDownloadRequest(List<String> paths) {
	super();
	this.paths = paths;
    }

    public StorageDownloadRequest() {
	super();
	// TODO Auto-generated constructor stub
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
