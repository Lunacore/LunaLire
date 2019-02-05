package br.com.lunacore.ui;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisTextField;

public class ConsoleWindow extends VisTable{
	
	VisScrollPane pane;
	volatile ArrayList<String> queue;
	int maxLines = 300;
	
	VisTable inner;
	public ConsoleWindow() {
		super();
		queue = new ArrayList<String>();
		inner = new VisTable();
		inner.align(Align.top);
		
		add(pane = new VisScrollPane(inner)).grow().pad(10);
		pane.setFlickScroll(false);
		pane.setSmoothScrolling(false);
	}
	
	public synchronized void act(float delta) {
		super.act(delta);

		synchronized (queue) {
			if(queue.size() > 0) {
				for(String s : queue) {
					inner.add(createLine(s)).growX().row();
					//area.appendText(s);
				}
				queue.clear();
				
				while(inner.getChildren().size > maxLines) {
					inner.removeActor(inner.getChildren().get(0));
				}
				
				//area.setPrefRows(area.getLines()); 	
				//area.setCursorAtTextEnd();
				pane.setScrollPercentY(1);
				pane.layout();
			}
		}
		
	}
	
	private VisTextField createLine(String text) {
		VisTextField field = new VisTextField(text);
		field.setReadOnly(true);
		return field;
	}
	
	private String[] splitByEnd(String[] array, int newSize) {
		if(newSize < array.length) {
			String[] out = new String[newSize];
			
			for(int i = 0; i < newSize; i ++) {
				out[i] = array[array.length - newSize + i];
			}
			return out;
		}
		else return array;
	}
	
	public synchronized void addText(String text) {
		queue.add(text);
	}
}
