import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import net.segoia.ogeg.services.storage.agents.SharedDocEditor;
import net.segoia.ogeg.services.storage.events.DocPos;
import net.segoia.ogeg.services.storage.events.StorageDocChangeData;

public class DocEditorTest {

    @Test
    public void testStringBuilder() {
	StringBuilder sb = new StringBuilder("Start string");

	sb.replace(5, 6, "1234");

	System.out.println(sb);

	sb.replace(1, 10, "?");
	System.out.println(sb);
	
	System.out.println(new StringBuilder("fg").replace(0, 1, ""));
	
	System.out.println(Arrays.toString("a".getBytes()));
    }

    @Test
    public void testDocEditor() {
	SharedDocEditor editor = new SharedDocEditor();

	editor.updateFromChange(new StorageDocChangeData(new DocPos(0, 0), new DocPos(0, 0), "a"));

	System.out.println(editor.getFullContent());

	assertEquals("a", editor.getFullContent());

	editor.updateFromChange(new StorageDocChangeData(new DocPos(0, 1), new DocPos(0, 1), "bc"));

	System.out.println(editor.getFullContent());

	editor.updateFromChange(new StorageDocChangeData(new DocPos(1, 0), new DocPos(1, 0), "de", "fg"));

	System.out.println(editor.getFullContent());
	
	/* try a delete */
	editor.updateFromChange(new StorageDocChangeData(new DocPos(1, 0), new DocPos(2, 1), ""));

	System.out.println(editor.getFullContent());
    }

}
