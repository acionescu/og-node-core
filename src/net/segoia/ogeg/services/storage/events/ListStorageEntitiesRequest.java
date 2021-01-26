package net.segoia.ogeg.services.storage.events;

public class ListStorageEntitiesRequest {
    /**
     * The path to list
     */
    private String path;

    /**
     * The index to start with
     */
    private int offset;

    /**
     * The maximum size of the response
     */
    private int limit;

    public ListStorageEntitiesRequest(String path) {
	super();
	this.path = path;
    }

    public ListStorageEntitiesRequest(String path, int offset, int limit) {
	super();
	this.path = path;
	this.offset = offset;
	this.limit = limit;
    }

    public ListStorageEntitiesRequest() {
	super();
	// TODO Auto-generated constructor stub
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public int getOffset() {
	return offset;
    }

    public void setOffset(int offset) {
	this.offset = offset;
    }

    public int getLimit() {
	return limit;
    }

    public void setLimit(int limit) {
	this.limit = limit;
    }
}
