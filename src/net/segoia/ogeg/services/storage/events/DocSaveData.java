package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;
import net.segoia.util.data.storage.StorageEntity;

@EventDataType(@EventType(value = "DOC:SAVE:EVENT", className = "DocSaveEvent"))
public class DocSaveData {
    private String parentStorageId;
    private String docKey;
    private StorageEntity entityToSave;

    public DocSaveData() {
	super();
    }

    public DocSaveData(String parentStorageId, String docKey, StorageEntity entityToSave) {
	super();
	this.parentStorageId = parentStorageId;
	this.docKey = docKey;
	this.entityToSave = entityToSave;
    }

    public String getParentStorageId() {
	return parentStorageId;
    }

    public void setParentStorageId(String parentStorageId) {
	this.parentStorageId = parentStorageId;
    }

    public String getDocKey() {
	return docKey;
    }

    public void setDocKey(String docKey) {
	this.docKey = docKey;
    }

    public StorageEntity getEntityToSave() {
	return entityToSave;
    }

    public void setEntityToSave(StorageEntity entityToSave) {
	this.entityToSave = entityToSave;
    }

}
