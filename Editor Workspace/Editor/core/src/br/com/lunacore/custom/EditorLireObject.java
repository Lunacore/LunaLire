package br.com.lunacore.custom;

import java.net.MalformedURLException;
import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.LireScene;
import br.com.lunacore.lunalire.LunaLireStarter;

public class EditorLireObject extends LireObject{
	
	//Esses dois s�o mais usados pelo editor
	//j� q no editor eu n preciso criar uma INSTANCIA da classe
	//mas eu preciso saber qual classe q � de qqr forma (e os parametros)
	private Class childClass;
	private HashMap<String, String> childParams;
	protected Rectangle limits;
	private boolean visible = true;
	boolean lock = false;

	public EditorLireObject(Element root, FileHandle assetsFolder, LireScene scene) {
		super(root, assetsFolder, scene);
		childParams = new HashMap<String, String>();
		limits = new Rectangle(-25, -25, 50, 50);

	}

	@Override
	protected void readXml(Element root, FileHandle relativePath)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		super.readXml(root, relativePath);
		
		if(root.getAttribute("class", null) != null) {
			//Preciso compilar
			try {
				childClass = LunaLireStarter.lireForName(root.getAttribute("class"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setLocked(boolean locked) {
		lock = locked;
		setVisible(!lock);
	}
	
	public boolean isLocked() {
		return lock;
	}
	
	public boolean isShowing() {
		return visible;
	}
	
	public void setShowing(boolean v) {
		visible = v;
	}
	
	public Rectangle getLimits() {
		return limits;
	}
	
	public void calculateLimits() {
		limits.set(
				getFinalTransform().getPosition().x,
				getFinalTransform().getPosition().y,
				50 * getFinalTransform().getScale().x,
				50 * getFinalTransform().getScale().y);
		
		Vector2 min = new Vector2(limits.x, limits.y);
		Vector2 max = new Vector2(limits.x + limits.width, limits.y + limits.height);
		for(LireComponent lc : getComponents()) {
			Rectangle rec = lc.getLimits();
			if(rec.x < min.x) {
				min.x = rec.x;
			}
			if(rec.y < min.y) {
				min.y = rec.y;
			}
			if(rec.x + rec.width > max.x) {
				max.x = rec.x + rec.width;
			}
			if(rec.y + rec.height > max.y) {
				max.y = rec.y + rec.height;
			}
		}
		
//		for(LireObject child : getChildren()) {
//			EditorLireObject l = (EditorLireObject) child;
//			Rectangle rec = l.getLimits();
//			if(rec.x < min.x) {
//				min.x = rec.x;
//			}
//			if(rec.y < min.y) {
//				min.y = rec.y;
//			}
//			if(rec.x + rec.width > max.x) {
//				max.x = rec.x + rec.width;
//			}
//			if(rec.y + rec.height > max.y) {
//				max.y = rec.y + rec.height;
//			}
//		}
		
		limits.set(min.x, min.y, max.x - min.x, max.y - min.y);
	}
	
	public void act(float delta) {
		super.act(delta);
		calculateLimits();
	}
	
	public void draw(Batch batch, float parentAlpha) {
		if(visible) {
			super.draw(batch, parentAlpha);
		}
	}
	
	@Override
	public Element toXmlElement(Element parent) {
		Element root =  super.toXmlElement(parent);
		if(childClass != null) {
			root.setAttribute("class", childClass.getCanonicalName());
					
			if(childParams != null) {
				Element custom = new Element("custom", root);
				root.addChild(custom);
				for(String key : childParams.keySet()) {
					custom.setAttribute(key, childParams.get(key));
				}
			}
		}
		return root;
	}
	
	public void setParam(String key, String value) {
		if(childParams == null) {
			childParams = new HashMap<String, String>();
		}
		childParams.put(key, value);
	}
	
	public String getParam(String key) {
		if(childParams == null) {
			return null;
		}
		else {
			return childParams.get(key);
		}
	}
	
	public Class getChildClass() {
		return childClass;
	}


	public void setChildClass(Class childClass) {
		this.childClass = childClass;
	}

	public HashMap<String ,String> getParams() {
		return childParams;
	}
	
}
