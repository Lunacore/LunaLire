package br.com.lunacore;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

public class OtherObject extends LireObject{

	public float aeita;
	
	Vector2 org;
	float timer = 0;
	
	
	public OtherObject(Element root, FileHandle assetsFolder, LireScene scene) {
		super(root, assetsFolder, scene);
		
		org = getTransform().getPosition().cpy();
		
	}
	
	public void act(float delta) {
		super.act(delta);
		timer += delta;
		
		getTransform().getPosition().set(org.cpy().add(0, (float)Math.sin(timer) * 50));
		
	}
	
	
}
