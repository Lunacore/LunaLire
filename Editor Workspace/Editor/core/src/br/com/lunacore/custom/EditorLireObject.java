package br.com.lunacore.custom;

import java.net.MalformedURLException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.XmlReader.Element;

import br.com.lunacore.Editor;
import br.com.lunacore.helper.Helper;
import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.LireScene;
import br.com.lunacore.lunalire.LunaLireStarter;
import br.com.lunacore.ui.LireSceneViewport;

//This is an verision of the LireObject that has more parameters specific to the editor
//that isn't really useful in the Lire runtime

public class EditorLireObject extends LireObject{
	
	private Class childClass;
	private HashMap<String, String> childParams;
	protected Rectangle limits;
	private boolean visible = true;
	boolean lock = false;
	
	static TextureAtlas ta;
	static NinePatch np;
	static Drawable invisible;
	
	Image topLeft;
	Image bottomLeft;
	Image topRight;
	Image bottomRight;
	
	public EditorLireObject(Element root, FileHandle assetsFolder, LireScene scene, final LireSceneViewport stage) {
		super(root, assetsFolder, scene);
		childParams = new HashMap<String, String>();
		limits = new Rectangle(-25, -25, 50, 50);
		
		if(ta == null) {
			ta = new TextureAtlas(Gdx.files.internal("atlas/selection.atlas"));
			np = ta.createPatch("selection");
			invisible = new TextureRegionDrawable(new Texture("icons/invisible.png"));
		}

		
		topLeft = new Image(invisible);
		topLeft.setSize(20, 20);
		topLeft.debug();
		topLeft.addListener(new DragListener() {
			
			Vector3 newPos = new Vector3();
			Vector3 oldPos = new Vector3();
			
			public void drag(InputEvent event, float x, float y, int pointer) {
				setTapSquareSize(0);
				//ScreenCam
				newPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				oldPos.set(getLimits().getX(), getLimits().getY() + getLimits().getHeight(), 0);
				
				//now its in WorldCam
				stage.getWorldCamera().unproject(newPos);
				
				float newWidth = getLimits().getWidth() + (oldPos.x - newPos.x);
				float newHeight = getLimits().getHeight() + (newPos.y - oldPos.y);
				
				Vector2 oldSize = new Vector2(getLimits().getWidth(), getLimits().getHeight());
				
				applySelectionTransform(new Vector2(newWidth, newHeight));
				
				for(EditorLireObject elo : Editor.getInstance().getSelectedObjects()) {
					elo.getTransform().setPosition(elo.projectPosition(
							elo.getFinalTransform().getPosition().cpy().sub(
									new Vector2(getLimits().getWidth(), getLimits().getHeight()).sub(oldSize).scl(1/2f)
									.scl(1, -1)
									),
							new Vector2()));
					elo.calculateLimits();
				}
			}
		});
		stage.addActor(topLeft);
		
		bottomLeft = new Image(invisible);
		bottomLeft.setSize(20, 20);
		bottomLeft.debug();
		bottomLeft.addListener(new DragListener() {
			
			Vector3 newPos = new Vector3();
			Vector3 oldPos = new Vector3();
			
			public void drag(InputEvent event, float x, float y, int pointer) {
				setTapSquareSize(0);
				//ScreenCam
				newPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				oldPos.set(getLimits().getX(), getLimits().getY(), 0);
				
				//now its in WorldCam
				stage.getWorldCamera().unproject(newPos);
				
				float newWidth = getLimits().getWidth() + (oldPos.x - newPos.x);
				float newHeight = getLimits().getHeight() + (oldPos.y - newPos.y);
				
				Vector2 oldSize = new Vector2(getLimits().getWidth(), getLimits().getHeight());
				
				applySelectionTransform(new Vector2(newWidth, newHeight));
				
				for(EditorLireObject elo : Editor.getInstance().getSelectedObjects()) {
					elo.getTransform().setPosition(elo.projectPosition(
							elo.getFinalTransform().getPosition().cpy().sub(
									new Vector2(getLimits().getWidth(), getLimits().getHeight()).sub(oldSize).scl(1/2f)
									.scl(1, 1)
									),
							new Vector2()));
					elo.calculateLimits();
				}
			}
		});
		stage.addActor(bottomLeft);
		
		topRight = new Image(invisible);
		topRight.setSize(20, 20);
		topRight.debug();
		topRight.addListener(new DragListener() {
			
			Vector3 newPos = new Vector3();
			Vector3 oldPos = new Vector3();
			
			public void drag(InputEvent event, float x, float y, int pointer) {
				setTapSquareSize(0);
				//ScreenCam
				newPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				oldPos.set(getLimits().getX() + getLimits().getWidth(), getLimits().getY() + getLimits().getHeight(), 0);
				
				//now its in WorldCam
				stage.getWorldCamera().unproject(newPos);
				
				float newWidth = getLimits().getWidth() + (newPos.x - oldPos.x);
				float newHeight = getLimits().getHeight() + (newPos.y - oldPos.y);
				
				Vector2 oldSize = new Vector2(getLimits().getWidth(), getLimits().getHeight());
				
				applySelectionTransform(new Vector2(newWidth, newHeight));
				
				for(EditorLireObject elo : Editor.getInstance().getSelectedObjects()) {
					elo.getTransform().setPosition(elo.projectPosition(
							elo.getFinalTransform().getPosition().cpy().sub(
									new Vector2(getLimits().getWidth(), getLimits().getHeight()).sub(oldSize).scl(1/2f)
									.scl(-1, -1)
									),
							new Vector2()));
					elo.calculateLimits();
				}
			}
		});
		stage.addActor(topRight);
		
		bottomRight = new Image(invisible);
		bottomRight.setSize(20, 20);
		bottomRight.debug();
		bottomRight.addListener(new DragListener() {
			
			Vector3 newPos = new Vector3();
			Vector3 oldPos = new Vector3();
			
			public void drag(InputEvent event, float x, float y, int pointer) {
				setTapSquareSize(0);
				//ScreenCam
				newPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				oldPos.set(getLimits().getX() + getLimits().getWidth(), getLimits().getY(), 0);
				
				//now its in WorldCam
				stage.getWorldCamera().unproject(newPos);
				
				float newWidth = getLimits().getWidth() + (newPos.x - oldPos.x);
				float newHeight = getLimits().getHeight() + (oldPos.y - newPos.y);
				
				Vector2 oldSize = new Vector2(getLimits().getWidth(), getLimits().getHeight());
				
				applySelectionTransform(new Vector2(newWidth, newHeight));
				
				for(EditorLireObject elo : Editor.getInstance().getSelectedObjects()) {
					elo.getTransform().setPosition(elo.projectPosition(
							elo.getFinalTransform().getPosition().cpy().sub(
									new Vector2(getLimits().getWidth(), getLimits().getHeight()).sub(oldSize).scl(1/2f)
									.scl(-1, 1)
									),
							new Vector2()));
					elo.calculateLimits();
				}
			}
		});
		stage.addActor(bottomRight);
	}
	
	//need to transform newsize from UICam to WorldCam
	private void applySelectionTransform(Vector2 newSize) {
				
		Vector2 oldSize = new Vector2(getLimits().getWidth(), getLimits().getHeight());
		
		for(EditorLireObject elo : Editor.getInstance().getSelectedObjects()) {

			elo.getTransform().setScale(elo.projectScale(
					elo.getFinalTransform().getScale().cpy().scl(
							newSize.x / oldSize.x,
							newSize.y / oldSize.y
							),
					new Vector2()));
			elo.calculateLimits();
						
		}		
		
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
	
	public void drawSelected(Batch batch, Camera worldCam) {
		batch.begin();
		//Things here are drawn in UICam
		
		Vector3 min = worldCam.project(new Vector3(getLimits().getX(), getLimits().getY(), 0));
		Vector3 max = worldCam.project(new Vector3(getLimits().getX() + getLimits().getWidth(), getLimits().getY() + getLimits().getHeight(), 0));
		
		np.draw(
				batch,
				min.x,
				min.y,
				(max.x - min.x)/2f, 
				(max.y - min.y)/2f,
				(max.x - min.x),
				(max.y - min.y),
				1,
				1,
				getFinalTransform().getAngle());
		batch.end();
		

		topLeft.setPosition(min.x - topLeft.getWidth()/2f + 3, max.y - topLeft.getHeight()/2f - 3);
		topRight.setPosition(max.x - topRight.getWidth()/2f - 3, max.y - topRight.getHeight()/2f - 3);
		bottomLeft.setPosition(min.x - bottomLeft.getWidth()/2f + 3, min.y - bottomLeft.getHeight()/2f + 3);
		bottomRight.setPosition(max.x - bottomRight.getWidth()/2f - 3, min.y - bottomRight.getHeight()/2f + 3);
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
