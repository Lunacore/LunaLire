package br.com.lunacore.editor.custom;

import java.io.OutputStream;

import br.com.lunacore.Editor;

public class ConsoleErrorStream extends OutputStream{
	
	public ConsoleErrorStream() {
		super();
	}
	
	@Override
	public void write(int b) {
		Editor.getInstance().getUIState().printParallel(String.valueOf((char)b), true);
	}
}
