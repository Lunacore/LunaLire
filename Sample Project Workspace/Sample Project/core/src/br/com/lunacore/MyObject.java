package br.com.lunacore;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.LireScene;
import br.com.lunacore.lunalire.LireScene.ObjectFinder;
import br.com.lunacore.lunalire.components.CameraComponent;

public class MyObject extends LireObject{
	
	public String atelamenumano;

	int hor = 0;
	int ver = 0;
	
	public MyObject(Element root, FileHandle assetsFolder, LireScene scene) {
		super(root, assetsFolder, scene);
	}
		
	@Override
	public void act(float delta) {
		super.act(delta);
		getTransform().getPosition().add(new Vector2(hor, ver).nor().scl(10));
		//getScene().setCameraActor(getScene().getObjectsByComponent(CameraComponent.class).get(0));
		getScene().getCameraActor().getTransform().setPosition(getTransform().getPosition());
	}
	
	@Override
	public void keyDown(int keycode) {

		if(keycode == Keys.LEFT) {
			hor = -1;
		}
		else if(keycode == Keys.RIGHT) {
			hor = 1;
		}
		
		if(keycode == Keys.UP) {
			ver = 1;
		}
		else if(keycode == Keys.DOWN) {
			ver = -1;
		}
		
		super.keyDown(keycode);
	}
	
	@Override
	public void keyUp(int keycode) {
		if(keycode == Keys.LEFT || keycode == Keys.RIGHT) {
			hor = 0;
		}
		if(keycode == Keys.UP || keycode == Keys.DOWN) {
			ver = 0;
		}
		super.keyUp(keycode);
	}
	

}