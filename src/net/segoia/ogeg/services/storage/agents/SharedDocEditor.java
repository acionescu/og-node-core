package net.segoia.ogeg.services.storage.agents;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.segoia.ogeg.services.storage.events.DocPos;
import net.segoia.ogeg.services.storage.events.StorageDocChangeData;
import net.segoia.util.data.storage.DocLineProcessor;
import net.segoia.util.data.storage.Storage;
import net.segoia.util.data.storage.StorageEntity;
import net.segoia.util.data.storage.StorageEntityExistsException;
import net.segoia.util.data.storage.StorageEntityInfo;
import net.segoia.util.data.storage.StorageException;
import net.segoia.util.data.storage.StorageUtil;

/**
 * Manages realtime update of a shared text document by multiple users
 * 
 * @author adi
 *
 */
public class SharedDocEditor extends StorageEntity {
    public static final String NEWLINE = System.lineSeparator();
    private Storage mainDataStore;
    private String storageKey;
    /**
     * The length of a single fragment
     */
    private int maxFragmentLength = 262144;
    private StringBuilder contentBuilder = new StringBuilder();
    /**
     * Store the documents as lines
     */
    private List<StringBuilder> lines = new ArrayList<>();

    private long lastModified;
    private long lastSaved;

    public SharedDocEditor(Storage mainDataStore, String storageKey) throws DocEditException {
	super(mainDataStore, storageKey);
	this.mainDataStore = mainDataStore;
	this.storageKey = storageKey;
	init();
    }

    public SharedDocEditor() {
	super();
    }

    private void init() throws DocEditException {
	if (mainDataStore.exists(storageKey)) {
	    /* if this exists, load the data */
	    try {
		load(mainDataStore.openForRead(storageKey));
	    } catch (Exception e) {
		Throwable cause = e.getCause();
		if (cause != null && cause instanceof DocEditException) {
		    throw (DocEditException) cause;
		}

		throw new DocEditException("Failed loading data from file " + storageKey, e);
	    }
	} else {
	    /* just create an empty file */
	    try {
		mainDataStore.create(storageKey).close();
	    } catch (IOException | StorageEntityExistsException | StorageException e) {
		throw new DocEditException("Failed creating shared doc " + storageKey, e);
	    }
	}

    }

    private void load(InputStream in) throws Exception {

	String charSet = "UTF-8";

	lines = new ArrayList<>();

	DocLineProcessor processor = new DocLineProcessor() {

	    @Override
	    public void processLine(String line) throws DocEditException {
		if (!StorageUtil.isText(line)) {
		    throw new DocEditException("Can't edit a binary file");
		}

		lines.add(new StringBuilder(line));
	    }
	};

	StorageUtil.processStreamAsLines(in, processor, charSet);

    }

    public void updateFromChange(StorageDocChangeData changeData) {
	DocPos from = changeData.getFrom();
	DocPos to = changeData.getTo();

	int startLine = from.getLine();
	int endLine = to.getLine();

	List<String> changesList = changeData.getText();

	/* the line index in the document */
	int currentLineIndex = startLine;
	StringBuilder currentLine;

	/* The index of the line in this update batch. Used to keep address changes in the changes list */
	int batchLineIndex = 0;
	String lastChangeText = "";

	/* define a list to mark lines to remove */
	List<Integer> toRemove = new ArrayList<>();

	while (currentLineIndex <= endLine || batchLineIndex < changesList.size()) {
	    if (currentLineIndex < lines.size()) {
		currentLine = lines.get(currentLineIndex);
	    } else {
		/* create lines until we get to the current index */
		do {
		    currentLine = new StringBuilder();
		    lines.add(currentLine);
		} while (lines.size() <= currentLineIndex);
	    }

	    /* determine start char and end char on the current line */
	    int lineStartCh = 0;
	    int lineEndCh = currentLine.length();

	    if (currentLineIndex == startLine) {
		lineStartCh = from.getCh();
	    }
	    if (currentLineIndex == endLine) {
		lineEndCh = to.getCh();
	    }

	    if (batchLineIndex < changesList.size()) {
		/* get change for current line */
		lastChangeText = changesList.get(batchLineIndex);
		batchLineIndex++;
	    }

	    /* we have to determine if this line should be updated or removed */

	    /* compute change size form end ch and start ch */
	    int changeSize = lineEndCh - lineStartCh;
//	    System.out.println("line " + currentLineIndex + " change size: " + changeSize + " line length: "
//		    + currentLine.length());
	    if (changeSize < currentLine.length() || startLine == endLine) {
		/* if this is an incomplete line update or it's a one liner chnage, do replace */
//		System.out.println("replace in line " + currentLineIndex + " " + lineStartCh + ":" + lineEndCh
//			+ " with " + lastChangeText);
		currentLine.replace(lineStartCh, lineEndCh, lastChangeText);
	    } else {
		/* otherwise, mark line for removal */
//		System.out.println("mark for removal line "+currentLineIndex);
		toRemove.add(currentLineIndex);
	    }

//	    
//	    else if(lineEndCh < currentLine.length()) {
//		/* if this is a last line incomplete delete, just replace the chars with the lastChange text, probably an empty string */
//		currentLine.replace(lineStartCh, lineEndCh, lastChangeText);
//	    }
//	    else {
//		/* remove the line altogether */
//		lines.remove(currentLineIndex);
//	    }

	    currentLineIndex++;
	}

	/* remove marked items, in descending order of indexes */
	for (int li = toRemove.size() - 1; li >= 0; li--) {
//	    System.out.println("removing "+toRemove.get(li));
	    lines.remove(toRemove.get(li).intValue());
	}

	lastModified = System.currentTimeMillis();
    }

    public String getFullContent() {
	StringBuilder out = new StringBuilder();
	boolean first = true;
	for (StringBuilder l : lines) {
	    if (!first) {
		out.append(NEWLINE);

	    }
	    out.append(l);
	    first = false;
	}
	return out.toString();
    }

    public int getFullContentBytesCount() {
	int bc = 0;
	for (StringBuilder l : lines) {
	    bc += l.toString().getBytes().length;
	}
	bc += (lines.size() - 1) * NEWLINE.getBytes().length;
	return bc;
    }

    @Override
    public StorageEntityInfo getInfo() {
	/* return a new entity info reflecting the current state of the doc */
	return new StorageEntityInfo(storageKey, false, lastModified, getFullContentBytesCount());
    }

    @Override
    public InputStream openForRead() throws StorageException {
	return new ByteArrayInputStream(getFullContent().getBytes());
    }

    @Override
    public boolean hasChanged() {
	return lastModified > lastSaved;
    }

    @Override
    public boolean save() throws StorageException {
	boolean saved = super.save();
	if (saved) {
	    lastSaved = System.currentTimeMillis();
	}
	return saved;
    }

    @Override
    public void write() throws StorageException {
	PrintWriter pw = null;
	try {
	    /* open for overwrite */
	    OutputStream os = mainDataStore.openForUpdate(storageKey, false);
	    /* since we have the data stored as lines, seems more efficient to write it as lines */
	    pw = new PrintWriter(os);

	    for (StringBuilder sb : lines) {
		pw.println(sb.toString());
	    }
	} catch (Exception e) {
	    throw new StorageException("Autosave failed for " + mainDataStore.getId() + " " + storageKey, e);

	} finally {
	    if (pw != null) {
		pw.flush();
		pw.close();
	    }
	}
    }

    public void terminate(boolean save) {

    }
}
