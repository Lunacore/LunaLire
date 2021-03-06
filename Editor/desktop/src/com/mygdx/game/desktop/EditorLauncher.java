package com.mygdx.game.desktop;

import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import br.com.lunacore.Editor;

public class EditorLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = Toolkit.getDefaultToolkit().getScreenSize().width - 10;
		config.height = Toolkit.getDefaultToolkit().getScreenSize().height - 100;
		config.y = 0;
		config.x = 0;
		
		new LwjglApplication(new Editor(), config);
	}
}
