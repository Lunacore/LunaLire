package br.com.lunacore.custom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

import com.kotcrab.vis.ui.widget.VisTextArea;

import br.com.lunacore.Editor;

public class ConsoleOutputStream extends OutputStream{

	@Override
	public void write(int b) {
		Editor.getInstance().getUIState().printParallel(String.valueOf((char)b), false);
	}
}
