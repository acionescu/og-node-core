package net.segoia.ogeg.services.storage.agents;

import java.util.HashMap;
import java.util.Map;

import net.segoia.ogeg.services.chat.agents.Chat;
import net.segoia.ogeg.services.chat.events.ChatPeerData;

public class DocEditContext {
    private String docKey;
    private SharedDocEditor editor;
    private Map<String, DocEditPeerContext> peers = new HashMap<>();
    /**
     * Simulate a chat around this document editing
     */
    private Chat chat;

    public DocEditContext(String docKey, SharedDocEditor editor) {
	super();
	this.docKey=docKey;
	this.editor = editor;
	this.chat = new Chat(docKey);
    }
    
    public boolean hasPeer(String peerId) {
	return peers.containsKey(peerId);
    }

    public void addPeer(String peerId) {
	peers.put(peerId, new DocEditPeerContext(peerId));
	chat.addLocalParicipant(new ChatPeerData(docKey, peerId));
    }
    
    public DocEditPeerContext removePeer(String peerId) {
	chat.removeParticipant(peerId);
	return peers.remove(peerId);
    }
    
    public SharedDocEditor getEditor() {
	return editor;
    }

    public void setEditor(SharedDocEditor editor) {
	this.editor = editor;
    }

    public Map<String, DocEditPeerContext> getPeers() {
	return peers;
    }

    public void setPeers(Map<String, DocEditPeerContext> peers) {
	this.peers = peers;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
    
    
}
