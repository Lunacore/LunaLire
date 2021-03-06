package br.com.lunacore;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

public class MyObject extends LireObject{
	
	public float agora_eh_float;
	public Texture tireiarquivoboteitextura;

	int hor = 0;
	int ver = 0;

	public MyObject(Element root, FileHandle assetsFolder, LireScene scene) {
		super(root, assetsFolder, scene);
	}
		
	@Override
	public void act(float delta) {
		super.act(delta);
		getTransform().getPosition().add(new Vector2(hor, ver).nor().scl(10));
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
