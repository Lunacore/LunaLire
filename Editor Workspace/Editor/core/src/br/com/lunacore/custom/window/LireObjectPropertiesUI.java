package br.com.lunacore.custom.window;

import java.net.MalformedURLException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import br.com.lunacore.Editor;
import br.com.lunacore.UIState;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireComponent.RefreshEvent;
import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.components.CameraComponent;
import br.com.lunacore.lunalire.components.ShapeRendererComponent;
import br.com.lunacore.lunalire.components.SpriteComponent;
import br.com.lunacore.lunalire.utils.InputValidatorConstants;
import br.com.lunacore.lunalire.utils.UIConstants;

public class LireObjectPropertiesUI extends LireEditorWindow{
	
	ClassPropertyUI classPropertyUI;
	
	public LireObjectPropertiesUI() {
		construct();
		
	}
	
	public void refresh() {
		clear();
		construct();
	}
	
	public void construct() {

		if(Editor.getInstance().getSelectedObjects().size() == 1) {
			final EditorLireObject obj = Editor.getInstance().getSelectedObjects().get(0);
		
			align(Align.topLeft);
			pad(5);
			
			FocusListener selectAll = new FocusListener() {
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if(focused) {
						VisTextField vtf = (VisTextField) actor;
						vtf.selectAll();
					}
					super.keyboardFocusChanged(event, actor, focused);
				}
			};
			
			
			final VisTable main = new VisTable();
			
			main.align(Align.topLeft);
			
			VisLabel nameLbl = new VisLabel("Name");
			main.add(nameLbl).pad(10);
			
			final VisTextField nameTxt = new VisTextField(obj.getName());
			nameTxt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					obj.setName(nameTxt.getText());
					Editor.getInstance().getUIState().refreshWindow(ObjectHierarchy.class);
				}
			});
			main.add(nameTxt).pad(10);
			main.row();

			VisLabel zLbl = new VisLabel("Z Order");
			main.add(zLbl).pad(10);
			
			final VisValidatableTextField zTxt = new VisValidatableTextField(obj.getZOrder() + "");
			zTxt.addValidator(InputValidatorConstants.intValidator);
			zTxt.addListener(selectAll);
			zTxt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					if(zTxt.isInputValid()) {
						int z = Integer.parseInt(zTxt.getText());
						obj.setZOrder((int)Math.max(z, 0));
					}
				}
			});
			main.add(zTxt).pad(10).row();
			
			main.add(new VisLabel("Lock")).pad(5);
			
			final VisCheckBox lockBox = new VisCheckBox("");
			lockBox.setChecked(obj.isLocked());
			
			lockBox.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					obj.setLocked(lockBox.isChecked());
				}
			});
			
			main.add(lockBox).pad(5).row();
						
			main.add(new VisLabel("Custom class")).pad(5);
			final VisTextField classLbl = new VisTextField("");
		
			if(obj.getChildClass() != null) {
				classLbl.setText(obj.getChildClass().getCanonicalName());
			}
			
			classLbl.setDisabled(true);
			
			Editor.getInstance().getDragAndDrop().addTarget(new Target(classLbl) {
				public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
					if(!(payload.getObject() instanceof Class)) return false;
					Class c = (Class) payload.getObject();
									
					return Editor.getInstance().isSubclassOf(c, LireObject.class);
				}

				public void drop(Source source, Payload payload, float x, float y, int pointer) {
					Class c = (Class) payload.getObject();
					if(LireObject.class.isAssignableFrom(c)) {
						classLbl.setText(c.getCanonicalName());
						obj.setChildClass(c);
						try {
							Editor.getInstance().getUIState().getSceneManager().compileAll(Editor.getInstance().getCurrentProject().child("core/src"));
							Editor.getInstance().getUIState().getSceneManager().saveScenes();
							//Atualizar objetos pegando os params
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
						Editor.getInstance().getUIState().refreshWindow(LireObjectPropertiesUI.class);
						classPropertyUI.refresh();
						refresh();
						classPropertyUI.refresh();
					}
					
				}
				
			});
			
			main.add(classLbl).pad(5);
			
			
			if(obj.getChildClass() != null) {
				classPropertyUI = new ClassPropertyUI(Editor.getInstance().getUIState(), obj.getChildClass(), obj);
				main.row();
				main.add(classPropertyUI).colspan(2);
			}
			
			InputValidator floatValidator = new InputValidator() {
				public boolean validateInput(String input) {
					try {
						Float.parseFloat(input);
						return true;
					}
					catch(Exception e) {
						return false;
					}
				}
			};
								
			add(main).growX().pad(5).row();
			add(new Separator()).growX().row();
			
			add(UIConstants.getTransformConfigTable(obj.getTransform())).pad(5).growX().row();
			add(new Separator()).growX().row();
			
			VisTextButton add = new VisTextButton("Add component");
			add.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					
					PopupMenu popup = new PopupMenu();
					
					Class[] compClazz = new Class[] {
							SpriteComponent.class,
							ShapeRendererComponent.class,
							CameraComponent.class
					};
					
					for(final Class c : compClazz) {
						MenuItem menuClass = new MenuItem(c.getSimpleName());
						menuClass.addListener(new ClickListener() {
							public void clicked(InputEvent event, float x, float y) {
								Object comp = obj.addComponent(c);
								if(comp instanceof String) {
									VisDialog dialog = new VisDialog("Component add");
									dialog.addCloseButton();
									dialog.row();
									dialog.add(new VisLabel("Could not add the component " + c.getSimpleName())).pad(5).row();
									dialog.add(new VisLabel("Reason: " + comp)).pad(5);
									
									dialog.pack();
									dialog.centerWindow();
																		
									Editor.getInstance().getUIState().addDialog(dialog);
								}
							};
						});
						popup.addItem(menuClass);
					}
					
					for(final Class c :Editor.getInstance().getUIState().getWindow(ClassListUI.class).getClasses()) {
						if(Editor.getInstance().isSubclassOf(c, LireComponent.class)) {
							MenuItem menuClass = new MenuItem(c.getSimpleName());
							menuClass.addListener(new ClickListener() {
								public void clicked(InputEvent event, float x, float y) {
									Object comp = obj.addComponent(c);
									if(comp instanceof String) {
										VisDialog dialog = new VisDialog("Component add");
										dialog.addCloseButton();
										dialog.row();
										dialog.add(new VisLabel("Could not add the component " + c.getSimpleName())).pad(5).row();
										dialog.add(new VisLabel("Reason: " + comp)).pad(5);
										
										dialog.pack();
										dialog.centerWindow();
																			
										Editor.getInstance().getUIState().addDialog(dialog);
									}
								};
							});
							popup.addItem(menuClass);
						}
					}

					popup.showMenu(getStage(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
					
					event.stop();
					
					//Adiciona um componente
					//pega da lista pronta
					//pega da lista de classes
					super.clicked(event, x, y);
				}
			});
			
			add(add).colspan(getColumns()).growX().row();
			
			int i = 0; 
			for(final LireComponent lc : obj.getComponents()) {
				
				VisTable wrapper = new VisTable();
				wrapper.add(new VisLabel(lc.getClass().getSimpleName())).row();;
				VisTable t = lc.getUITable(Editor.getInstance().getDragAndDrop());
				wrapper.add(t).grow().row();
				
				VisTextButton rmv = new VisTextButton("Remove component");
				rmv.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						//Remove o componente
						
						obj.getComponents().remove(lc);
						refresh();
						
						super.clicked(event, x, y);
					}
				});
				
				wrapper.add(rmv).growX();
				
				t.addListener(new EventListener() {
					public boolean handle(Event event) {
						if(event instanceof RefreshEvent) {
							refresh();
							return true;
						}
						return false;
					}
				});
				add(wrapper).prefHeight(100).pad(5).grow().row();
				
				if(i < obj.getComponents().size()-1)
				add(new Separator()).growX().row();
				
				i++;
			}
		}
		
	}

	@Override
	public String getTitle() {
		return "Object properites";
	}
	

}
