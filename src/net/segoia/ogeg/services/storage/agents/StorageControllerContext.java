package net.segoia.ogeg.services.storage.agents;

import net.segoia.event.eventbus.app.EventNodeControllerContext;
import net.segoia.event.eventbus.peers.GlobalAgentEventNodeContext;

public class StorageControllerContext extends EventNodeControllerContext{
    private StorageManagerAgentContext storageAgentContext;

    public StorageControllerContext(GlobalAgentEventNodeContext globalContext, String peerId, StorageManagerAgentContext storageAgentContext) {
	super(globalContext, peerId);
	this.storageAgentContext=storageAgentContext;
    }

}
