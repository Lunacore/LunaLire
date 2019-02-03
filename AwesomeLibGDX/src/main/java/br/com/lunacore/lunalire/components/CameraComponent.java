package br.com.lunacore.lunalire.components;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.utils.InputValidatorConstants;

public class CameraComponent extends LireComponent{

	OrthographicCamera camera;
	Rectangle limits;
	
	public CameraComponent() {
		limits = new Rectangle();
		camera = new OrthographicCamera();
	}
	
	public void read(Element comp, FileHandle relativePath) {
		
		Element viewport = comp.getChildByName("viewport");
		
		camera.setToOrtho(false,
				viewport.getFloat("width") * parent.getTransform().getScale().x,
				viewport.getFloat("height") * parent.getTransform().getScale().y);
		camera.position.set(
				parent.getTransform().getPosition().x,
				parent.getTransform().getPosition().y,
				0
				);
		camera.zoom = viewport.getFloat("zoom");
		
		if(parent.getScene() != null) {
			parent.getScene().setCamera(camera);
		}
	}
	
	public void create() {
		
	}
	
	public void draw(Batch sb, float parentAlpha) {
		
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void dispose() {
		
	}

	public boolean validate(LireObject obj) {
		return obj.getComponent(CameraComponent.class) == null;
	}

	public void act(float delta) {
		camera.position.set(
				parent.getTransform().getPosition().x,
				parent.getTransform().getPosition().y,
				0
				);
	}

	public VisTable getUITable(DragAndDrop dragndrop) {
		VisTable table = new VisTable();
		table.align(Align.topLeft);
				
		table.add(new VisLabel("Viewport width")).pad(5);
		VisValidatableTextField widthTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		widthTxt.setText(camera.viewportWidth + "");
		table.add(widthTxt).row();
		
		table.add(new VisLabel("Viewport height")).pad(5);
		VisValidatableTextField heightTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		heightTxt.setText(camera.viewportHeight + "");
		table.add(heightTxt).row();
		
		table.add(new VisLabel("Camera zoom")).pad(5);
		VisValidatableTextField zoomTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		zoomTxt.setText(camera.zoom + "");
		table.add(zoomTxt).row();
		
		return table;
	}

	public Element getXmlElement(Element root) {
		Element compo = new Element("component", root);
		compo.setAttribute("class", getClass().getCanonicalName());
		
		Element viewport = new Element("viewport", compo);
		viewport.setAttribute("width", camera.viewportWidth + "");
		viewport.setAttribute("height", camera.viewportHeight + "");
		viewport.setAttribute("zoom", camera.zoom + "");
		compo.addChild(viewport);
		
		return compo;
	}

	@Override
	public String getValidationCause() {
		return "Object already has a Camera component!";
	}

	@Override
	public Rectangle getLimits() {
		limits.set(
				parent.getFinalTransform().getPosition().x,
				parent.getFinalTransform().getPosition().y,
				camera.viewportWidth * parent.getFinalTransform().getScale().x,
				camera.viewportHeight * parent.getFinalTransform().getScale().y
				);
		return limits;
	}

}