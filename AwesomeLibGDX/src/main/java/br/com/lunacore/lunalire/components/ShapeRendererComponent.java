package br.com.lunacore.lunalire.components;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTree;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.utils.InputValidatorConstants;
import br.com.lunacore.lunalire.utils.UIConstants;
import br.com.lunacore.math.Transform;

public class ShapeRendererComponent extends LireComponent{

	//TODO: Adicionar polyline, polygon e grossura da linha
	
	private ShapeRenderer sr;
	ArrayList<ShapeDef> shapes;
	Rectangle limits;
	
	public ShapeRendererComponent() {
		sr = new ShapeRenderer();
		shapes = new ArrayList<ShapeDef>();
		limits = new Rectangle();
	}
	
	public ShapeType readShapeType(Element comp) {
		String shapeType = comp.getAttribute("shapeType", "line");
		ShapeType type = ShapeType.Line;
		if(shapeType.equals("fill")) {
			type = ShapeType.Filled;
		}
		if(shapeType.equals("point")) {
			type = ShapeType.Point;
		}
		return type;
	}
	
	public VisTable getShapeTable(ShapeDef def) {
		VisTable defTable = new VisTable();
		defTable.align(Align.topLeft);
		
		if(def instanceof RectDef) {
			defTable.add(new VisLabel("Rectangle")).row();
		}
		else if(def instanceof CircleDef) {
			defTable.add(new VisLabel("Circle")).row();
		}
		else if(def instanceof LineDef) {
			defTable.add(new VisLabel("Line")).row();
		}
		
		VisTable transTable = UIConstants.getTransformConfigTable(def.shapeTransform);
		
		transTable.row();
		transTable.add(new VisLabel("Shape type")).pad(5);
		
		VisSelectBox<ShapeType> types = new VisSelectBox<ShapeType>();
		types.setItems(ShapeType.Line, ShapeType.Filled, ShapeType.Point);
		types.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				def.drawType = types.getSelected();
			}
		});
		types.setSelected(def.drawType);
		transTable.add(types).pad(5).colspan(4).grow().row();
		
		transTable.add(new VisLabel("Color")).pad(5);
		VisTextButton colorbtn = new VisTextButton("Pick color");
		colorbtn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
									
				ColorPicker colorpicker = new ColorPicker(new ColorPickerListener() {
					public void reset(Color previousColor, Color newColor) {
						def.color.set(previousColor);
					}

					public void finished(Color newColor) {
						def.color.set(newColor);
					}
					
					public void changed(Color newColor) {
						def.color.set(newColor);
					}
					
					public void canceled(Color oldColor) {
						def.color.set(oldColor);
					}
				});
				colorpicker.setColor(def.color);
				colorpicker.pack();
				colorpicker.setResizable(true);
				colorpicker.centerWindow();
				
				colorbtn.getStage().addActor(colorpicker);
				
				super.clicked(event, x, y);
			}
		});
		
		transTable.add(colorbtn).pad(5).colspan(4).grow().row();

		if(def instanceof RectDef) {
			RectDef cast = (RectDef) def;
			
			transTable.add(new VisLabel("Width"));
			final VisValidatableTextField val = new VisValidatableTextField(InputValidatorConstants.floatValidator);
			val.setText(cast.width + "");
			val.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					if(val.isInputValid()) {
						cast.width = Float.parseFloat(val.getText());
					}
				}});
			transTable.add(val).pad(5).colspan(4).row();
			
			transTable.add(new VisLabel("Height"));
			final VisValidatableTextField val2 = new VisValidatableTextField(InputValidatorConstants.floatValidator);
			val2.setText(cast.height + "");
			val2.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					if(val2.isInputValid()) {
						cast.height = Float.parseFloat(val2.getText());
					}
				}});
			transTable.add(val2).pad(5).colspan(4).row();
		}
		else if(def instanceof CircleDef) {
			CircleDef cast = (CircleDef) def;
			
			transTable.add(new VisLabel("Radius"));
			final VisValidatableTextField val = new VisValidatableTextField(InputValidatorConstants.floatValidator);
			val.setText(cast.radius + "");
			val.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					if(val.isInputValid()) {
						cast.radius = Float.parseFloat(val.getText());
					}
				}});
			transTable.add(val).pad(5).colspan(4).row();
		}
		else if(def instanceof LineDef) {
			LineDef cast = (LineDef) def;
			
			int i = 0;
			for(Vector2 v : cast.points) {
				transTable.add(new VisLabel("Line " + i)).row();
				
				transTable.add(new VisLabel("X"));
				VisValidatableTextField val = new VisValidatableTextField(InputValidatorConstants.floatValidator);
				val.setText(v.x + "");
				val.addListener(new ChangeListener() {
					public void changed(ChangeEvent event, Actor actor) {
						if(val.isInputValid()) {
							v.x = Float.parseFloat(val.getText());
						}
					}});
				transTable.add(val).pad(5);
				
				
				transTable.add(new VisLabel("Y"));
				VisValidatableTextField val2 = new VisValidatableTextField(InputValidatorConstants.floatValidator);
				val2.addListener(new ChangeListener() {
					public void changed(ChangeEvent event, Actor actor) {
						if(val2.isInputValid()) {
							v.y = Float.parseFloat(val2.getText());
						}
					}});
				val2.setText(v.y + "");
				transTable.add(val2).pad(5);
				
				i++;
			}
			
		}
		
		defTable.add(transTable).row();
		return defTable;
	}
	
	public VisTable getUITable(DragAndDrop dragndrop) {
		VisTable table = new VisTable();
		table.align(Align.topLeft);
				
		VisTable shapeGroup = new VisTable();
		
		VisTable lastTable = null;
		for(ShapeDef def : shapes) {
			lastTable = getShapeTable(def);
			
			String title = "Shape";
			
			if(def instanceof RectDef) title = "Rectangle";
			if(def instanceof CircleDef) title = "Circle";
			if(def instanceof LineDef) title = "Line";

			VisTree tree = new VisTree();
			tree.getStyle().selection = tree.getStyle().background;
			Node r = new Node(new VisLabel(title));
			r.add(new Node(lastTable));
			tree.add(r);
			
			shapeGroup.add(tree).grow().row();
		}
		VisScrollPane shapeScroll = new VisScrollPane(shapeGroup);
		table.add(shapeScroll).grow().row();
		
		VisTable vt = lastTable;
		
		table.row();
		VisTextButton addShape = new VisTextButton("Add shape");
		addShape.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				
				PopupMenu popup = new PopupMenu();
				
				MenuItem rect = new MenuItem("Rectangle");
				rect.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						RectDef rd = new RectDef(ShapeType.Line, new Transform(), Color.WHITE, 10, 10);
						shapes.add(rd);
						table.addActorAfter(vt, getShapeTable(rd));
						
						RefreshEvent evt = new RefreshEvent(this);
						table.fire(evt);
					};
				});
				popup.addItem(rect);
				
				MenuItem circ = new MenuItem("Circle");
				circ.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						CircleDef rd = new CircleDef(ShapeType.Line, new Transform(), Color.WHITE, 10);
						shapes.add(rd);
						table.addActorAfter(vt, getShapeTable(rd));
						
						RefreshEvent evt = new RefreshEvent(this);
						table.fire(evt);
					};
				});
				popup.addItem(circ);
				
				MenuItem line = new MenuItem("Line");
				line.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						LineDef rd = new LineDef(ShapeType.Line, new Transform(), Color.WHITE, new ArrayList<Vector2>());
						shapes.add(rd);
						table.addActorAfter(vt, getShapeTable(rd));
						
						RefreshEvent evt = new RefreshEvent(this);
						table.fire(evt);
					};
				});
				popup.addItem(line);
				
				popup.showMenu(table.getStage(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
								
				super.clicked(event, x, y);
			}
		});
		table.add(addShape).growX();
				
		//table.debugAll();
		
		return table;
	}
	
	public Transform readTransform(Element comp) {
		return new Transform(
				new Vector2(
						comp.getFloat("x", 0),
						comp.getFloat("y", 0)
						),
				comp.getFloat("angle", 0),
				new Vector2(1, 1)
				);
	}
	
	public Color readColor(Element comp) {
		return new Color(
				comp.getFloat("r", 1),
				comp.getFloat("g", 1),
				comp.getFloat("b", 1),
				comp.getFloat("a", 1));
	}
	
	@Override
	public void read(Element comp, FileHandle relativePath) {
		
		for(Element el : comp.getChildrenByName("rectangle")) {
			addRect(readShapeType(el),
					readTransform(el),
					readColor(el),
					el.getFloat("width", 0),
					el.getFloat("height", 0));
		}
		for(Element el : comp.getChildrenByName("circle")) {
			addCircle(readShapeType(el),
					readTransform(el),
					readColor(el),
					el.getFloat("radius", 0));
		}
		for(Element el : comp.getChildrenByName("line")) {
			
			ArrayList<Vector2> vertex = new ArrayList<Vector2>();
			
			for(Element ve : el.getChildrenByName("vertex")) {
					vertex.add(new Vector2(
							ve.getFloat("x"),
							ve.getFloat("y")
							));
			}
			
			addLine(readShapeType(el),
					readTransform(el),
					readColor(el),
					vertex);
		}
		
	}
	
	@Override
	public Element getXmlElement(Element root) {
		Element compo = new Element("component", root);
		compo.setAttribute("class", getClass().getCanonicalName());
		
		for(ShapeDef shape : shapes) {
			
			Element ele = null;

			if(shape instanceof RectDef) {
				ele = new Element("rectangle", compo);
				RectDef cast = (RectDef) shape;
				
				ele.setAttribute("width", cast.width + "");
				ele.setAttribute("height", cast.height + "");

			}
			else if(shape instanceof CircleDef) {
				ele = new Element("circle", compo);
				CircleDef cast = (CircleDef) shape;
				
				ele.setAttribute("radius", cast.radius + "");

			}
			else if(shape instanceof LineDef) {
				ele = new Element("line", compo);
				LineDef cast = (LineDef) shape;

				for(Vector2 v : cast.points) {
					Element ln = new Element("vertex", ele);
					ln.setAttribute("x", v.x + "");
					ln.setAttribute("y", v.y + "");
					
					ele.addChild(ln);
				}
			}
			
			String shapeType = "line";
			switch(shape.drawType) {
			case Filled:
				shapeType = "fill";
				break;
			case Line:
				shapeType = "line";
				break;
			case Point:
				shapeType = "point";
				break;
			}
			
			ele.setAttribute("shapeType", shapeType);
			
			ele.setAttribute("r", shape.color.r + "");
			ele.setAttribute("g", shape.color.g + "");
			ele.setAttribute("b", shape.color.b + "");
			ele.setAttribute("a", shape.color.a + "");

			ele.setAttribute("x", shape.shapeTransform.getPosition().x + "");
			ele.setAttribute("y", shape.shapeTransform.getPosition().y + "");
			ele.setAttribute("angle", shape.shapeTransform.getAngle() + "");
			
			compo.addChild(ele);
		}
		
		return compo;
	}
	
	public void addLine(ShapeType drawType, Transform shapeTransform, Color color, ArrayList<Vector2> points) {
		shapes.add(new LineDef(drawType, shapeTransform, color, points));
	}
	
	public void addCircle(ShapeType drawType, Transform shapeTransform, Color color, float radius) {
		shapes.add(new CircleDef(drawType, shapeTransform, color, radius));
	}
	
	public void addRect(ShapeType drawType, Transform shapeTransform, Color color, float width, float height) {
		shapes.add(new RectDef(drawType, shapeTransform, color, width, height));
	}
	
	public void create() {
		
	}
	
	public Rectangle getLimits() {
		
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		
		for(ShapeDef sd : shapes) {
			if(sd.minX() < minX) {
				minX = sd.minX();
			}
			if(sd.maxX() > maxX) {
				maxX = sd.maxX();
			}
			if(sd.minY() < minY) {
				minY = sd.minY();
			}
			if(sd.maxY() > maxY) {
				maxY = sd.maxY();
			}
		}
		
		limits.set(minX, minY, maxX - minX, maxY - minY);
		return limits;
		
	}
	
	public void act(float delta) {
		
	}

	public void draw(Batch sb, float parentAlpha) {
		sb.end();
		
		sr.setProjectionMatrix(sb.getProjectionMatrix());
		sr.setAutoShapeType(true);
		sr.begin();
		
		for(ShapeDef def : shapes) {
			sr.setColor(def.color);
			sr.set(def.drawType);
			def.render(sr);
		}
		
		sr.end();
		
		
		sb.begin();
	}
	
	public void drawToEditor(Batch sb, float parentAlpha) {
		draw(sb, parentAlpha);
	}

	
	public void update(float delta) {
		
	}

	public void dispose() {
		
	}

	public boolean validate(LireObject obj) {
		return obj.getComponent(ShapeRendererComponent.class) == null;
	}
	
	abstract class ShapeDef {
		ShapeType drawType;
		Transform shapeTransform;
		Color color;
		
		public ShapeDef(ShapeType drawType, Transform shapeTransform, Color color) {
			super();
			this.drawType = drawType;
			this.shapeTransform = shapeTransform;
			this.color = color;
		}
		
		protected abstract float maxY();
		protected abstract float minY();
		protected abstract float maxX();
		protected abstract float minX();

		public abstract void render(ShapeRenderer sr);
	}
	
	class CircleDef extends ShapeDef{

		float radius;
		private Vector2 pt;
		
		public CircleDef(ShapeType drawType, Transform shapeTransform, Color color, float radius) {
			super(drawType, shapeTransform, color);
			this.radius = radius;
			
			pt = new Vector2();
		}

		public void render(ShapeRenderer sr) {
			
			pt.set(shapeTransform.getPosition());
			pt.rotate(parent.getFinalTransform().getAngle());
			
			sr.ellipse(
					parent.getFinalTransform().getPosition().x + pt.x,
					parent.getFinalTransform().getPosition().y + pt.y,
					radius * parent.getFinalTransform().getScale().x,
					radius * parent.getFinalTransform().getScale().y,
					parent.getFinalTransform().getAngle() + shapeTransform.getAngle());
					
		}

		protected float maxY() {
			return parent.getFinalTransform().getPosition().y + pt.y + radius * parent.getFinalTransform().getScale().y;
		}

		protected float minY() {
			return parent.getFinalTransform().getPosition().y + pt.y - radius * parent.getFinalTransform().getScale().y;
		}

		protected float maxX() {
			return parent.getFinalTransform().getPosition().x + pt.x + radius * parent.getFinalTransform().getScale().x;
		}

		protected float minX() {
			return parent.getFinalTransform().getPosition().x + pt.x - radius * parent.getFinalTransform().getScale().x;
		}
		
	}
	
	class RectDef extends ShapeDef{
		
		float width;
		float height;
		
		private Vector2 pt;
		
		Vector2 p0, p1, p2, p3;
		
		public RectDef(ShapeType drawType, Transform shapeTransform, Color color, float width, float height) {
			super(drawType, shapeTransform, color);
			this.width = width;
			this.height = height;
			
			pt = new Vector2();
			
			p0 = new Vector2();
			p1 = new Vector2();
			p2 = new Vector2();
			p3 = new Vector2();
		}
		
		public void render(ShapeRenderer sr) {
			pt.set(shapeTransform.getPosition());
			pt.rotate(parent.getFinalTransform().getAngle());
			pt.add(parent.getFinalTransform().getPosition());
			sr.rect(
					pt.x - width/2f,
					pt.y - height/2f,
					width/2f,
					height/2f,
					width,
					height,
					parent.getFinalTransform().getScale().x * shapeTransform.getScale().x,
					parent.getFinalTransform().getScale().y * shapeTransform.getScale().y,
					shapeTransform.getAngle());
			
			p0 = new Vector2(
					-(width * parent.getFinalTransform().getScale().x * shapeTransform.getScale().x)/2f,
					-(height * parent.getFinalTransform().getScale().y * shapeTransform.getScale().y)/2f
					)
					.rotate(shapeTransform.getAngle())
					.add(pt.x, pt.y);
			p1 = new Vector2(
					-(width * parent.getFinalTransform().getScale().x * shapeTransform.getScale().x)/2f,
					(height * parent.getFinalTransform().getScale().y * shapeTransform.getScale().y)/2f
					)
					.rotate(shapeTransform.getAngle())
					.add(pt.x, pt.y);
			p2 = new Vector2(
					(width * parent.getFinalTransform().getScale().x * shapeTransform.getScale().x)/2f,
					-(height * parent.getFinalTransform().getScale().y * shapeTransform.getScale().y)/2f
					)
					.rotate(shapeTransform.getAngle())
					.add(pt.x, pt.y);
			p3 = new Vector2(
					(width * parent.getFinalTransform().getScale().x * shapeTransform.getScale().x)/2f,
					(height * parent.getFinalTransform().getScale().y * shapeTransform.getScale().y)/2f
					)
					.rotate(shapeTransform.getAngle())
					.add(pt.x, pt.y);
		}
		protected float maxY() {
			return Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
		}
		protected float minY() {
			return Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
		}
		protected float maxX() {
			return Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
		}
		protected float minX() {
			return Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
		}	
		
	}
	
	public class LineDef extends ShapeDef {

		ArrayList<Vector2> points;
		Vector2 pp1, pp2;
		
		public LineDef(ShapeType drawType, Transform shapeTransform, Color color, ArrayList<Vector2> points) {
			super(drawType, shapeTransform, color);
			this.points = points;
			pp1 = new Vector2();
			pp2 = new Vector2();
		}


		public void addPoint(Vector2 point) {
			points.add(point);
		}
		
		public void removePoint(Vector2 point) {
			points.remove(point);
		}

		public void removePoint(int index) {
			points.remove(index);
		}
		
		public void render(ShapeRenderer sr) {
			
			if(points.size() >= 2) {
				for(int i = 0; i < points.size() - 1; i ++) {
					pp1.set(points.get(i))
							.rotate(parent.getFinalTransform().getAngle() + shapeTransform.getAngle())
							.scl(parent.getFinalTransform().getScale().x)
							.add(parent.getFinalTransform().getPosition());
		
					pp2.set(points.get(i+1))
							.rotate(parent.getFinalTransform().getAngle() + shapeTransform.getAngle())
							.scl(parent.getFinalTransform().getScale().y)
							.add(parent.getFinalTransform().getPosition());
				
					sr.line(pp1, pp2);
				}
			}
		}

		protected float maxY() {
			if(points.size() > 0) {
				float max = points.get(0).y;
				for(Vector2 v : points) {
					if(v.y > max) {
						max = v.y;
					}
				}
				return max;
			}
			return 0;
		}
		protected float minY() {
			if(points.size() > 0) {
				float min = points.get(0).y;
				for(Vector2 v : points) {
					if(v.y < min) {
						min = v.y;
					}
				}
				return min;
			}
			return 0;
		}
		protected float maxX() {
			if(points.size() > 0) {
				float max = points.get(0).x;
				for(Vector2 v : points) {
					if(v.x > max) {
						max = v.x;
					}
				}
				return max;
			}
			return 0;
		}
		protected float minX() {
			if(points.size() > 0) {
				float min = points.get(0).x;
				for(Vector2 v : points) {
					if(v.x < min) {
						min = v.x;
					}
				}
				return min;
			}
			return 0;
		}
	}

	@Override
	public String getValidationCause() {
		return "Object already has a Shape Renderer component!";
	}



	

}
