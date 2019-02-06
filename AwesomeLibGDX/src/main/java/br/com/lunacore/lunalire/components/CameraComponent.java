package br.com.lunacore.lunalire.components;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
	float viewportWidth;
	float viewportHeight;
	ShapeRenderer sr;
	
	public CameraComponent() {
		limits = new Rectangle();
		camera = new OrthographicCamera();
		sr = new ShapeRenderer();
	}
	
	public void read(Element comp, FileHandle relativePath) {
		
		Element viewport = comp.getChildByName("viewport");
		
		viewportWidth = viewport.getFloat("width");
		viewportHeight = viewport.getFloat("height");
		
		camera.setToOrtho(false,
				viewport.getFloat("width") * parent.getTransform().getScale().x,
				viewport.getFloat("height") * parent.getTransform().getScale().y);
		camera.position.set(
				parent.getTransform().getPosition().x,
				parent.getTransform().getPosition().y,
				0
				);
		camera.zoom = viewport.getFloat("zoom");
		
		camera.up.rotate(Vector3.Z, parent.getFinalTransform().getAngle());
		
		if(parent.getScene() != null) {
			parent.getScene().setCamera(camera);
		}
	}
	
	public void create() {
		
	}
	
	public void draw(Batch sb, float parentAlpha) {
		
	}
	
	@Override
	public void drawToEditor(Batch sb, float parentAlpha) {
		sb.end();
		sr.setProjectionMatrix(sb.getProjectionMatrix());
		sr.begin(ShapeType.Line);
		
		sr.setColor(Color.WHITE);
		
		sr.rect(
				parent.getFinalTransform().getPosition().x - camera.viewportWidth/2f,
				parent.getFinalTransform().getPosition().y - camera.viewportHeight/2f,
				camera.viewportWidth/2f,
				camera.viewportHeight/2f,
				camera.viewportWidth,
				camera.viewportHeight,
				1,
				1,
				parent.getFinalTransform().getAngle()
				);
		
		sr.setColor(Color.RED);
		
		//Bottom left
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(10, 10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(50, 10)));
		
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(10, 10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(10, 50)));
		
		//Top left
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(10, -10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(50, -10)));
		
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(10, -10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(10, -50)));
		
		//Bottom right
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-10, 10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-50, 10)));
				
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-10, 10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-10, 50)));
		
		//Top right
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-10, -10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-50, -10)));
				
		sr.line(
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-10, -10)),
				parent.getFinalTransform().getPosition().cpy()
				.sub(-camera.viewportWidth/2f, -camera.viewportHeight/2f)
				.add(parent.getFinalTransform().getScale().cpy().scl(-10, -50)));
		
		sr.x(parent.getFinalTransform().getPosition(), parent.getFinalTransform().getScale().cpy().scl(30, 30).x);
		
		sr.end();
		sb.begin();
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

		camera.viewportWidth = viewportWidth * parent.getFinalTransform().getScale().x;
		camera.viewportHeight = viewportHeight * parent.getFinalTransform().getScale().y;
		
		camera.up.set(Vector3.Y.cpy().rotate(Vector3.Z, parent.getFinalTransform().getAngle()));
		
		camera.update();
	}
	

	public VisTable getUITable(DragAndDrop dragndrop) {
		VisTable table = new VisTable();
		table.align(Align.topLeft);
				
		table.add(new VisLabel("Viewport width")).pad(5);
		VisValidatableTextField widthTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		widthTxt.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
			public void changed(com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent event, Actor actor) {
				if(widthTxt.isInputValid()) {
					viewportWidth = Float.parseFloat(widthTxt.getText());
					
				}
				
			}
		});
		widthTxt.setText(viewportWidth + "");
		table.add(widthTxt).row();
		
		table.add(new VisLabel("Viewport height")).pad(5);
		VisValidatableTextField heightTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		heightTxt.setText(viewportHeight + "");
		heightTxt.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
			public void changed(com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent event, Actor actor) {
				if(heightTxt.isInputValid()) {
					viewportHeight = Float.parseFloat(heightTxt.getText());
				}
				
			}
		});
		table.add(heightTxt).row();
		
		table.add(new VisLabel("Camera zoom")).pad(5);
		VisValidatableTextField zoomTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		zoomTxt.setText(camera.zoom + "");
		zoomTxt.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
			public void changed(com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent event, Actor actor) {
				if(zoomTxt.isInputValid()) {
					camera.zoom = Float.parseFloat(zoomTxt.getText());
					camera.update();
				}
				
			}
		});
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
				-(camera.viewportWidth * parent.getFinalTransform().getScale().x)/2f + parent.getTransform().getPosition().x,
				-(camera.viewportHeight * parent.getFinalTransform().getScale().y)/2f + parent.getTransform().getPosition().y,
				camera.viewportWidth * parent.getFinalTransform().getScale().x,
				camera.viewportHeight * parent.getFinalTransform().getScale().y
				);
		return limits;
	}


}
