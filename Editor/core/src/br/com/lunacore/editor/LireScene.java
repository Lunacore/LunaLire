package br.com.lunacore.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import br.com.lunacore.editor.KeyMapper.Device;
import br.com.lunacore.editor.components.CameraComponent;

@SuppressWarnings("rawtypes")
public class LireScene {
	
	ArrayList<LireObject> objects;
	Stage stage;
	LireObject cameraActor;
	
	public LireScene(Element root) {
		
		//Por enquanto carrega uma camera padr�o, mas dps tenho q criar 
		//o componente de objeto de camera
		OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage = new Stage(new ScreenViewport(camera));
		objects = new ArrayList<LireObject>();
		camera.position.set(0, 0, 0);
		
		for(Element el : root.getChildrenByName("object")) {
			loadLireObject(el);
		}
	}
	
	@SuppressWarnings("unchecked")
	private LireObject loadLireObject(Element element) {
		LireObject lo = null;
		if(element.getAttribute("class", null) != null) {
			try {
				Class c = LunaLireStarter.lireForName(element.getAttribute("class"));
				lo = (LireObject) c.getConstructor(Element.class, FileHandle.class, LireScene.class).newInstance(element, null, this);
				
				for(Field f : c.getFields()) {
					Element custom = element.getChildByName("custom");
					
					if(custom.getAttribute(f.getName(), null) != null) {
						
						if(f.getType().equals(float.class)) {
							f.set(lo, custom.getFloat(f.getName()));
						}
						else if(f.getType().equals(int.class)) {
							f.set(lo, custom.getInt(f.getName()));
						}
						else if(f.getType().equals(String.class)) {
							f.set(lo, custom.get(f.getName()));
						}
						else if(f.getType().equals(Texture.class)) {
							f.set(lo, new Texture(custom.get(f.getName())));
						}
						else {
							//Classe n�o suportada (por enquanto)
						}
						
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			lo = new LireObject(element, null, this);
		}
		
		
		System.out.println("Loading object " + lo.getName());
		stage.addActor(lo);
		objects.add(lo);
		
		if(lo.getComponent(CameraComponent.class) != null) {
			setCameraActor(lo);
		}
		
		for(Element child : element.getChildrenByName("object")) {
			loadLireObject(child).setParent(lo);
		}
		return lo;
	}
	
	public ArrayList<LireObject> getObjectsByName(String name){
		ArrayList<LireObject> ret = new ArrayList<LireObject>();
		for(LireObject l : objects) {
			if(l.getName().equals(name)) {
				ret.add(l);
			}
		}
		return ret;
	}
	
	public ArrayList<LireObject> getObjectsByComponent(Class component){
		ArrayList<LireObject> ret = new ArrayList<LireObject>();
		for(LireObject l : objects) {
			if(l.getComponent(component) != null) {
				ret.add(l);
			}
		}
		return ret;
	}
	
	public ArrayList<LireObject> findObjects(ObjectFinder finder){
		ArrayList<LireObject> ret = new ArrayList<LireObject>();
		for(LireObject l : objects) {
			if(finder.accept(l)) {
				ret.add(l);
			}
		}
		return ret;
	}

	public LireObject findFirstObject(ObjectFinder finder){
		for(LireObject l : objects) {
			if(finder.accept(l)) {
				return l;
			}
		}
		return null;
	}
	
	public abstract class ObjectFinder{
		public abstract boolean accept(LireObject object);
	}
	
	public void setCameraActor(LireObject actor) {
		if(actor.getComponent(CameraComponent.class) != null) {
			cameraActor = actor;
			setCamera(((CameraComponent) actor.getComponent(CameraComponent.class)).getCamera());
		}
	}

	public void render(SpriteBatch sb) {
		stage.getActors().sort(new Comparator<Actor>() {
			public int compare(Actor o1, Actor o2) {
				LireObject obj1 = (LireObject)o1;
				LireObject obj2 = (LireObject)o2;

				return obj1.zorder - obj2.zorder;
			}});
		stage.draw();
	}
	
	public void update(float delta) {
		stage.act(delta);
		stage.getViewport().apply();
	}
	
	public LireObject getCameraActor() {
		return cameraActor;
	}


	public void setCamera(OrthographicCamera camera2) {
		stage.getViewport().setCamera(camera2);
		stage.getViewport().setWorldSize(camera2.viewportWidth, camera2.viewportHeight);
	}

	public void dispose() {
		for(LireObject l : objects) {
			l.dispose();
		}
		objects.clear();
		stage.clear();
	}
	
	public void keyDown(int keycode) {
		for(LireObject l : objects) {
			l.keyDown(keycode);
		}		
	}

	public void keyUp(int keycode) {
		for(LireObject l : objects) {
			l.keyUp(keycode);
		}		
	}

	public void keyTyped(char character) {
		for(LireObject l : objects) {
			l.keyTyped(character);
		}		
	}

	public void touchDown(int screenX, int screenY, int pointer, int button) {
		for(LireObject l : objects) {
			l.touchDown(screenX, screenY, pointer, button);
		}		
	}

	public void touchUp(int screenX, int screenY, int pointer, int button) {
		for(LireObject l : objects) {
			l.touchUp(screenX, screenY, pointer, button);
		}		
	}

	public void touchDragged(int screenX, int screenY, int pointer) {
		for(LireObject l : objects) {
			l.touchDragged(screenX, screenY, pointer);
		}		
	}

	public void mouseMoved(int screenX, int screenY) {
		for(LireObject l : objects) {
			l.mouseMoved(screenX, screenY);
		}		
	}

	public void scrolled(int amount) {
		for(LireObject l : objects) {
			l.scrolled(amount);
		}		
	}

	public void connected(Controller controller) {
		for(LireObject l : objects) {
			l.connected(controller);
		}		
	}

	public void disconnected(Controller controller) {
		for(LireObject l : objects) {
			l.disconnected(controller);
		}		
	}

	public void buttonDown(Controller controller, int buttonCode) {
		for(LireObject l : objects) {
			l.buttonDown(controller, buttonCode);
		}		
	}

	public void buttonUp(Controller controller, int buttonCode) {
		for(LireObject l : objects) {
			l.buttonUp(controller, buttonCode);
		}		
	}

	public void axisMoved(Controller controller, int axisCode, float value) {
		for(LireObject l : objects) {
			l.axisMoved(controller, axisCode, value);
		}		
	}

	public void povMoved(Controller controller, int povCode, PovDirection value) {
		for(LireObject l : objects) {
			l.povMoved(controller, povCode, value);
		}		
	}

	public void xSliderMoved(Controller controller, int sliderCode, boolean value) {
		for(LireObject l : objects) {
			l.xSliderMoved(controller, sliderCode, value);
		}
		
	}

	public void ySliderMoved(Controller controller, int sliderCode, boolean value) {
		for(LireObject l : objects) {
			l.ySliderMoved(controller, sliderCode, value);
		}		
	}

	public void accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		for(LireObject l : objects) {
			l.accelerometerMoved(controller, accelerometerCode, value);
		}		
	}

	public void inputIn(Device device, String mapName) {
		for(LireObject l : objects) {
			l.inputIn(device, mapName);
		}		
	}

	public void inputOut(Device device, String mapName) {
		for(LireObject l : objects) {
			l.inputOut(device, mapName);
		}		
	}

	

}