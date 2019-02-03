package br.com.lunacore;

import com.badlogic.gdx.ApplicationAdapter;
import br.com.lunacore.lunalire.LunaLireStarter;

public class MyGdxGame extends ApplicationAdapter {
	
	//Colocar os final int dos ids das cenas aqui
	
	public void create () {
		LunaLireStarter.init();
		//Colocar as cenas aqui
		LunaLireStarter.create();
	}

	public void render () {
		LunaLireStarter.render();
	}
	
	public void dispose () {
		LunaLireStarter.dispose();
	}
}
