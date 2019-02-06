package br.com.lunacore.ui;


import java.lang.reflect.Field;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisTable;

import br.com.lunacore.Editor;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.custom.EditorRuler;
import br.com.lunacore.custom.EditorWindow;
import br.com.lunacore.custom.ObjectPresets;
import br.com.lunacore.custom.window.LireObjectPropertiesUI;
import br.com.lunacore.custom.window.ObjectHierarchy;
import br.com.lunacore.helper.Helper;

public class LireSceneViewport extends Stage{
	
	//ScreenCam: The absolute position, 0 being top left
	//UICam: The Scene2D Camera position, fixed, 0 beign bottom left
	//WorldCam: the editor world camera, can be moved and zoomed by interaction, is centered ad (0, 0)
	

	ShapeRenderer sr;
	
	boolean selecting = false;
	Rectangle screenSelection = new Rectangle();
	
	//Positions in ScreenCam
	Vector3 selectionBegin = new Vector3();
	Vector3 selectionEnd = new Vector3();
	
	ArrayList<EditorLireObject> objectsInScene;
	ArrayList<EditorLireObject> localSelection;
		
	Texture translateIcon;
	Texture rotateIcon;

	ShaderProgram selectionShader;
	FrameBuffer selectionBuffer;
	
	OrthographicCamera worldCamera;
	
	VisTable root;
	
	float lastX = -1;
	float lastY = -1;
	
	TopBar topBar;
	
	//Here the scenario is just an object that will remain at (0, 0)
	//And at fullscreen so i can drop things into the scene
	Actor draggableTable;
	
	enum ManipulationMode{
		TRANSLATE,
		ROTATE
	}
	
	ManipulationMode manipulationMode = ManipulationMode.TRANSLATE;
	
	//TODO: Constructor
	public LireSceneViewport(Viewport UIViewport) {
		super(UIViewport);
						
		worldCamera = new OrthographicCamera();
		worldCamera.setToOrtho(false, 1920, 1080);
		worldCamera.position.set(0, 0, 0);
		
		translateIcon = new Texture("icons/translate.png");
		rotateIcon = new Texture("icons/rotate.png");
		
		selectionBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		selectionShader = new ShaderProgram(Gdx.files.internal("shaders/vertex.vs"), Gdx.files.internal("shaders/border.fs"));
		ShaderProgram.pedantic = false;
		
		if(selectionShader.getLog().length() > 0) {
			System.out.println(selectionShader.getLog());
		}
		
		sr = new ShapeRenderer();
		objectsInScene = new ArrayList<EditorLireObject>();
		localSelection = new ArrayList<EditorLireObject>();

		root = new VisTable();
		addActor(root);
		root.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		root.add((topBar = new TopBar(Editor.getInstance().getUIState())).getTable()).fillX().expandX().row();

		draggableTable = new Actor();
		
		VisTable tbl = new VisTable();
		tbl.setPosition(0, 0);
		tbl.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - topBar.getTable().getHeight());
		tbl.align(Align.topLeft);
		//tbl.debugAll();
		
		tbl.add();
		tbl.add(new EditorRuler(false)).growX().height(20).row();
		tbl.add(new EditorRuler(true)).growY().width(20);
		
		tbl.add(draggableTable).grow();
		
		addTargetToDraggableTable();
		
		root.add(tbl).grow();
		addDraggableTableListeners();

	}
	
	public void addDraggableTableListeners() {

		draggableTable.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if(!Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
					Editor.getInstance().unselectAll();
				}
				super.clicked(event, x, y);
			}
		});
		draggableTable.addListener(new DragListener() {
			@Override
			public void dragStart(InputEvent event, float x, float y, int pointer) {
				selecting = true;
				
				//Selection is in ScreenCam
				selectionBegin.set(
						Gdx.input.getX(),
						Gdx.input.getY(), 0);
				selectionEnd.set(selectionBegin); 
				
				super.dragStart(event, x, y, pointer);
			}
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				selectionEnd.set(
						Gdx.input.getX(),
						Gdx.input.getY(), 0);
				
				//Selection is in ScreenCam
				float minX = Math.min(selectionBegin.x, selectionBegin.x);
				float minY = Math.min(selectionBegin.y, selectionBegin.y);
				float maxX = Math.max(selectionEnd.x, selectionEnd.x);
				float maxY = Math.max(selectionEnd.y, selectionEnd.y);
				
				Rectangle bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
				localSelection.clear();
				
				//Actor bounds are in UICam
				for(EditorLireObject ob : objectsInScene) {
					Rectangle r = new Rectangle(ob.getX(), Gdx.graphics.getHeight() - ob.getY() - ob.getHeight(), ob.getWidth(), ob.getHeight());
					
					if(bounds.overlaps(r)) {
						localSelection.add(ob);
					}
				}
				
				super.drag(event, x, y, pointer);
			}
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer) {
				selecting = false;
								
				if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
					Editor.getInstance().addSelectedObjects(localSelection);
				}
				else {
					Editor.getInstance().setSelectedObjects(localSelection);
				}
				
				super.dragStop(event, x, y, pointer);
			}
		});
	}
	
	public void refresh(Viewport viewport) {
		clear();
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		root = new VisTable();
		root.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		root.align(Align.top);
		root.add((topBar = new TopBar(Editor.getInstance().getUIState())).getTable()).fillX().expandX().row();
	
		VisTable tbl = new VisTable();
		tbl.setPosition(0, 0);
		tbl.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - topBar.getTable().getHeight());
		tbl.align(Align.topLeft);
		//tbl.debugAll();
		
		tbl.add();
		tbl.add(new EditorRuler(false)).growX().height(20).row();
		tbl.add(new EditorRuler(true)).growY().width(20);
		
		draggableTable.clearListeners();
		addDraggableTableListeners();
		tbl.add(draggableTable).grow();
		
		root.add(tbl).grow();

		addActor(root);

		selectionBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		setViewport(viewport);
		for(EditorLireObject obj : getObjectsInScene()) {
			addActor(obj);
			addLireObjectListener(obj);
			obj.toFront();
		}
		draggableTable.toBack();
	}
	
	public void resetScene() {
		for(int i = objectsInScene.size() - 1; i >= 0; i --) {
			removeLireObject(objectsInScene.get(i));
		}
		objectsInScene.clear();
	}
	
	//TODO: Update methods
	public void act(float delta) {
		super.act(delta);
		//Cenario needs to be clicked, handled by UICam, that why it needs UICam positioning
		worldCamera.update();
		
		
		//Updates the actor positioning into the actor to scene2d
		for(EditorLireObject lo : objectsInScene) {
			
			Rectangle r = lo.getLimits();
			
			Vector3 boundMin = worldCamera.project(new Vector3(r.x, r.y, 0));
			Vector3 boundMax = worldCamera.project(new Vector3(r.x + r.width, r.y + r.height, 0));
			
			lo.setBounds(boundMin.x, boundMin.y, boundMax.x - boundMin.x, boundMax.y - boundMin.y);
			lo.setRotation(lo.getFinalTransform().getAngle());
			lo.setOrigin(lo.getWidth()/2f, lo.getHeight()/2f);
		}
	}
	
	public void draw() {
		//Draw guide lines
		
		//Here i need to draw in world pos, but i want line thickness constant,
		//so i will draw in ScreenCam by projecting WorldCam to it
		
		sr.setProjectionMatrix(Helper.getScreenProjection());
		sr.setAutoShapeType(true);
		sr.begin();
		
		sr.set(ShapeType.Line);
		sr.setColor(new Color(17/255f, 26/255f, 36/255f, 1f).lerp(Color.WHITE, 0.1f));
		
		Vector3 cenarioScreen = Helper.projectIntoScreenPos(worldCamera, new Vector3(0, 0, 0));
		
		sr.line(
				new Vector2(cenarioScreen.x, 0),
				new Vector2(cenarioScreen.x, Gdx.graphics.getHeight())
				);
			
		sr.line(
				new Vector2(0, cenarioScreen.y),
				new Vector2(Gdx.graphics.getWidth(), cenarioScreen.y)
				);
	
		//The selection rectangle also needs to be draw in ScreenProjection
		sr.setColor(Color.WHITE);
			
		if(selecting) {
			
			//Luckly, Selection is already in ScreenCam
			float minX = Math.min(selectionBegin.x, selectionEnd.x);
			float minY = Math.min(selectionBegin.y, selectionEnd.y);
			float maxX = Math.max(selectionBegin.x, selectionEnd.x);
			float maxY = Math.max(selectionBegin.y, selectionEnd.y);
	
			screenSelection.set(minX, minY, maxX - minX, maxY - minY);
			
			sr.setColor(Color.WHITE);
			sr.rect(minX, minY, maxX - minX, maxY - minY);

			localSelection.clear();
			
			//Actor bounds are in UICam
			for(EditorLireObject ob : objectsInScene) {
				Rectangle r = new Rectangle(ob.getX(), Gdx.graphics.getHeight() - ob.getY() - ob.getHeight(), ob.getWidth(), ob.getHeight());
								
				if(r.overlaps(screenSelection)) {
					localSelection.add(ob);
				}
			}
		}
		sr.setColor(Color.WHITE);
		sr.end();
		
		//Placeholder
		getBatch().setProjectionMatrix(getCamera().combined);
		for(EditorLireObject ob : getObjectsInScene()) {
			if(Editor.getInstance().getSelectedObjects().contains(ob)) {
				ob.setSelectionVisible(true);
				ob.drawSelected(getBatch(), worldCamera);
			}
			else {
				ob.setSelectionVisible(false);
			}
		}		

		//I need to draw all the LireObjects using WorldCam
		getBatch().setProjectionMatrix(worldCamera.combined);
		getBatch().begin();
		for(EditorLireObject lo : objectsInScene) {
			lo.setShowing(true);
			lo.drawToEditor(getBatch(), 1);
			lo.setShowing(false); //This is for the Scene2D Stage to don't draw the objects again (with HIS projection)
		}
		getBatch().end();
		
		//Draw the selected objects also using WorldCam in a FrameBuffer, so the border shader can be applied
		selectionBuffer.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		getBatch().begin();
		for(EditorLireObject lo : Editor.getInstance().getSelectedObjects()) {
			lo.setShowing(true);
			lo.draw(getBatch(), 1);
			lo.setShowing(false);
		}
		getBatch().end();

		selectionBuffer.end();
		
		//Here i need to change to ScreenCam so i can draw the FrameBuffer
		selectionShader.begin();
		getBatch().setShader(selectionShader);
		getBatch().setProjectionMatrix(Helper.getScreenProjection());
		getBatch().begin();
		
		selectionShader.setUniformf("borderColor", Color.NAVY);
		selectionShader.setUniformf("borderSize", 0.002f);
		
		getBatch().draw(selectionBuffer.getColorBufferTexture(), 0, 0);
		
		getBatch().setShader(null);
		getBatch().end();
		selectionShader.end();

		//And now, over every object, the manipulation icons (translate, rotate, scale)
		//This one need to be drawed in UICamera, because SpriteBatch Textures are y-flipped by default
		//So i dont want upside down images
		//(OBS: if you didnt realize, UICam is just ScreenCam upside down)
		getBatch().setProjectionMatrix(getCamera().combined);
		getBatch().begin();
		for(EditorLireObject lo : Editor.getInstance().getSelectedObjects()) {

			Texture icon = translateIcon;
			if(manipulationMode == ManipulationMode.ROTATE) {
				icon = rotateIcon;
			}
			
			//Here i need to transform the Object WorldPos to UIPos
			
			Vector3 pos = worldCamera.project(new Vector3(lo.getFinalTransform().getPosition(), 0));
			//A trick to convert from ScreenCam to UICam is just invert the y position
			//i dont need to call project
			//pos.y = Gdx.graphics.getHeight() - pos.y;
			
			getBatch().draw(icon,
					(pos.x - icon.getWidth()/2f),
					(pos.y - icon.getHeight()/2f),
					icon.getWidth()/2f,
					icon.getHeight()/2f,
					icon.getWidth(),
					icon.getHeight(),
					0.2f,
					0.2f,
					0,
					0,
					0,
					icon.getWidth(),
					icon.getHeight(),
					false,
					false
					);
		}
		getBatch().end();
		
		//Camera needs to be in UICam to draw the other objects
		super.draw();		
	}
	
	public ArrayList<EditorLireObject> getObjectsInScene(){
		return objectsInScene;
	}
	
	public void addLireObjectListener(final EditorLireObject lr) {
		lr.addListener(new ClickListener(){
					
					boolean dragged = false;
					
					public void clicked(InputEvent event, float x, float y) {
						if(!dragged) {
								if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
									if(Editor.getInstance().getSelectedObjects().contains(lr)) {
										Editor.getInstance().unselect(lr);
									}
									else {
										Editor.getInstance().addSelectedObject(lr);
									}
								}
								else {
									Editor.getInstance().setSelectedObject(lr);
								}
						}
						dragged = false;
					}
					
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
						if(!Editor.getInstance().getSelectedObjects().contains(lr) &&
								!Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && button == Buttons.LEFT) {
							Editor.getInstance().setSelectedObject(lr);
						}
						return super.touchDown(event, x, y, pointer, button);
					}
					
					public void touchDragged(InputEvent event, float x, float y, int pointer) {
						dragged = true;
						super.touchDragged(event, x, y, pointer);
					}
				});
		DragListener dl = new DragListener() {
			
			float lastX = -1;
			float lastY = -1;
			Vector2 startDragPoint = new Vector2();
			public void dragStart(InputEvent event, float x, float y, int pointer) {
				//This is in ScreenCam
				startDragPoint.set(
						Gdx.input.getX(pointer), 
						Gdx.input.getY(pointer)
						);
				super.dragStart(event, x, y, pointer);
			}
		
			public void drag(InputEvent event, float x, float y, int pointer) {
				if(Gdx.input.isButtonPressed(Buttons.LEFT) && Editor.getInstance().getSelectedObjects().contains(lr)) {
					if(lastX != -1 && lastY != -1) {
						float dx = Gdx.input.getX(pointer) - lastX;
						float dy = Gdx.input.getY(pointer) - lastY;
						
						switch(manipulationMode) {
						case TRANSLATE:
							for(EditorLireObject l : Editor.getInstance().getSelectedObjects()) {
								
								Vector2 targetPosition = l.getFinalTransform().getPosition().cpy().add(
										dx * worldCamera.zoom,
										-dy * worldCamera.zoom);
											
								l.getTransform().setPosition(l.projectPosition(targetPosition, new Vector2()));
								
							}
							break;
						case ROTATE:
							
							//For me to get the angle between the mousepos and the object
							//i need to convert the mousePos to WorldCam first
							Vector3 worldMousePos = worldCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
							float ang1 = Helper.xy(worldMousePos).sub(lr.getFinalTransform().getPosition()).angle();
							
							//I also need to project the lastX coordinates
							//Recycling is healthy
							worldMousePos = worldCamera.unproject(new Vector3(lastX, lastY, 0));
							float ang2 = Helper.xy(worldMousePos)/*Swizzles*/.sub(lr.getFinalTransform().getPosition()).angle();
							
							for(EditorLireObject l : Editor.getInstance().getSelectedObjects()) {
								l.getTransform().setAngle(l.getTransform().getAngle() + (ang1 - ang2));
							}
							break;
						}
						
						
						Editor.getInstance().getUIState().getSceneManager().unsave();
						Editor.getInstance().getUIState().refreshWindow(LireObjectPropertiesUI.class);
		
					}
					//ScreenCam
					lastX = Gdx.input.getX(pointer);
					lastY = Gdx.input.getY(pointer);
				}
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				if(button == Buttons.LEFT) {
					lastX = -1;
					lastY = -1;
				}
			}
		};
		Editor.getInstance().getDragAndDrop().addTarget(new Target(lr) {
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				if(payload.getObject() instanceof FileHandle) {
					FileHandle fh = (FileHandle) payload.getObject();
					return fh.name().endsWith(".lo");
				}
				return false;
			}
		
			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				FileHandle fh = (FileHandle) payload.getObject();
				
				if(fh != null) {
					if(fh.name().endsWith(".lo")) {
						addLireObject(Editor.getInstance().getStage().loadObject(fh, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
						Editor.getInstance().getUIState().getSceneManager().unsave();
						Editor.getInstance().getUIState().refreshWindow(ObjectHierarchy.class);
					}
				}
			}
		});
		
		dl.setTapSquareSize(0);
		lr.addListener(dl);
	}
	
	//TODO: Add Lire object
	public void addLireObject(final EditorLireObject lr) {
		addLireObjectListener(lr);
		Editor.getInstance().setSelectedObject(lr);
		addActor(lr);
		objectsInScene.add(lr);
	}
	
	public void removeLireObject(EditorLireObject obj) {
		//Neither this
		//cenario.removeChildren(obj);
		objectsInScene.remove(obj);
		obj.remove();
		obj.dispose();
		Editor.getInstance().unselect(obj);
		Editor.getInstance().getUIState().refreshWindow(ObjectHierarchy.class);
		Editor.getInstance().getUIState().getSceneManager().unsave();
	}
	
	public EditorLireObject loadObject(FileHandle handle, float x, float y) {
		final EditorLireObject lr = new EditorLireObject(new XmlReader().parse(handle), Editor.getInstance().getCurrentProject().child("core/assets"), null, this);
		//Here i need to put the object in the mouse position, so here we go again
		Vector3 worldPos = worldCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		lr.getTransform().setPosition(worldPos.x, worldPos.y);
		return lr;
	}
	
	//TODO: Specific sittuation functions (mostly used just once in the code, but to big to be put where it should be)
	public void addTargetToDraggableTable() {
		Editor.getInstance().getDragAndDrop().addTarget(new Target(draggableTable) {
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				if(payload.getObject() instanceof FileHandle) {
					FileHandle fh = (FileHandle) payload.getObject();
					return true;
				}
				return false;
			}

			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				FileHandle fh = (FileHandle) payload.getObject();
				
				if(fh != null) {
					if(fh.name().endsWith(".lo")) {
						addLireObject(Editor.getInstance().getStage().loadObject(fh, Gdx.input.getX(), Gdx.input.getY()));
						Editor.getInstance().getUIState().getSceneManager().unsave();
						Editor.getInstance().getUIState().refreshWindow(ObjectHierarchy.class);
					}
					else if(fh.name().endsWith(".png") || fh.name().endsWith(".jpg")) {
						addLireObject(ObjectPresets.getSpritePreset(fh));
						Editor.getInstance().getUIState().getSceneManager().unsave();
						Editor.getInstance().getUIState().refreshWindow(ObjectHierarchy.class);
					}
				}
			}
			
		});
	}
	
	public void refreshObjectParams(Class oldClass, Class newClass) {
		for(EditorLireObject obj : getObjectsInScene()) {
			if(obj.getChildClass() == null) continue;
			if(obj.getChildClass().getCanonicalName().equals(oldClass.getCanonicalName())) {
				obj.setChildClass(newClass);
				
				for(String s : obj.getParams().keySet()) {
					try {
						newClass.getField(s);
					}catch(NoSuchFieldException e) {
						obj.removeParam(s);
						//Learn portuguese
						System.out.println("Achei um campo que n�o existe mais na classe: " + s);
					}
				}
				
				for(Field f : newClass.getFields()) {
					if(obj.getParam(f.getName()) == null) {
						obj.setParam(f.getName(), "");
						//Brazilian, not Portugal
						System.out.println("Achei um campo q existe na classe mas nao existe ainda no obj: " + f.getName());
					}
				}
			}
		}
	}
	
	//TODO: Input Handling
	@Override
	public boolean keyDown(int keyCode) {
		
		
		if(keyCode == Keys.FORWARD_DEL) {
			ArrayList<EditorLireObject> objects = Editor.getInstance().getSelectedObjects();
			for(int i = objects.size() - 1; i >= 0; i --) {
				EditorLireObject lo = objects.get(i);
				if(getKeyboardFocus() instanceof EditorWindow) {
					removeLireObject(lo);
					Editor.getInstance().unselectAll();
				}
			}
			
		}
		if(keyCode == Keys.W) {
			if(getKeyboardFocus() instanceof EditorWindow) {
				manipulationMode = ManipulationMode.TRANSLATE;
			}
		}
		if(keyCode == Keys.E) {
			if(getKeyboardFocus() instanceof EditorWindow) {
				manipulationMode = ManipulationMode.ROTATE;
			}
		}
		return super.keyDown(keyCode);
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(Gdx.input.isButtonPressed(Buttons.MIDDLE)) {
			
			if(lastX != -1 && lastY != -1) {
				float dx = screenX - lastX;
				float dy = screenY - lastY;
				
				//dx and dy need to be scale acordingly
				worldCamera.position.sub(
						dx * (1920 / Gdx.graphics.getWidth()) * worldCamera.zoom,
						-dy * (1080 / Gdx.graphics.getHeight()) * worldCamera.zoom, 0);
				worldCamera.update();
			}
			
			lastX = screenX;
			lastY = screenY;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Buttons.MIDDLE) {
			lastX = -1;
			lastY = -1;
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}

	public boolean scrolled(int amount) {
		
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			float amtDiv = 1;
			
			if(amount > 0)
				amtDiv = 0.9f;
			else
				amtDiv = 1.1f;
			
			worldCamera.zoom /= amtDiv;
		}

		return super.scrolled(amount);
	}

	public OrthographicCamera getWorldCamera() {
		return worldCamera;
	}



}
