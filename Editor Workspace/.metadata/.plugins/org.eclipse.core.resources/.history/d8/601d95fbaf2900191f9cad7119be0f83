package br.com.lunacore.custom;

import com.kotcrab.vis.ui.widget.VisWindow;

public class EditorWindow extends VisWindow{

	boolean open = true;

	public EditorWindow(String title) {
		super(title);
	}
	
	public EditorWindow(String string, boolean b) {
		super(string, b);
	}

	protected void close() {
		super.close();
		open = false;
	}
	
	public void open() {
		open = true;
	}

	public boolean isOpen() {
		return open;
	}

}
