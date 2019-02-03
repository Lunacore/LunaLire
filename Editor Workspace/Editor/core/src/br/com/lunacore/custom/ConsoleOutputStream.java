package br.com.lunacore.custom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.kotcrab.vis.ui.widget.VisTextArea;

import br.com.lunacore.Editor;

public class ConsoleOutputStream extends PrintStream{

	PrintStream sys;
	
	public ConsoleOutputStream(PrintStream sys) {
		super(sys);
		this.sys = sys;
	}
	
	@Override
	public void print(String s) {
		super.print(s);

		Editor.getInstance().getUIState().printParallel(s);
	}
	
	@Override
	public void println(String x) {
		super.println(x);
		
		Editor.getInstance().getUIState().printParallel(x);
	}
	

}