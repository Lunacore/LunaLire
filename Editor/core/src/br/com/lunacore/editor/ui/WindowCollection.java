package br.com.lunacore.editor.ui;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.widget.VisTable;

import br.com.lunacore.Editor;
import br.com.lunacore.editor.custom.EditorWindow;
import br.com.lunacore.editor.custom.window.ClassListUI;
import br.com.lunacore.editor.custom.window.ConsoleWindow;
import br.com.lunacore.editor.custom.window.FileExplorer;
import br.com.lunacore.editor.custom.window.LireEditorWindow;
import br.com.lunacore.editor.custom.window.LireObjectPropertiesUI;
import br.com.lunacore.editor.custom.window.ObjectHierarchy;
@SuppressWarnings("rawtypes")
public class WindowCollection {
	
	HashMap<LireEditorWindow, EditorWindow> windows;
	HashMap<LireEditorWindow, Vector2> positioning;
	HashMap<Class, LireEditorWindow> classing;

	
	public WindowCollection() {
		windows = new HashMap<LireEditorWindow, EditorWindow>();
		positioning = new HashMap<LireEditorWindow, Vector2>();
		classing = new HashMap<Class, LireEditorWindow>();
		
		registerWindow(ClassListUI.class, 10, 240, 250, 450);
		registerWindow(LireObjectPropertiesUI.class, 800, 100, 300, 450);
		registerWindow(FileExplorer.class, 10, 10, 700, 250);
		registerWindow(ConsoleWindow.class, 100, 100, 700, 250);
		registerWindow(ObjectHierarchy.class, 0, 0, 250, 450);
	}
	
	
	
	public void registerWindow(Class win, float x, float y, float width, float height) {
		try {
			LireEditorWindow tbl = (LireEditorWindow) win.newInstance();
			
			EditorWindow classWindow = new EditorWindow(tbl.getTitle(), true);
			classWindow.addCloseButton();
			classWindow.add(tbl).grow();
			classWindow.setPosition(x, y);
			classWindow.setSize(width, height);
			classWindow.setResizable(true);
			classWindow.setPosition(10, Gdx.graphics.getHeight());
			
			positioning.put(tbl, new Vector2(x, y));
			windows.put(tbl, classWindow);
			classing.put(win, tbl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refresh(Class c) {
		classing.get(c).refresh();
		windows.get(classing.get(c)).pack();
	}
	
	public void open(Class win) {
		if(!windows.get(classing.get(win)).isOpen()) {
			Editor.getInstance().getStage().addActor(windows.get(classing.get(win)));
			windows.get(classing.get(win)).open();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getWindow(Class<T> win) {
		return (T) classing.get(win);
	}
	
	public void insertIntoStage() {
		for(VisTable v : windows.keySet()) {
			if(windows.get(v).isOpen()) {
				//Posicionamento
				windows.get(v).setPosition(positioning.get(v).x, positioning.get(v).y);
				Editor.getInstance().getStage().addActor(windows.get(v));
			}
		}
		
	}

}
