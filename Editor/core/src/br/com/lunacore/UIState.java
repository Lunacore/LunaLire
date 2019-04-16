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

import br.com.lunacore.awesome.states.State;
import br.com.lunacore.editor.custom.ConsoleErrorStream;
import br.com.lunacore.editor.custom.ConsoleOutputStream;
import br.com.lunacore.editor.custom.EditorLireObject;
import br.com.lunacore.editor.custom.StringInputDialog;
import br.com.lunacore.editor.custom.YesNoDialog.DialogListener;
import br.com.lunacore.editor.custom.window.ClassListUI;
import br.com.lunacore.editor.custom.window.ConsoleWindow;
import br.com.lunacore.editor.custom.window.LireObjectPropertiesUI;
import br.com.lunacore.editor.custom.window.ObjectHierarchy;
import br.com.lunacore.editor.ui.LireSceneViewport;
import br.com.lunacore.editor.ui.SceneManager;
import br.com.lunacore.editor.ui.TopBar;
import br.com.lunacore.editor.ui.WindowCollection;

@SuppressWarnings("rawtypes")
public class UIState extends State{

	LireSceneViewport stage;
	SceneManager sceneManager;
	
	WindowCollection collection;

	public volatile ConsoleOutputStream editorOut;
	public volatile ConsoleErrorStream editorErr;

	VisTable table;
	
	private TopBar topBar;
	
	public void create() {
		super.create();
		
		collection = new WindowCollection();

		topBar = new TopBar(this);
		
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
		//collection.insertIntoStage();

		//This table holds all the stage window actors
		table = new VisTable();
		table.setFillParent(true);
		table.align(Align.left);
		table.debugAll();
		table.setWidth(stage.getWidth());
		table.setHeight(stage.getHeight());
		
		table.add(topBar.getTable()).colspan(3).fillX().expandX().row();

		
		table.add(collection.getWindow(ObjectHierarchy.class)).fill().align(Align.topLeft);
		table.add(new VisTable()).align(Align.topLeft).width(stage.getWidth() * 0.3f).expandY();
		table.add(collection.getWindow(LireObjectPropertiesUI.class)).align(Align.topLeft).fill();
		table.row();
		
		table.add(collection.getWindow(ConsoleWindow.class)).colspan(3).align(Align.topLeft).fill().expandX().maxHeight(stage.getHeight() * 0.3f);

		
		stage.addActor(table);
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

	public TopBar getTopBar() {
		return topBar;
	}

	public TopBar setTopBar(TopBar topBar) {
		this.topBar = topBar;
		return topBar;
	}

}
