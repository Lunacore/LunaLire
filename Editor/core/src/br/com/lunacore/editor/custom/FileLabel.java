package br.com.lunacore.editor.custom;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;

public class FileLabel extends VisLabel{
	
	FileHandle handle;
	
	public FileLabel(FileHandle handle, int align) {
		super(handle.name());
		this.handle = handle;
	}
	
	public FileLabel(FileHandle handle) {
		this(handle, Align.center);
	}
	
	
	public FileHandle getHandle() {
		return handle;
	}

	public void setHandle(FileHandle handle) {
		this.handle = handle;
	}

}
