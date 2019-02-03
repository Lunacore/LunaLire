package br.com.lunacore.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;

import br.com.lunacore.Editor;
import br.com.lunacore.UIState;

public class TopBar extends MenuBar{
	
	UIState state;
	
	public TopBar(final UIState state) {
		super();
		this.state = state;
		
		createFile();
		createWindow();
		createBuildRun();
		
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
									BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
									String line = null;
									while((line = br.readLine()) != null) {
										Editor.getInstance().getUIState().editorOut.println(line);
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
									BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
									String line = null;
									while((line = br.readLine()) != null) {
										Editor.getInstance().getUIState().editorOut.println(line);
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
				
				MenuItem windowClassList = new MenuItem("Class list");
				windowClassList.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						state.openClassListWindow();
					}
				});
				window.addItem(windowClassList);
				
				MenuItem windowAssetExplorer = new MenuItem("Assets explorer");
				windowAssetExplorer.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						state.openAssetExplorerWindow();
					}
				});
				window.addItem(windowAssetExplorer);
				
				MenuItem windowObjectProperties = new MenuItem("Object properties");
				windowObjectProperties.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						state.openObjectPropertiesWindow();
					}
				});
				window.addItem(windowObjectProperties);
				
				MenuItem windowConsole = new MenuItem("Console");
				windowConsole.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						state.openConsoleWindow();
					}
				});
				window.addItem(windowConsole);
				
				addMenu(window);
	}

}
