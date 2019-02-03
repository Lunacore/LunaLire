package br.com.lunacore.lunalire;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.XmlReader.Element;

import br.com.lunacore.lunalire.KeyMapper.Device;
import br.com.lunacore.math.Transform;

public class LireObject extends Actor{
	
	protected Transform transform;
	private ArrayList<LireComponent> components;
	private LireObject parent;
	protected ArrayList<LireObject> children;
	private Transform finalTransform;
	protected LireScene scene;
	int zorder;
		
	
	public LireObject(Element root, FileHandle assetsFolder, LireScene scene) {
		this.scene = scene;
		finalTransform = new Transform(Vector2.Zero.cpy());
		transform = new Transform(Vector2.Zero.cpy());
		components = new ArrayList<LireComponent>();
		children = new ArrayList<LireObject>();
		
		if(root != null) {
			try {
				readXml(root, assetsFolder);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	protected void readXml(Element root, FileHandle relativePath) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Element tr = root.getChildByName("transform");
		if(tr == null) {
			System.err.println("No transform found on object " + root.getAttribute("name"));
		}
		else {
			float x = Float.parseFloat(tr.getChildByName("position").getAttribute("x"));
			float y = Float.parseFloat(tr.getChildByName("position").getAttribute("y"));
			transform.getPosition().set(x, y);
			
			float rotation = Float.parseFloat(tr.getChildByName("rotation").getAttribute("value"));
			transform.setAngle(rotation);
			
			float sx = Float.parseFloat(tr.getChildByName("scale").getAttribute("x"));
			float sy = Float.parseFloat(tr.getChildByName("scale").getAttribute("y"));
			transform.getScale().set(sx, sy);
		}
		
		setZOrder(Integer.parseInt(root.getAttribute("z", "0")));
		setName(root.getAttribute("name"));

		for(Element el : root.getChildrenByName("component")) {
			Class lc = Class.forName(el.getAttribute("class"));
			addComponent(lc);
			getComponent(lc).read(el, relativePath);
		}
	}
	
	public void setZOrder(int zorder) {
		this.zorder = zorder;
		setZIndex(zorder);
	}
	
	public int getZOrder() {
		return zorder;
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	public <T extends LireComponent> T getComponent(Class class1){
		for(LireComponent c : components) {
			if(class1.equals(c.getClass())) {
				return (T) c;
			}
		}
		return null;
	}
	
	public void act(float delta) {
		super.act(delta);
		for(LireComponent lc : components) {
			lc.act(delta);
		}
	}
	
	public void draw(Batch batch, float parentAlpha) {
		for(LireComponent lc : components) {
			lc.draw(batch, parentAlpha);
		}
	}
	
	public Transform getFinalTransform() {
		
		if(getLireParent() != null) {
			finalTransform.getPosition().set(
					transform.getPosition().cpy().scl(getLireParent().getFinalTransform().getScale())
					.rotate(getLireParent().getFinalTransform().getAngle())
					.add(getLireParent().getFinalTransform().getPosition())
					);
			
			finalTransform.setAngle(transform.getAngle() + getLireParent().getFinalTransform().getAngle());
			
			finalTransform.setScale(
					transform.getScale().cpy()
					.scl(getLireParent().getFinalTransform().getScale()));
			
			return finalTransform;
		}
		
		return transform;
		
	}
	
	
	public void attachChildren(LireObject object) {
		object.setParent(this);
		children.add(object);
	}
	
	public void removeChildren(LireObject object) {
		object.parent = null;
		children.remove(object);
	}

	public Object addComponent(Class clazz) {
		try {
			LireComponent component = (LireComponent) clazz.newInstance();
			
			if(component.validate(this)) {
				component.setParent(this);
				components.add(component);
				return component;
			}
			else {
				return component.getValidationCause();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public void drawDebug(ShapeRenderer shapes) {
//	}
//	
//	public void debugDraw(ShapeRenderer shapes) {
//		super.drawDebug(shapes);
//	}
	
	public ArrayList<LireObject> getChildren() {
		return children;
	}

	public LireObject getLireParent() {
		return parent;
	}

	public void setParent(LireObject parent) {
		this.parent = parent;
	}

	public ArrayList<LireComponent> getComponents() {
		return components;
	}

	public Element toXmlElement(Element parent) {
		Element root = new Element("object", parent);
		root.setAttribute("name", getName());
		root.setAttribute("z", getZOrder() + "");
		
		Element transform = new Element("transform", root);
		root.addChild(transform);
		
		Element pos = new Element("position", transform);
		transform.addChild(pos);
		pos.setAttribute("x", getTransform().getPosition().x + "");
		pos.setAttribute("y", getTransform().getPosition().y + "");
		
		Element rot = new Element("rotation", transform);
		transform.addChild(rot);
		rot.setAttribute("value", getTransform().getAngle() + "");
		
		Element scl = new Element("scale", transform);
		transform.addChild(scl);
		scl.setAttribute("x", getTransform().getScale().x + "");
		scl.setAttribute("y", getTransform().getScale().y + "");
		
		for(LireComponent lc : getComponents()) {
			Element component = lc.getXmlElement(root);
			root.addChild(component);
		}
		
		return root;
	}

	public LireScene getScene() {
		return scene;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	

	public void keyDown(int keycode) {
		// TODO Auto-generated method stub
		
	}

	public void keyUp(int keycode) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(char character) {
		// TODO Auto-generated method stub
		
	}

	public void touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
	}

	public void touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
	}

	public void touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		
	}

	public void scrolled(int amount) {
		// TODO Auto-generated method stub
		
	}

	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	public void buttonDown(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		
	}

	public void buttonUp(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		
	}

	public void axisMoved(Controller controller, int axisCode, float value) {
		// TODO Auto-generated method stub
		
	}

	public void povMoved(Controller controller, int povCode, PovDirection value) {
		// TODO Auto-generated method stub
		
	}

	public void xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		
	}

	public void ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		
	}

	public void accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		
	}

	public void inputIn(Device device, String mapName) {
		// TODO Auto-generated method stub
		
	}

	public void inputOut(Device device, String mapName) {
		// TODO Auto-generated method stub
		
	}
	
}
