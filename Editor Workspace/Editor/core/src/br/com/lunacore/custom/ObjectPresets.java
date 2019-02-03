package br.com.lunacore.custom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import br.com.lunacore.Editor;
import br.com.lunacore.lunalire.components.SpriteComponent;

public class ObjectPresets {
	
	public static EditorLireObject getSpritePreset(FileHandle sprite) {
		EditorLireObject obj = Editor.getInstance().getUIState().getStage().loadObject(Gdx.files.internal("presets/sprite.lo"), 0, 0);
		((SpriteComponent) obj.getComponent(SpriteComponent.class)).setSprite(sprite);
		return obj;
	}
	
	public static EditorLireObject getParticlePreset(FileHandle sprite) {
		
		return null;
	}
	

}
