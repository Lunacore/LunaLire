package br.com.lunacore.editor.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.VisWindow;

import br.com.lunacore.Editor;
import br.com.lunacore.UIState;
import br.com.lunacore.editor.custom.window.LireEditorWindow;
import br.com.lunacore.editor.custom.window.SettingsWindow;

public class TopBar extends MenuBar{
	
	UIState state;
	
	public TopBar(final UIState state) {
		super();
		this.state = state;
		
		createFile();
		createProject();
		createWindow();
		createBuildRun();
		
	}
	
	public void createProject(){
		Menu project = new Menu("Project");
		
		MenuItem settings = new MenuItem("Project Settings");
		settings.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				
				SettingsWindow st = new SettingsWindow();
				
				VisWindow dialog = new VisWindow("Project Settings");
				dialog.add(st).grow();
				dialog.setResizable(true);
				dialog.centerWindow();
				dialog.setSize(500, 500);
				dialog.setModal(true);
				dialog.addCloseButton();
				Editor.getInstance().getUIState().getStage().addActor(dialog);
				
				//VisDialog
				
				//dialog.setSize(500, 500);
				//dialog.centerWindow();
			}
		});
		project.addItem(settings);
		
		addMenu(project);
	}
	
	public void createBuildRun() {
		
		Menu buildRun = new Menu("Build/Run");
		
		MenuItem br = new MenuItem("Build and run");
		buildRun.addItem(br);
		
		br.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
					
					if(Editor.getInstance().getCurrentProject() != null) {
					
						Editor.getInstance().getUIState().getSceneManager().saveScenes();
						
						try {
						ProcessBuilder builder = new ProcessBuilder(Editor.getInstance().getCurrentProject().path() + "/gradlew.bat", "-p", "desktop", "build", "run");
						builder.directory(Editor.getInstance().getCurrentProject().file());
						final Process p = builder.start();
						
						//Input Stream
						new Thread(new Runnable() {
							public void run() {
								try {
									int line = -1;
									while((line = p.getInputStream().read()) != -1) {
										Editor.getInstance().getUIState().editorOut.write(line);
									}
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						
						//Error stream
						new Thread(new Runnable() {
							public void run() {
								try {
									int line = -1;
									while((line = p.getErrorStream().read()) != -1) {
										Editor.getInstance().getUIState().editorOut.write(line);
									}
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						
						}catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					
			
				
				super.clicked(event, x, y);
			}
		});
		
		MenuItem run = new MenuItem("Run");
		buildRun.addItem(run);
		
		MenuItem build = new MenuItem("Build");
		buildRun.addItem(build);

		addMenu(buildRun);
	}
	
	public void createFile() {
		//File
				Menu file = new Menu("File");
				
				MenuItem newProject = new MenuItem("New project");
				//newProject.setShortcut(Keys.CONTROL_LEFT, Keys.N);
				file.addItem(newProject);
				
				MenuItem openProject = new MenuItem("Open project");
				openProject.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						state.openProjectMenu();
					}
				});
				//openProject.setShortcut(Keys.CONTROL_LEFT, Keys.O);
				file.addItem(openProject);
				
				MenuItem saveProject = new MenuItem("Save scene");
				saveProject.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						state.saveProject();
					}
				});
				//saveProject.setShortcut(Keys.CONTROL_LEFT, Keys.S);
				file.addItem(saveProject);
				addMenu(file);

	}
	
	public void createWindow() {
		//Window
				Menu window = new Menu("Window");
				
				for(final LireEditorWindow win : Editor.getInstance().getUIState().getWindowCollection().windows.keySet()) {
					MenuItem windowClassList = new MenuItem(win.getTitle());
					windowClassList.addListener(new ClickListener() {
						public void clicked(InputEvent event, float x, float y) {
							state.openWindow(win.getClass());
						}
					});
					window.addItem(windowClassList);
				}
				
				addMenu(window);
	}

}
