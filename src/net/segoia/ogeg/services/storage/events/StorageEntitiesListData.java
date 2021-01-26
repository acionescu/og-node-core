package net.segoia.ogeg.services.storage.events;

import java.util.List;

import net.segoia.util.data.storage.StorageEntity;

public class StorageEntitiesListData {
    /**
     * The path of the storage listed
     */
    private String path;
    private List<StorageEntity> entities;
    /**
     * The offset specified in the request
     */
    private int offset;
    /**
     * The total number of entities in the storage at the specified path
     */
    private int totalCount;

    public StorageEntitiesListData() {
	super();
	// TODO Auto-generated constructor stub
    }

    public StorageEntitiesListData(String path, List<StorageEntity> entities, int totalCount) {
	super();
	this.path = path;
	this.entities = entities;
	this.totalCount = totalCount;
    }

    public StorageEntitiesListData(String path, List<StorageEntity> entities, int totalCount, int offset) {
	super();
	this.path = path;
	this.entities = entities;
	this.totalCount = totalCount;
	this.offset = offset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<StorageEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<StorageEntity> entities) {
        this.entities = entities;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
