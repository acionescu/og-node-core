package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.CustomEvent;
import net.segoia.event.eventbus.EventType;

@EventType("STORAGE:DATA:ENTITIES_LIST")
public class StorageEntitiesListDataEvent extends CustomEvent<StorageEntitiesListData> {
    public static final String ET = "STORAGE:DATA:ENTITIES_LIST";

    public StorageEntitiesListDataEvent(StorageEntitiesListData data) {
	super(ET, data);
    }

    public StorageEntitiesListDataEvent() {
	super(ET);
    }

    @Override
    public StorageEntitiesListData getData() {
	// TODO Auto-generated method stub
	return super.getData();
    }

    @Override
    public void setData(StorageEntitiesListData data) {
	// TODO Auto-generated method stub
	super.setData(data);
    }

}
