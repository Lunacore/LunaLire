package br.com.lunacore.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import br.com.lunacore.Editor;
import br.com.lunacore.UIState;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.lunalire.LireScene;

public class ClassPropertyUI extends VisTable{

	UIState state;
	Class selection;
	EditorLireObject reference;
	
	public ClassPropertyUI(UIState state, Class selection, EditorLireObject reference) {
		super();
		this.reference = reference;
		this.selection = selection;
		this.state = state;
		try {
			construct();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void construct() throws IllegalArgumentException, IllegalAccessException {			
		if(selection != null) {
			
			align(Align.topLeft);

			
			VisLabel title = new VisLabel(selection.getSimpleName());
			add(title).align(Align.top).growX();
			
			for(final Field field : selection.getFields()) {
				row();
				
				add(new VisLabel(field.getName())).pad(5).align(Align.topLeft);
				
				//Float
				if(field.getType().equals(float.class)) {
					final VisValidatableTextField val = new VisValidatableTextField(new InputValidator() {
						public boolean validateInput(String input) {
							try {
								Float.parseFloat(input);
								return true;
							}
							catch(Exception e) {
								return false;
							}
						}
					});
					val.addListener(new ChangeListener() {
						public void changed(ChangeEvent event, Actor actor) {
							if(val.isInputValid()) {
								reference.setParam(field.getName(), val.getText());
							}
						}
					});
					val.setText(reference.getParam(field.getName()));
					add(val).pad(5);
				}
				//Int
				else if(field.getType().equals(int.class)) {
					final VisValidatableTextField val = new VisValidatableTextField(new InputValidator() {
						public boolean validateInput(String input) {
							try {
								Integer.parseInt(input);
								return true;
							}
							catch(Exception e) {
								return false;
							}
						}
					});
					val.addListener(new ChangeListener() {
						public void changed(ChangeEvent event, Actor actor) {
							if(val.isInputValid()) {
								reference.setParam(field.getName(), val.getText());
							}
						}
					});
					val.setText(reference.getParam(field.getName()));
					add(val).pad(5);
				}
				//String
				else if(field.getType().equals(String.class)) {
					final VisTextField val = new VisTextField();
					val.setText(reference.getParam(field.getName()));
					val.addListener(new ChangeListener() {
						public void changed(ChangeEvent event, Actor actor) {
							reference.setParam(field.getName(), val.getText());
						}
					});
					add(val).pad(5);
				}
				//Texture
				else if(field.getType().equals(Texture.class)) {
					final VisTextField val = new VisTextField("");
					val.setDisabled(true);
				
					Editor.getInstance().getDragAndDrop().addTarget(new Target(val) {
						public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
							if(payload.getObject() instanceof FileHandle) {
								FileHandle fh = (FileHandle) payload.getObject();
								return fh.name().endsWith(".png") || fh.name().endsWith(".jpg");
							}
							return false;
						}

						public void drop(Source source, Payload payload, float x, float y, int pointer) {
							FileHandle fh = (FileHandle) payload.getObject();
							val.setText(getLocalPath(fh));
							reference.setParam(field.getName(), getLocalPath(fh));
						}
					});
					
					val.setText(reference.getParam(field.getName()));
					add(val).pad(5).align(Align.topLeft);
				}
				else if(field.getType().equals(boolean.class)) {
					final VisCheckBox check = new VisCheckBox("");
					check.setChecked(Boolean.getBoolean(reference.getParam(field.getName())));
					check.addListener(new ChangeListener() {
						public void changed(ChangeEvent event, Actor actor) {
							reference.setParam(field.getName(), check.isChecked() + "");
						}
					});
					add(check).pad(5);
				}
				else if(field.getType().equals(FileHandle.class)) {
					final VisTextField val = new VisTextField("");
					val.setDisabled(true);
				
					Editor.getInstance().getDragAndDrop().addTarget(new Target(val) {
						public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
							if(payload.getObject() instanceof FileHandle) {
								return true;
							}
							return false;
						}

						public void drop(Source source, Payload payload, float x, float y, int pointer) {
							FileHandle fh = (FileHandle) payload.getObject();
							val.setText(getLocalPath(fh));
							reference.setParam(field.getName(), getLocalPath(fh));
						}
					});
					
					val.setText(reference.getParam(field.getName()));
					add(val).pad(5).align(Align.topLeft);
				}
				else {
					VisTextField val = new VisTextField();
					val.setDisabled(true);
					val.setText(field.getType().getSimpleName());
					add(val).pad(5).align(Align.topLeft);
				}
			}
				
			VisTextButton rmv = new VisTextButton("Remove class");
			rmv.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					reference.setChildClass(null);
					Editor.getInstance().getUIState().refreshObjectProperties();
				}
			});
			row();
			add(rmv).colspan(2).growX();
			
			//add(new VisTable()).grow();
		}
	}
	
	static public String getLocalPath(FileHandle handle) {
		String[] aps = handle.path().split("/");
		boolean found = false;
		String finalString = "";
		for(String s : aps) {
			if(found) {
				finalString += "/" + s;
			}			
			if(s.equals("assets")) {
				found = true;
			}
		}		
		return finalString.substring(1);
	}
	
	
	public void refresh() {
		clear();
		try {
			construct();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	

	public void setClassSelection(Class c) {
		selection = c;
		refresh();
	}
	
}