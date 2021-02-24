package net.segoia.ogeg.services.storage.events;

public class DocPos {
    /**
     * The line position in the document
     */
    private int line;
    /**
     * The character position in the line
     */
    private int ch;

    public DocPos(int line, int ch) {
	super();
	this.line = line;
	this.ch = ch;
    }

    public DocPos() {
	super();
	// TODO Auto-generated constructor stub
    }

    public int getLine() {
	return line;
    }

    public void setLine(int line) {
	this.line = line;
    }

    public int getCh() {
	return ch;
    }

    public void setCh(int ch) {
	this.ch = ch;
    }

}
