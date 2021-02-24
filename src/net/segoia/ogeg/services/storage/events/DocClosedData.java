package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;
import net.segoia.ogeg.services.chat.events.ChatPeerData;

@EventDataType(@EventType(value = "PEER:DOC:CLOSED", className = "PeerDocClosedEvent"))
public class DocClosedData extends ChatPeerData {
    private String docKey;

    public DocClosedData() {
	super();
	// TODO Auto-generated constructor stub
    }

    public DocClosedData(String docKey, String peerId) {
	super();
	this.docKey = docKey;
	setPeerId(peerId);
    }

    public String getDocKey() {
        return docKey;
    }

    public void setDocKey(String docKey) {
        this.docKey = docKey;
    }
}
