package br.com.lunacore.lunalire.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.VisTable;

import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireObject;

public class ParticleComponent extends LireComponent{

	ParticleEffect particle;
	FileHandle handle;
	
	public void create() {
		
	}

	public void draw(Batch sb, float parentAlpha) {
		particle.draw(sb);
	}

	public void dispose() {
		particle.dispose();
	}

	public Rectangle getLimits() {
		return new Rectangle(
				particle.getBoundingBox().getCenterX() - particle.getBoundingBox().getWidth()/2f,
				particle.getBoundingBox().getCenterY() - particle.getBoundingBox().getHeight()/2f,
				particle.getBoundingBox().getWidth(),
				particle.getBoundingBox().getHeight());
	}

	public void drawToEditor(Batch sb, float parentAlpha) {
		particle.getEmitters().get(0).setAttached(true);
		draw(sb, parentAlpha);
	}

	public boolean validate(LireObject obj) {
		return true;
	}

	public String getValidationCause() {
		return "";
	}

	public void act(float delta) {
			particle.setPosition(parent.getFinalTransform().getPosition().x, parent.getFinalTransform().getPosition().y);
			particle.scaleEffect(parent.getFinalTransform().getScale().x, parent.getFinalTransform().getScale().x, 1);
			particle.update(Gdx.graphics.getDeltaTime());
	}

	public void read(Element comp, FileHandle relativePath) {
		if(comp.getAttribute("loc", null) != null) {
			if(relativePath != null) {
				handle = new FileHandle(relativePath.path() + "/" + comp.getAttribute("loc"));
			}
			else {
				handle = Gdx.files.internal(comp.getAttribute("loc"));
			}
			particle = new ParticleEffect();
			particle.load(handle, handle.parent());
		}
		
	}

	public VisTable getUITable(DragAndDrop dragndrop) {
		return new VisTable();
	}

	public Element getXmlElement(Element root) {
		Element element = new Element("component", root);
		element.setAttribute("class", getClass().getCanonicalName());
		element.setAttribute("loc", getLocalPath(handle));
		return element;
	}

}