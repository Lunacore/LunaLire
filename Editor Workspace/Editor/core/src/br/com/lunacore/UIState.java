package br.com.lunacore;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

import br.com.lunacore.custom.ConsoleOutputStream;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.custom.EditorWindow;
import br.com.lunacore.custom.StringInputDialog;
import br.com.lunacore.custom.YesNoDialog.DialogListener;
import br.com.lunacore.states.State;
import br.com.lunacore.ui.ClassListUI;
import br.com.lunacore.ui.ConsoleWindow;
import br.com.lunacore.ui.FileExplorer;
import br.com.lunacore.ui.LireObjectPropertiesUI;
import br.com.lunacore.ui.LireSceneViewport;
import br.com.lunacore.ui.ObjectHierarchy;
import br.com.lunacore.ui.SceneManager;
import br.com.lunacore.ui.TopBar;

public class UIState extends State{

	LireSceneViewport stage;
	VisTable root;
	
	ClassListUI classList;
	FileExplorer explorer;
	LireObjectPropertiesUI objectProperties;
	SceneManager sceneManager;
	ConsoleWindow console;
	ObjectHierarchy hierarchy;
	
	EditorWindow classWindow;
	EditorWindow explorerWindow;
	EditorWindow objPropWindow;
	EditorWindow consoleWindow;
	EditorWindow hierarchyWindow;

	
	public volatile ConsoleOutputStream editorOut;
	
	public void create() {
		super.create();
		stage = new LireSceneViewport(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		//stage.setDebugAll(true);
		
		root = new VisTable();
		stage.addActor(root);
		root.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);

		createMainPanel();
		insertIntoMainPanel();
		
		editorOut = new ConsoleOutputStream(System.out);

	}
	
	public void resize(int width, int height) {
		//stage = new LireSceneViewport(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		stage.refresh(new ScalingViewport(Scaling.fit, width, height));
		root = new VisTable();
		stage.addActor(root);
		root.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
		insertIntoMainPanel();
	}
	
	public void openProjectMenu() {
		FileChooser chooser = new FileChooser(Editor.getLunaLireRootFolder(), Mode.OPEN);
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".ll") || pathname.isDirectory();
			}
		});
		chooser.setListener(new FileChooserListener() {
			public void selected(Array<FileHandle> files) {
				Editor.getInstance().setCurrentProject(files.get(0).parent());
				sceneManager.loadScene();
				Editor.getInstance().refreshClassList();
			}

			public void canceled() {
				
			}
		});
		Editor.getInstance().addActor(chooser);
	}
	
	public void saveProject() {
		if(Editor.getInstance().getCurrentProject() != null) {
			sceneManager.saveScenes();
			//copiar o arquivo project.ll pra pasta assets
			try {
			FileUtils.copyFile(
					Editor.getInstance().getCurrentProject().child("project.ll").file(),
					Editor.getInstance().getCurrentProject().child("core/assets/project.ll").file());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void printParallel(String text) {		
//		try {
//		console.getArea().appendText(text + "\n");
//		}
//		catch(Exception e) {
//			System.out.println("err");
//		}
//		if(console.getArea().getText().length() > 2000) {
//			console.getArea().setText(console.getArea().getText().substring(1000));
//		}
	}
	
	public void insertIntoMainPanel() {
		root.add(new TopBar(this).getTable()).fillX().expandX().row();
		root.add(new VisTable()).grow();
		
		if(classWindow.isOpen()) {
			classWindow.setPosition(30, Gdx.graphics.getHeight() - classWindow.getHeight() - 50);
			stage.addActor(classWindow);
		}
		if(explorerWindow.isOpen()) {
			explorerWindow.setPosition(30, 30);
			stage.addActor(explorerWindow);
		}
		if(objPropWindow.isOpen()) {
			objPropWindow.setPosition(Gdx.graphics.getWidth() - objPropWindow.getWidth() - 30, Gdx.graphics.getHeight() - objPropWindow.getHeight() - 50);
			stage.addActor(objPropWindow);
		}
		if(consoleWindow.isOpen()) {
			consoleWindow.setPosition(Gdx.graphics.getWidth(), 30);
			stage.addActor(consoleWindow);
		}
		if(hierarchyWindow.isOpen()) {
			hierarchyWindow.setPosition(Gdx.graphics.getWidth()/2f, 30);
			stage.addActor(hierarchyWindow);
		}
	}
	
	public void createMainPanel() {
		classList = new ClassListUI(this);
		sceneManager = new SceneManager(this);
		objectProperties = new LireObjectPropertiesUI(this);
		explorer = new FileExplorer(this);
		console = new ConsoleWindow();
		hierarchy = new ObjectHierarchy();

		classWindow = new EditorWindow("Class list", true);
		classWindow.addCloseButton();
		classWindow.add(classList).grow();
		classWindow.setPosition(16, 240);
		classWindow.setSize(250, 450);
		classWindow.setResizable(true);
		classWindow.setPosition(10, Gdx.graphics.getHeight());
		
		explorerWindow = new EditorWindow("Explorer");
		explorerWindow.addCloseButton();
		explorerWindow.setSize(700, 250);
		explorerWindow.add(explorer).grow();
		explorerWindow.setResizable(true);
		
		objPropWindow = new EditorWindow("Object Properties");
		objPropWindow.addCloseButton();
		objPropWindow.setPosition(800, 100);
		objPropWindow.setSize(300, 450);
		objPropWindow.add(objectProperties).prefSize(200).grow();
		objPropWindow.setResizable(true);
		objPropWindow.pack();
		
		consoleWindow = new EditorWindow("Console");
		consoleWindow.addCloseButton();
		consoleWindow.setPosition(100, 100);
		consoleWindow.setSize(700, 250);
		VisScrollPane cpw = new VisScrollPane(console);
		cpw.setFlickScroll(false);
		consoleWindow.add(cpw).grow();
		consoleWindow.setResizable(true);
		
		hierarchyWindow = new EditorWindow("Object Hierarchy");
		hierarchyWindow.addCloseButton();
		hierarchyWindow.centerWindow();
		hierarchyWindow.setSize(250, 450);
		hierarchyWindow.add(hierarchy).grow();
		hierarchyWindow.setResizable(true);
	}
	
	public void addActor(Actor actor) {
		stage.addActor(actor);
	}

	public void refreshClassList() {
		classList.refresh();
		explorer.refresh();
	}
	
	public void refreshObjectProperties() {
		objectProperties.refresh();		
		objPropWindow.pack();
	}
	
	public void refreshObjectHierarchy() {
		hierarchy.refresh();
		hierarchyWindow.pack();
	}
	
	public void enter() {
		
	}

	public void render(SpriteBatch sb) {
		stage.draw();
	}

	public void update(float delta) {
		stage.act(delta);
	}

	public void openConsoleWindow() {
		if(!consoleWindow.isOpen()) {
			stage.addActor(consoleWindow);
			consoleWindow.open();
		}
	}
	
	public void openClassListWindow() {
		if(!classWindow.isOpen()) {
			stage.addActor(classWindow);
			classWindow.open();
		}
	}

	public void openAssetExplorerWindow() {
		if(!explorerWindow.isOpen()) {
			stage.addActor(explorerWindow);
			explorerWindow.open();
		}				
	}

	public void addDialog(VisDialog dialog) {
		stage.addActor(dialog);
	}

	public LireSceneViewport getStage() {
		return stage;
	}

	public void openObjectPropertiesWindow() {
		if(!objPropWindow.isOpen()) {
			stage.addActor(objPropWindow);
			objPropWindow.open();
		}				
	}

	public void addObjectToViewport(EditorLireObject lr) {
		stage.addLireObject(lr);
	}
	
	public void createNewScenePopup() {
		final StringInputDialog inputDialog = new StringInputDialog("New scene", "Enter scene name",
				new DialogListener() {
					public void reject() {
						
					}
					public void accept(Object obj) {
						sceneManager.createScene((String) obj);
					}
				}, new InputValidator() {
					public boolean validateInput(String input) {
						return !input.contains(" ");
					}
				});
		addDialog(inputDialog);
	}

	public void resetScene() {
		stage.resetScene();
	}

	public SceneManager getSceneManager() {
		return sceneManager;
	}

	public ClassListUI getClassList() {
		return classList;
	}

	public ObjectHierarchy getHierarchy() {
		return hierarchy;
	}



	

}