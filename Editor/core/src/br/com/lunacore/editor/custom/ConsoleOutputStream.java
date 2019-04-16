package br.com.lunacore.editor.custom;

import java.io.OutputStream;

import br.com.lunacore.Editor;

public class ConsoleOutputStream extends OutputStream{

	@Override
	public void write(int b) {
		Editor.getInstance().getUIState().printParallel(String.valueOf((char)b), false);
	}
}
