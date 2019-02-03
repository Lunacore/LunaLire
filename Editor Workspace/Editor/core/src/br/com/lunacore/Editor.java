package br.com.lunacore;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.VisUI;

import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.ui.FileExplorer;
import br.com.lunacore.ui.LireSceneViewport;

public class Editor extends ApplicationAdapter {
	
	static Editor instance;
	static FileHandle rootFolder;
	
	FileHandle currentProject;
	UIState uiState;
	
	ArrayList<EditorLireObject> selectedObjects;
	
	DragAndDrop dragndrop;
	
	public void create () {
		instance = this;
		selectedObjects = new ArrayList<EditorLireObject>();
		dragndrop = new DragAndDrop();
		//VisUI.load(Gdx.files.internal("skin/tinted.json"));
		VisUI.load();
		AwesomeLibGDX.init();
		AwesomeLibGDX.addState(uiState = new UIState());
		AwesomeLibGDX.create();
	}
	
	
	public FileHandle getCurrentProject() {
		return currentProject;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isSubclassOf(Class clazz, Class subclass) {
		try {
			clazz.asSubclass(subclass);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public void unselectAll() {
		for(EditorLireObject lo : selectedObjects) {
			lo.setDebug(false);
		}
		selectedObjects.clear();
		
		uiState.refreshObjectProperties();
	}
	
	public void setSelectedObject(EditorLireObject object) {
		if(object != getStage().getCenario()) {
			selectedObjects.clear();
			selectedObjects.add(object);
		}
		uiState.refreshObjectProperties();

	}
	
	public void unselect(EditorLireObject object) {
		if(selectedObjects.contains(object)) {
			selectedObjects.remove(object);
		}
		uiState.refreshObjectProperties();

	}
	
	public void addSelectedObject(EditorLireObject object) {
		if(object != getStage().getCenario()) {
			if(!selectedObjects.contains(object)) {
				selectedObjects.add(object);
			}
		}
		uiState.refreshObjectProperties();

	}
	
	public void setSelectedObjects(ArrayList<EditorLireObject> objects) {
		for(EditorLireObject lo : selectedObjects) {
		}
		selectedObjects.clear();
		for(EditorLireObject lo : objects) {
			if(lo != getStage().getCenario()) {
				selectedObjects.add(lo);
			}
		}
		uiState.refreshObjectProperties();

	}
	
	public void addSelectedObjects(ArrayList<EditorLireObject> objects) {
		for(EditorLireObject lo : objects) {
			if(lo != getStage().getCenario()) {
				lo.setDebug(true);
				selectedObjects.add(lo);
			}
		}
		uiState.refreshObjectProperties();

	}

	public void setCurrentProject(FileHandle currentProject) {
		this.currentProject = currentProject;
	}
	
	public static FileHandle getLunaLireRootFolder() {
		if(rootFolder == null)
		rootFolder = Gdx.files.absolute(new File(System.getProperty("user.dir")).getParentFile().getParentFile().getParentFile().getAbsolutePath());
	
		return rootFolder;
	}

	public void addActor(Actor actor) {
		uiState.addActor(actor);
	}
	
	public DragAndDrop getDragAndDrop() {
		return dragndrop;
	}
	

	public static Editor getInstance() {
		return instance;
	}

	public void render () {
		AwesomeLibGDX.render();
	}
	
	public void dispose () {
		AwesomeLibGDX.dispose();
		VisUI.dispose();
	}

	@Override
	public void resize(int width, int height) {
		AwesomeLibGDX.resize(width, height);
	}

	public void refreshClassList() {
		uiState.refreshClassList();
	}

	public LireSceneViewport getStage() {
		return uiState.stage;
	}

	public ArrayList<EditorLireObject> getSelectedObjects() {
		return selectedObjects;
	}

	public void refreshObjectProperties() {
		uiState.refreshObjectProperties();
	}

	public FileExplorer getFileExplorer() {
		return uiState.explorer;
	}

	public void newScenePopup() {
		uiState.createNewScenePopup();
		getFileExplorer().refreshContents();
	}

	public UIState getUIState() {
		return uiState;
	}

	public void openInEclipse(FileHandle handle) {
		System.out.println("Opening file " + handle.path() + " in eclipse");
		try {
			Runtime.getRuntime().exec("eclipse -data \"" + getCurrentProject().parent().path() + "\" " + handle.path());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Class getClassFromFile(FileHandle handle) throws ClassNotFoundException, MalformedURLException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(System.in, System.out, System.err, handle.path());
		
		File sourceFolder = new File(Editor.getInstance().getCurrentProject().file().getAbsolutePath() + "/core/src");
					
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {sourceFolder.toURI().toURL()});
		//System.out.println(Class.forName(handle.nameWithoutExtension(), true, classLoader));
		return Class.forName(getClassName(handle), true, classLoader);
	}
	
	public String getClassName(FileHandle handle) {
		FileHandle root = handle;
		String nm = "";
		while(!root.name().equals("src")) {
			nm += root.nameWithoutExtension() + ".";
			root = root.parent();
		}
		return reversePackage(nm);
	}

	public String reversePackage(String pckg) {
		String finalString = "";
		String[] list = pckg.split("\\.");
		
		for(int i = list.length -1; i >= 0; i --) {
			finalString += list[i] + ".";
		}
		return finalString.substring(0, finalString.length() - 1);
	}
	

}
