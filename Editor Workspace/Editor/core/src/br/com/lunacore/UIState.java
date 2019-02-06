package br.com.lunacore;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

import br.com.lunacore.custom.ConsoleErrorStream;
import br.com.lunacore.custom.ConsoleOutputStream;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.custom.EditorRuler;
import br.com.lunacore.custom.EditorWindow;
import br.com.lunacore.custom.StringInputDialog;
import br.com.lunacore.custom.YesNoDialog.DialogListener;
import br.com.lunacore.custom.window.ClassListUI;
import br.com.lunacore.custom.window.ConsoleWindow;
import br.com.lunacore.custom.window.FileExplorer;
import br.com.lunacore.custom.window.LireObjectPropertiesUI;
import br.com.lunacore.custom.window.ObjectHierarchy;
import br.com.lunacore.states.State;
import br.com.lunacore.ui.LireSceneViewport;
import br.com.lunacore.ui.SceneManager;
import br.com.lunacore.ui.TopBar;
import br.com.lunacore.ui.WindowCollection;

public class UIState extends State{

	LireSceneViewport stage;
	SceneManager sceneManager;
	
	WindowCollection collection;

	public volatile ConsoleOutputStream editorOut;
	public volatile ConsoleErrorStream editorErr;

	public void create() {
		super.create();
		
		collection = new WindowCollection();

		stage = new LireSceneViewport(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		Gdx.input.setInputProcessor(stage);


		createMainPanel();

		stage.refresh(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		insertIntoMainPanel();

		editorOut = new ConsoleOutputStream();
		editorErr = new ConsoleErrorStream();

	}
	
	public void resize(int width, int height) {
		//stage = new LireSceneViewport(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		stage.refresh(new ScalingViewport(Scaling.fit, width, height));
		
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
				refreshWindow(ClassListUI.class);
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
	
	public void printParallel(String text, boolean error) {		
		synchronized(getWindow(ConsoleWindow.class)) {
			getWindow(ConsoleWindow.class).addText(text, error);
		}
	}

	
	public void insertIntoMainPanel() {
		collection.insertIntoStage();

	}
	
	public void createMainPanel() {
		sceneManager = new SceneManager();
	}
	
	public void addActor(Actor actor) {
		stage.addActor(actor);
	}

	public void refreshWindow(Class win) {
		collection.refresh(win);
	}
	
	
	public void enter() {
		
	}

	public void render(SpriteBatch sb) {
		stage.draw();
	}

	public void update(float delta) {
		stage.act(delta);
	}

	public void openWindow(Class win) {
		collection.open(win);
	}

	public void addDialog(VisDialog dialog) {
		stage.addActor(dialog);
		dialog.centerWindow();
	}

	public LireSceneViewport getStage() {
		return stage;
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

	public <T> T getWindow(Class<T> win) {
		return collection.getWindow(win);
	}

	public WindowCollection getWindowCollection() {
		return collection;
	}

}
