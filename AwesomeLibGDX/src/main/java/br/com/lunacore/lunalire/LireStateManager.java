package br.com.lunacore.lunalire;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import br.com.lunacore.lunalire.KeyMapper;
import br.com.lunacore.lunalire.KeyMapper.Device;

public class LireStateManager {
	
	HashMap<Integer, LireScene> scenes;	
	int currentScene = -1;
	KeyMapper keyMapper;
	
	public LireStateManager() {
		scenes = new HashMap<Integer, LireScene>();
		XmlReader reader = new XmlReader();
		Element root = reader.parse(Gdx.files.internal("project.ll"));
		
		//Carrega as cenas a partir do arquivo .ll
		
		for(Element sc : root.getChildrenByName("scene")) {
			//Pegar alguma configura��o de startScene
			//por enquanto ele carrega na primeira
			
			String loc = sc.getAttribute("loc");
			System.out.println("Loading scene " + loc);
			
			Element ss = reader.parse(Gdx.files.internal(loc));

			LireScene lr = new LireScene(ss);

			int ID = Integer.parseInt(ss.getAttribute("ID"));
			scenes.put(ID, lr);
			if(currentScene == -1) {
				currentScene = ID;
			}
		}
		keyMapper = new KeyMapper(this);

	}
	
	public void render(SpriteBatch sb) {
		if(currentScene != -1) {
			scenes.get(currentScene).render(sb);
		}
	}

	public void update(float delta) {
		if(currentScene != -1) {
			scenes.get(currentScene).update(delta);
		}
	}
	
	public void registerKey(String mapName, Device device, int keycode) {
		keyMapper.registerKeyMap(mapName, device, keycode);
	}

	public void dispose() {
		scenes.get(currentScene).dispose();
	}

	public void keyDown(int keycode) {
		scenes.get(currentScene).keyDown(keycode);
	}

	public void keyUp(int keycode) {
		scenes.get(currentScene).keyUp(keycode);		
	}

	public void keyTyped(char character) {
		scenes.get(currentScene).keyTyped(character);		
	}

	public void touchDown(int screenX, int screenY, int pointer, int button) {
		scenes.get(currentScene).touchDown(screenX, screenY, pointer, button);		
	}

	public void touchUp(int screenX, int screenY, int pointer, int button) {
		scenes.get(currentScene).touchUp(screenX, screenY, pointer, button);		
	}

	public void touchDragged(int screenX, int screenY, int pointer) {
		scenes.get(currentScene).touchDragged(screenX, screenY, pointer);		
	}

	public void mouseMoved(int screenX, int screenY) {
		scenes.get(currentScene).mouseMoved(screenX, screenY);		
	}

	public void scrolled(int amount) {
		scenes.get(currentScene).scrolled(amount);
	}

	public void connected(Controller controller) {
		scenes.get(currentScene).connected(controller);
	}

	public void disconnected(Controller controller) {
		scenes.get(currentScene).disconnected(controller);		
	}

	public void buttonDown(Controller controller, int buttonCode) {
		scenes.get(currentScene).buttonDown(controller, buttonCode);		
	}

	public void buttonUp(Controller controller, int buttonCode) {
		scenes.get(currentScene).buttonUp(controller, buttonCode);		
	}

	public void axisMoved(Controller controller, int axisCode, float value) {
		scenes.get(currentScene).axisMoved(controller, axisCode, value);		
	}

	public void povMoved(Controller controller, int povCode, PovDirection value) {
		scenes.get(currentScene).povMoved(controller, povCode, value);		
	}

	public void xSliderMoved(Controller controller, int sliderCode, boolean value) {
		scenes.get(currentScene).xSliderMoved(controller, sliderCode, value);		
	}

	public void ySliderMoved(Controller controller, int sliderCode, boolean value) {
		scenes.get(currentScene).ySliderMoved(controller, sliderCode, value);		
	}

	public void accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		scenes.get(currentScene).accelerometerMoved(controller, accelerometerCode, value);		
	}

	public void inputIn(Device device, String mapName) {
		scenes.get(currentScene).inputIn(device, mapName);
	}

	public void inputOut(Device device, String mapName) {
		scenes.get(currentScene).inputOut(device, mapName);		
	}

}