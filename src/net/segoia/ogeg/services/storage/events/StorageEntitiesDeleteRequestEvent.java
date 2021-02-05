package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.CustomEvent;
import net.segoia.event.eventbus.EventType;

@EventType("STORAGE:DELETE:ENTITIES")
public class StorageEntitiesDeleteRequestEvent extends CustomEvent<StorageEntitiesDeleteRequest> {
    public static final String ET = "STORAGE:DELETE:ENTITIES";

    public StorageEntitiesDeleteRequestEvent(StorageEntitiesDeleteRequest data) {
	super(ET, data);
    }

    public StorageEntitiesDeleteRequestEvent() {
	super(ET);
    }

    @Override
    public StorageEntitiesDeleteRequest getData() {
	// TODO Auto-generated method stub
	return super.getData();
    }

    @Override
    public void setData(StorageEntitiesDeleteRequest data) {
	// TODO Auto-generated method stub
	super.setData(data);
    }

}
