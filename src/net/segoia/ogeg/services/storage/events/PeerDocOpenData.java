package net.segoia.ogeg.services.storage.events;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;
import net.segoia.ogeg.services.chat.events.ChatPeerData;

@EventDataType(@EventType(value = "PEER:DOC:OPENED", className = "PeerDocOpenedEvent"))
public class PeerDocOpenData extends ChatPeerData {
    private String docKey;

    public PeerDocOpenData() {
	super();
	// TODO Auto-generated constructor stub
    }

    public PeerDocOpenData(String docKey, String peerId) {
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
