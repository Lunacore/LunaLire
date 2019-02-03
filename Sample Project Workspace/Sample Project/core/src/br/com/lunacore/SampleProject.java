package br.com.lunacore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import br.com.lunacore.lunalire.LunaLireStarter;

public class SampleProject extends ApplicationAdapter {
	public void create () {
		LunaLireStarter.projectLocation = new FileHandle(Gdx.files.getLocalStoragePath());

		LunaLireStarter.init();
	}

	public void render () {
		LunaLireStarter.render();
	}
	
	public void dispose () {
		LunaLireStarter.dispose();
	}
}
