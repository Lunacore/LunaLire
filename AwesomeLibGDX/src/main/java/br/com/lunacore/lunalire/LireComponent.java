package br.com.lunacore.lunalire;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.VisTable;

public abstract class LireComponent {

	protected LireObject parent;
	
	/**
	 * This method is called at the begin of the object constructor (i think)
	 */
	public abstract void create();
	public abstract void draw(Batch sb, float parentAlpha);
	public abstract void dispose();
	public abstract Rectangle getLimits();
	/** This draw call is only called at the editor
	 * 	its used to show visual information about the component in the editor, but not in the game
	 * 
	 * @param batch
	 */
	public abstract void drawToEditor(Batch sb, float parentAlpha);
	/**
	 * This method is called when an LireObject tries to append a new component of this class 
	 * into it
	 * 
	 * Its used for example, to prevent adding more than one component of the same class (Ex.)
	 * <code>return obj.getComponent(ShapeRendererComponent.class) == null;</code>
	 * 
	 * You can also append other components at the object before appending this one, for example
	 * while adding a Fixture component to an object that has no Body component, you can add
	 * a default Body component to the object
	 * 
	 * @param obj the object that is trying to append this component
	 * @return true if this component can be appended to the object
	 */
	public abstract boolean validate(LireObject obj);
	/**
	 * This is used after a failed validation cause, to explain why the component validation was failed
	 * 
	 * @return A string explaining the failed validation cause
	 */
	public abstract String getValidationCause();
	public abstract void act(float delta);
	/**
	 * This is called for serialization
	 * 
	 * Assign the variables from the Element to the variables of the component
	 * 
	 * @param comp
	 * @param relativePath
	 */
	public abstract void read(Element comp, FileHandle relativePath);
	public abstract VisTable getUITable(DragAndDrop dragndrop);
	/**
	 * This is called for serialization
	 * 
	 * Create an element (root is the parent element) that contains all the information of this component, 
	 * so it can be read by the method {@link #read(Element, FileHandle)}
	 * 
	 * @param root the parent of the component, when creating a new element, use
	 * <code>Element el = new Element("component", root)</code>
	 * @return the element to be appended to the root
	 * 
	 * do NOT append the element to the root by yourself
	 */
	public abstract Element getXmlElement(Element root);

	public LireObject getParent() {
		return parent;
	}
	public void setParent(LireObject parent) {
		this.parent = parent;
	}
	
	public static class RefreshEvent extends Event{
		Object obj;
		public RefreshEvent(Object data) {
			obj = data;
		}
	}

	public static String getLocalPath(FileHandle handle) {
		if(handle == null) return "";
		String[] aps = handle.path().split("/");
		boolean found = false;
		String finalString = "";
		for(String s : aps) {
			if(found) {
				finalString += "/" + s;
			}			
			if(s.equals("assets")) {
				found = true;
			}
		}		
		return finalString.substring(1);
	}

}
