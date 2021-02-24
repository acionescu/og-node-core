package net.segoia.ogeg.services.storage.events;

import java.util.Arrays;
import java.util.List;

import net.segoia.event.eventbus.EventType;
import net.segoia.event.eventbus.annotations.EventDataType;

/**
 * This is structured as a change object from the @see <a href="https://codemirror.net/doc/manual.html">CodeMirror</a>
 * js editor
 * 
 * @author adi
 *
 */
@EventDataType(@EventType(value = "DOC:CHANGE:DATA", className = "StorageDocChangeDataEvent"))
public class StorageDocChangeData {
    private String docKey;

    /**
     * Start position in the doc
     */
    private DocPos from;

    /**
     * End position in the doc
     */
    private DocPos to;
    /**
     * The actual change.
     */
    private List<String> text;
    /**
     * Information about the type of change <br>
     * Usual values are '+input' for addition, '+delete' for deletion, 'paste' if the user pasted some text
     */
    private String opType;

    /**
     * The source of the change
     */
    private String origin;

    public StorageDocChangeData(DocPos from, DocPos to, String... text) {
	super();
	this.from = from;
	this.to = to;
	this.text = Arrays.asList(text);
    }

    public StorageDocChangeData(DocPos from, DocPos to, List<String> text) {
	super();
	this.from = from;
	this.to = to;
	this.text = text;
    }

    public StorageDocChangeData() {
	super();
	// TODO Auto-generated constructor stub
    }

    public DocPos getFrom() {
	return from;
    }

    public void setFrom(DocPos from) {
	this.from = from;
    }

    public DocPos getTo() {
	return to;
    }

    public void setTo(DocPos to) {
	this.to = to;
    }

    public List<String> getText() {
	return text;
    }

    public void setText(List<String> text) {
	this.text = text;
    }

    public String getOrigin() {
	return origin;
    }

    public void setOrigin(String origin) {
	this.origin = origin;
    }

    public String getOpType() {
	return opType;
    }

    public void setOpType(String opType) {
	this.opType = opType;
    }

    public String getDocKey() {
	return docKey;
    }

    public void setDocKey(String docKey) {
	this.docKey = docKey;
    }

}
