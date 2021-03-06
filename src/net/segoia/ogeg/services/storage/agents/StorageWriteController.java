/**
 * og-node-core - The core resources of an Open Groups node
 * Copyright (C) 2020  Adrian Cristian Ionescu - https://github.com/acionescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.segoia.ogeg.services.storage.agents;

import java.io.IOException;
import java.io.OutputStream;

import net.segoia.event.eventbus.streaming.StreamControllerContext;
import net.segoia.event.eventbus.streaming.WriteStreamController;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedData;
import net.segoia.event.eventbus.streaming.events.StartStreamRejectedEvent;
import net.segoia.util.data.storage.Storage;
import net.segoia.util.data.storage.StorageEntityExistsException;
import net.segoia.util.data.storage.StorageException;

public class StorageWriteController extends WriteStreamController {
    private OutputStream outputStream;
    private Storage mainDataStore;
    private String storageKey;

    public StorageWriteController(Storage mainDataStore, String storageKey) {
	super();
	this.mainDataStore = mainDataStore;
	this.storageKey = storageKey;
    }

    @Override
    public void init(StreamControllerContext controllerContext) {
	/* get source peer */
	String sourcePeerId = controllerContext.getStreamContext().getSourcePeerId();

	try {
	    /* create the leaf file */
	    outputStream = mainDataStore.createLeaf(storageKey);

	} catch (StorageEntityExistsException e) {

	    controllerContext.sendToPeer(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(storageKey, StorageManagerConstants.STORAGE_ENTITY_EXISTS)),
		    sourcePeerId);
	    throw new RuntimeException("Failed create storage for key "+storageKey, e);
	} catch (StorageException e) {
	    e.printStackTrace();
	    controllerContext.sendToPeer(
		    new StartStreamRejectedEvent(
			    new StartStreamRejectedData(storageKey, StorageManagerConstants.STORAGE_PATH_INVALID)),
		    sourcePeerId);
	    throw new RuntimeException("Failed create storage for key "+storageKey, e);
	}

	/* all seems well, init */
	super.init(controllerContext);

    }

    @Override
    protected void write(byte[] data) throws Exception {
	outputStream.write(data);
    }

    @Override
    public void terminate() {
	try {
	    controllerContext.logger().debug("Closing output stream for file "+storageKey);
	    outputStream.close();
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }

}
