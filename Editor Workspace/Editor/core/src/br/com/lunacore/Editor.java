package br.com.lunacore;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.VisUI;

import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.custom.window.FileExplorer;
import br.com.lunacore.custom.window.LireObjectPropertiesUI;
import br.com.lunacore.ui.LireSceneViewport;

public class Editor extends ApplicationAdapter {
	
	static Editor instance;
	static FileHandle rootFolder;
	
	FileHandle currentProject;
	UIState uiState;
	
	ArrayList<EditorLireObject> selectedObjects;
	
	DragAndDrop dragndrop;
	
	WatchService watchService;
	Map<WatchKey, FileHandle> keys;
	
	Thread watcherThread;
	
	HashMap<FileHandle, Class> compiledClasses;
	
	Stack<FileHandle> queueToCompile;
	
	public void create () {
		instance = this;
		selectedObjects = new ArrayList<EditorLireObject>();
		dragndrop = new DragAndDrop();
		compiledClasses = new HashMap<FileHandle, Class>();
		queueToCompile = new Stack<FileHandle>();
		
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
		
		uiState.refreshWindow(LireObjectPropertiesUI.class);
	}
	
	public void setSelectedObject(EditorLireObject object) {
		selectedObjects.clear();
		selectedObjects.add(object);
		uiState.refreshWindow(LireObjectPropertiesUI.class);

	}
	
	public void unselect(EditorLireObject object) {
		if(selectedObjects.contains(object)) {
			selectedObjects.remove(object);
		}
		uiState.refreshWindow(LireObjectPropertiesUI.class);

	}
	
	public void addSelectedObject(EditorLireObject object) {
		if(!selectedObjects.contains(object)) {
			selectedObjects.add(object);
		}
		uiState.refreshWindow(LireObjectPropertiesUI.class);

	}
	
	public void setSelectedObjects(ArrayList<EditorLireObject> objects) {
		for(EditorLireObject lo : selectedObjects) {
		}
		selectedObjects.clear();
		for(EditorLireObject lo : objects) {
			selectedObjects.add(lo);
		}
		uiState.refreshWindow(LireObjectPropertiesUI.class);

	}
	
	public void addSelectedObjects(ArrayList<EditorLireObject> objects) {
		for(EditorLireObject lo : objects) {
			lo.setDebug(true);
			selectedObjects.add(lo);
		}
		uiState.refreshWindow(LireObjectPropertiesUI.class);

	}

	public void setCurrentProject(FileHandle currentProject) {
		this.currentProject = currentProject;
		
		try {
			watchService = FileSystems.getDefault().newWatchService();
			keys = new HashMap<WatchKey, FileHandle>();
			
			registerDir(currentProject.child("core/src"));
			
			if(watcherThread != null) {
				watcherThread.interrupt();
			}
			
			watcherThread = new Thread(new Runnable() {
				public void run() {
					try {
						while(true) {
							WatchKey key = watchService.take();
								
							FileHandle f = keys.get(key);
							
							if(f != null){
								for(WatchEvent<?> event : key.pollEvents()) {
									WatchEvent.Kind kind = event.kind();
																		
									if (kind == StandardWatchEventKinds.OVERFLOW) {
					                    continue;
					                }
									
									Path p = (Path) event.context();
									FileHandle file = f.child(p.toString());
									
									
									if(file.name().endsWith(".java")) {
										System.out.println("Class changed: " + file.path());
										
										queueToCompile.push(file);
										
										break;
									}
									
									 if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
										 if (Files.isDirectory(file.file().toPath(), LinkOption.NOFOLLOW_LINKS)) {
											 registerDir(file);
										 }
						             }
									 
									
								}
							}
							
							boolean valid = key.reset();
						    if (!valid) {
						    	keys.remove(key);
						       	if (keys.isEmpty()) {
						       		break;
						       	}
						    }
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
			watcherThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void registerDir(FileHandle dir) throws IOException {
		keys.put(dir.file().toPath().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE), dir);

		for(FileHandle fh : dir.list()) {
			if(fh.isDirectory()) {
				registerDir(fh);
			}
		}
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
		
		while(!queueToCompile.isEmpty()) {
			FileHandle f = queueToCompile.pop();
			try {
				Editor.getInstance().getUIState().getSceneManager().compile(f);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void dispose () {
		AwesomeLibGDX.dispose();
		VisUI.dispose();
	}

	@Override
	public void resize(int width, int height) {
		AwesomeLibGDX.resize(width, height);
	}

	public LireSceneViewport getStage() {
		return uiState.stage;
	}

	public ArrayList<EditorLireObject> getSelectedObjects() {
		return selectedObjects;
	}

	public void newScenePopup() {
		uiState.createNewScenePopup();
		uiState.getWindow(FileExplorer.class).refreshContents();
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
		System.out.println(compiler.getClass().getCanonicalName());
				
		int ret = compiler.run(System.in, Editor.getInstance().getUIState().editorOut, Editor.getInstance().getUIState().editorErr, handle.path());
		
		if(ret != 0) return null;
		
		File sourceFolder = new File(Editor.getInstance().getCurrentProject().file().getAbsolutePath() + "/core/src");
					
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {sourceFolder.toURI().toURL()});
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


	public void refreshObjectParameters(Class oldClass, Class newClass) {
		System.out.println("Refreshing object parameters");
		uiState.getStage().refreshObjectParams(oldClass, newClass);
	}


	public HashMap<FileHandle, Class> getCompiledClasses() {
		return compiledClasses;
	}
	

}
