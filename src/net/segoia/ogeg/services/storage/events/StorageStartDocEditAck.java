package net.segoia.ogeg.services.storage.events;

import java.util.List;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;
import net.segoia.ogeg.services.chat.events.ChatPeerData;

@EventDataType(@EventType(value = "DOC:START_EDIT:ACK", className = "StorageStartDocEditAckEvent"))
public class StorageStartDocEditAck {
    /**
     * The storage key of this doc
     */
    private String docKey;
    /**
     * The location where the document can be downloaded <br>
     * If null, then the document is newly created
     */
    private String docUri;
    
    /**
     * The list of peers already editing the document 
     */
    private List<ChatPeerData> editingPeers;

    public StorageStartDocEditAck() {
	super();
    }

    public StorageStartDocEditAck(String docKey) {
	super();
	this.docKey = docKey;
    }

    public StorageStartDocEditAck(String docKey, String docUri) {
	super();
	this.docKey = docKey;
	this.docUri = docUri;
    }
    
    

    public StorageStartDocEditAck(String docKey, String docUri, List<ChatPeerData> editingPeers) {
	super();
	this.docKey = docKey;
	this.docUri = docUri;
	this.editingPeers = editingPeers;
    }

    public String getDocKey() {
	return docKey;
    }

    public void setDocKey(String docKey) {
	this.docKey = docKey;
    }

    public String getDocUri() {
	return docUri;
    }

    public void setDocUri(String docUri) {
	this.docUri = docUri;
    }

    public List<ChatPeerData> getEditingPeers() {
        return editingPeers;
    }

    public void setEditingPeers(List<ChatPeerData> editingPeers) {
        this.editingPeers = editingPeers;
    }

}
