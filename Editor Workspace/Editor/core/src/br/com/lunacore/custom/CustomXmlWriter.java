package br.com.lunacore.custom;

import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

public class CustomXmlWriter extends XmlWriter{

	public CustomXmlWriter(FileHandle toSave) throws IOException {
		super(new FileWriter(toSave.file()));
	}
	
	public void setRoot(Element root) throws IOException {
		if(root != null) {
			element(root.getName());
			
			if(root.getAttributes() != null) {
				for(String s : root.getAttributes().keys()) {
					String att = root.getAttribute(s, null);
					if(att != null)
					attribute(s, att);
				}
			}
			
			for(int i = 0; i < root.getChildCount(); i ++) {
				Element child = root.getChild(i);
				setRoot(child);
			}
			
			if(root.getText() != null)
			text(root.getText());
			
			pop();
		}
	}

}
