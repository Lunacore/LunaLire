package br.com.lunacore.editor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LunaLireStarter {

	//Puxar todos os arquivos da pasta SRC e tacar pra carregar no AssetManager
	public static FileHandle projectLocation;
	
	static SpriteBatch batch;
	static LireStateManager stateManager;
	static boolean drawFPS = false;
	static AssetManager assetManager;
		
	public static void init() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		
		//Carrega todos os arquivos
		//assetManager.load(fileName, type);
		
		stateManager = new LireStateManager();
	}
	
	@SuppressWarnings("rawtypes")
	public static Class lireForName(String className) throws ClassNotFoundException, MalformedURLException {
		File sourceFolder = new File(projectLocation.file().getAbsolutePath() + "/core/src");
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {sourceFolder.toURI().toURL()});
		return Class.forName(className, true, classLoader);
	}
	
	public static void render() {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stateManager.render(batch);
		stateManager.update(Gdx.graphics.getDeltaTime());
	}
	
	public static void dispose() {
		stateManager.dispose();
	}
	
}
