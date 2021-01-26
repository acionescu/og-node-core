package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.CustomEvent;
import net.segoia.event.eventbus.EventType;

@EventType("STORAGE:LIST:ENTITIES")
public class ListStorageEntitiesRequestEvent extends CustomEvent<ListStorageEntitiesRequest> {
    public static final String ET = "STORAGE:LIST:ENTITIES";

    public ListStorageEntitiesRequestEvent() {
	super(ET);
    }

    public ListStorageEntitiesRequestEvent(ListStorageEntitiesRequest data) {
	super(ET, data);
    }

    @Override
    public ListStorageEntitiesRequest getData() {
	// TODO Auto-generated method stub
	return super.getData();
    }

    @Override
    public void setData(ListStorageEntitiesRequest data) {
	// TODO Auto-generated method stub
	super.setData(data);
    }

}
