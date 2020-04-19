package ddnet.useragent.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public abstract class Form extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void toFront() {
		synchronized(getTreeLock()) {
		    int status = super.getExtendedState() & ~JFrame.ICONIFIED;
		    super.setExtendedState(status);
		    super.setAlwaysOnTop(true);
		    super.toFront();
		    super.requestFocus();
		    //super.setAlwaysOnTop(false);
		}
	 }
	
	@Override
	public void keyPressed(KeyEvent e) {
		onKeyPressed(e);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	protected void onKeyPressed(KeyEvent e) {
		// Nada para hacer por defecto.
	}
}
