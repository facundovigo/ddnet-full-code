package ddnet.useragent.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

public class UIUtils {
	private UIUtils() {}
	
	public static void changeFontRecursive(Container root, Font font) {
	    for (Component c : root.getComponents()) {
	        c.setFont(font);
	        if (c instanceof Container)
	            changeFontRecursive((Container) c, font);
	    }
	}
}
