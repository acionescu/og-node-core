package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.CustomEvent;
import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.peers.vo.GenericErrorResponse;

@EventType("STORAGE:ERROR:EVENT")
public class StorageErrorEvent extends CustomEvent<GenericErrorResponse> {
    public static final String ET = "STORAGE:ERROR:EVENT";

    public StorageErrorEvent(GenericErrorResponse data) {
	super(ET, data);
    }

    public StorageErrorEvent() {
	super(ET);
    }

    @Override
    public GenericErrorResponse getData() {
	// TODO Auto-generated method stub
	return super.getData();
    }

    @Override
    public void setData(GenericErrorResponse data) {
	// TODO Auto-generated method stub
	super.setData(data);
    }
}
