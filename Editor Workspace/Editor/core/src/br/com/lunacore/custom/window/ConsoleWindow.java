package br.com.lunacore.custom.window;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.VisTextFieldStyle;

public class ConsoleWindow extends LireEditorWindow{
	
	VisScrollPane pane;
	volatile ArrayList<String> queue;
	volatile ArrayList<String> errorQueue;
	int maxLines = 300;
	
	
	ArrayList<VisTextField> lines;
	
	VisTable inner;
	
	VisTextFieldStyle errStyle;
	
	public ConsoleWindow() {
		super();
		construct();
	}
	
	public void construct() {
		lines = new ArrayList<VisTextField>();
		
		errStyle = copyStyle(VisUI.getSkin().get(VisTextFieldStyle.class));
		errStyle.fontColor = Color.RED.cpy().lerp(Color.WHITE, 0.2f);
		
		queue = new ArrayList<String>();
		errorQueue = new ArrayList<String>();
				
		inner = new VisTable();
		inner.align(Align.top);
		
		add(pane = new VisScrollPane(inner)).grow().pad(10);
		pane.setFlickScroll(false);
		pane.setSmoothScrolling(false);
	}
	
	public synchronized void act(float delta) {
		super.act(delta);

		listenQueue(queue, false);
		listenQueue(errorQueue, true);
		
	}
	
	public void listenQueue(ArrayList<String> queue, boolean error) {
		synchronized (queue) {
			if(queue.size() > 0) {
				for(String s : queue) {
					
					if(lines.size() == 0) {
						createLine(error);
					}
					
					String[] splat = s.split("\n");
					
					if(s.startsWith("\n")) createLine(error);
					
					int i = 0;
					for(String sl : splat) {
						if(i != 0) createLine(error);
						lines.get(lines.size()-1).appendText(sl);
						i++;
					}
					
					if(s.endsWith("\n") && !s.equals("\n")) createLine(error);
					
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
	
	private void createLine(boolean error) {
		VisTextField field = new VisTextField("");
		field.setReadOnly(true);

		if(error) {
			field.setStyle(errStyle);
		}
		lines.add(field);
		inner.add(field).growX().row();
	}
	
	public VisTextFieldStyle copyStyle(VisTextFieldStyle style) {
		VisTextFieldStyle copied = new VisTextFieldStyle();
		copied.font = style.font;
		copied.fontColor = style.fontColor;
		copied.background = style.background;
		copied.backgroundOver = style.background;
		copied.cursor = style.cursor;
		copied.disabledBackground = style.disabledBackground;
		copied.disabledFontColor = style.disabledFontColor;
		copied.errorBorder = style.errorBorder;
		copied.focusBorder = style.focusBorder;
		copied.focusedBackground = style.focusedBackground;
		copied.focusedFontColor = style.focusedFontColor;
		copied.messageFont = style.messageFont;
		copied.messageFontColor = style.messageFontColor;
		copied.selection = style.selection;
		return copied;
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
	
	public synchronized void addText(String text, boolean error) {
		if(error)
			errorQueue.add(text);
		else
			queue.add(text);
	}

	@Override
	public void refresh() {
		clear();
		construct();
		
	}

	@Override
	public String getTitle() {
		return "Console";
	}
}