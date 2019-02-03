package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import br.com.lunacore.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		//Aqui eu tenho q ler um arquivo de configuração pra saber o q eu vou fazer
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
