package br.com.lunacore.editor.custom;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class FileButton extends VisTextButton{

	FileHandle handle;

	
	public FileButton(FileHandle handle, int align) {
		super(handle.name());
		setBackground((Drawable) null);
	}
	
	public FileButton(FileHandle handle) {
		this(handle, Align.center);
	}
	
	
	public FileHandle getHandle() {
		return handle;
	}

	public void setHandle(FileHandle handle) {
		this.handle = handle;
	}

}
