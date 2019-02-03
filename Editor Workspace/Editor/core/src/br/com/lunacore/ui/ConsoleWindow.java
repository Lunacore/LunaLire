package br.com.lunacore.ui;

import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;

public class ConsoleWindow extends VisTable{
	
	volatile VisTextArea area;
	
	public ConsoleWindow() {
		super();
		
		area = new VisTextArea();
		area.setDisabled(true);
		area.getStyle().disabledFontColor.set(0.7f, 0.7f, 0.7f, 1);
		
		add(new VisScrollPane(area)).grow().pad(10);
	}
	
	public VisTextArea getArea() {
		return area;
	}
}
