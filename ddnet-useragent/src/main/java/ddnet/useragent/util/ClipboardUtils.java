package ddnet.useragent.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClipboardUtils {
	private ClipboardUtils() { }
	
	public static void setClipboardContent(final String clipboardContent) {
		StringSelection stringSelection = new StringSelection (clipboardContent);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}	
}
