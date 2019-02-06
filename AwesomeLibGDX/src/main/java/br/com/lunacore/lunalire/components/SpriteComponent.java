package br.com.lunacore.lunalire.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

import br.com.lunacore.helper.Helper;
import br.com.lunacore.lunalire.LireComponent;
import br.com.lunacore.lunalire.LireObject;
import br.com.lunacore.lunalire.utils.NinePatchLoader;

public class SpriteComponent extends LireComponent{

	Drawable sprite;
	FileHandle spriteHandle;
	boolean flipX;
	boolean flipY;
	Color tint;

	public Drawable getSprite() {
		return sprite;
	}

	public void setSprite(FileHandle handle) {
		if(handle.name().contains(".9.")) {
			this.sprite = new NinePatchDrawable(NinePatchLoader.loadNinePatch(handle));
			System.out.println("Size: (" + getSpriteWidth() + ", " + getSpriteHeight() + ")");
		}
		else {
			this.sprite = new TextureRegionDrawable(new Texture(handle));
		}
		this.spriteHandle = handle;
		
		if(tint == null) {
			tint = Color.WHITE.cpy();
		}
	}

	public Color getTint() {
		return tint;
	}

	public void setTint(Color tint) {
		this.tint = tint;
	}

	@Override
	public void create() {
			
		
	}
	
	public Rectangle getLimits() {
		if(sprite != null) {
		return new Rectangle(
				parent.getFinalTransform().getPosition().x - (getSpriteWidth() * parent.getFinalTransform().getScale().x)/2f,
				parent.getFinalTransform().getPosition().y - (getSpriteHeight() * parent.getFinalTransform().getScale().y)/2f,
				getSpriteWidth() * parent.getFinalTransform().getScale().x,
				getSpriteHeight() * parent.getFinalTransform().getScale().y);
		}
		else {
			return new Rectangle(
					parent.getFinalTransform().getPosition().x,
					parent.getFinalTransform().getPosition().y,
					50 * parent.getFinalTransform().getScale().x,
					50 * parent.getFinalTransform().getScale().y);
		}
	}
	
	public float getSpriteWidth() {
		if(sprite instanceof NinePatchDrawable) {
			NinePatchDrawable cast = (NinePatchDrawable) sprite;
			return cast.getPatch().getTotalWidth();
		}
		else if(sprite instanceof TextureRegionDrawable) {
			TextureRegionDrawable cast = (TextureRegionDrawable) sprite;
			return cast.getRegion().getRegionWidth();
		}
		return 0;
	}
	
	public float getSpriteHeight() {
		if(sprite instanceof NinePatchDrawable) {
			NinePatchDrawable cast = (NinePatchDrawable) sprite;
			return cast.getPatch().getTotalHeight();
		}
		else if(sprite instanceof TextureRegionDrawable) {
			TextureRegionDrawable cast = (TextureRegionDrawable) sprite;
			return cast.getRegion().getRegionHeight();
		}
		return 0;
	}

	@Override
	public void draw(Batch sb, float parentAlpha) {
	
		if(sprite != null) {
			sb.setColor(tint.cpy().mul(1, 1, 1, parentAlpha));

			if(sprite instanceof NinePatchDrawable) {
				NinePatchDrawable cast = (NinePatchDrawable) sprite;
				cast.draw(sb,
						parent.getFinalTransform().getPosition().x - cast.getPatch().getTotalWidth()/2f * parent.getFinalTransform().getScale().x,
						parent.getFinalTransform().getPosition().y - cast.getPatch().getTotalHeight()/2f * parent.getFinalTransform().getScale().y,
						cast.getPatch().getTotalWidth()/2f * parent.getFinalTransform().getScale().x,
						cast.getPatch().getTotalHeight()/2f * parent.getFinalTransform().getScale().y,
						cast.getPatch().getTotalWidth() * parent.getFinalTransform().getScale().x,
						cast.getPatch().getTotalHeight() * parent.getFinalTransform().getScale().y,
						1,
						1,
						parent.getFinalTransform().getAngle()
						);
			}
			else if(sprite instanceof TextureRegionDrawable) {
				TextureRegionDrawable cast = (TextureRegionDrawable) sprite;
				cast.draw(sb,
						parent.getFinalTransform().getPosition().x - cast.getRegion().getRegionWidth()/2f * parent.getFinalTransform().getScale().x,
						parent.getFinalTransform().getPosition().y - cast.getRegion().getRegionHeight()/2f * parent.getFinalTransform().getScale().y,
						cast.getRegion().getRegionWidth()/2f * parent.getFinalTransform().getScale().x,
						cast.getRegion().getRegionHeight()/2f * parent.getFinalTransform().getScale().y,
						cast.getRegion().getRegionWidth() * parent.getFinalTransform().getScale().x,
						cast.getRegion().getRegionHeight() * parent.getFinalTransform().getScale().y,
						1,
						1,
						parent.getFinalTransform().getAngle()
						);

			}
		}
		
		sb.setColor(1, 1, 1, 1);
	}
	
	public void drawToEditor(Batch sb, float parentAlpha) {
		draw(sb, parentAlpha);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void read(Element comp, FileHandle relativePath) {
		Element sprt = comp.getChildByName("sprite");
		if(sprt == null) {
			System.err.println("Sprite not found");
		}
		else {
					
			if(sprt.getAttribute("loc", null) != null) {
				if(relativePath != null) {
					FileHandle fh = new FileHandle(relativePath.path() + "/" + sprt.getAttribute("loc"));
					setSprite(fh);
				}
				else {
					setSprite(Gdx.files.internal(sprt.getAttribute("loc")));
				}
			}
			
			flipX = Boolean.valueOf(sprt.getAttribute("flipX"));
			flipY = Boolean.valueOf(sprt.getAttribute("flipY"));
			
			Element tint = sprt.getChildByName("tint");
			if(tint != null) {
				this.tint.set(tint.getFloat("r"), tint.getFloat("g"), tint.getFloat("b"), tint.getFloat("a"));
			}
			else {
				this.tint = Color.WHITE.cpy();
			}
		}
	}

	public boolean isFlipX() {
		return flipX;
	}

	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}

	public boolean isFlipY() {
		return flipY;
	}

	public void setFlipY(boolean flipY) {
		this.flipY = flipY;
	}

	public boolean validate(LireObject obj) {
		return obj.getComponent(SpriteComponent.class) == null;
	}

	public void act(float delta) {
		
	}

	@Override
	public VisTable getUITable(DragAndDrop dragndrop) {
		VisTable table = new VisTable();
		table.align(Align.topLeft);		
		table.add(new VisLabel("Sprite")).pad(5);
		VisTextField spriteLoc = new VisTextField(getLocalPath(spriteHandle));
		spriteLoc.setDisabled(true);
		
		dragndrop.addTarget(new Target(spriteLoc) {
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				if(payload.getObject() instanceof FileHandle) {
					FileHandle fh = (FileHandle) payload.getObject();
					return fh.name().endsWith(".png") || fh.name().endsWith(".jpg");
				}
				return false;
			}

			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				FileHandle fh = (FileHandle) payload.getObject();
				setSprite(fh);
				spriteLoc.setText(getLocalPath(fh));
			}
			
		});
		table.add(spriteLoc).pad(5).row();
		
		table.add(new VisLabel("Color")).pad(5);
		
		VisTextButton colorbtn = new VisTextButton("Pick color");
		colorbtn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
									
				ColorPicker colorpicker = new ColorPicker(new ColorPickerListener() {
					public void reset(Color previousColor, Color newColor) {
						tint.set(previousColor);
					}

					public void finished(Color newColor) {
						tint.set(newColor);
					}
					
					public void changed(Color newColor) {
						tint.set(newColor);
					}
					
					public void canceled(Color oldColor) {
						tint.set(oldColor);
					}
				});
				
				colorpicker.pack();
				colorpicker.setResizable(true);
				colorpicker.centerWindow();
				colorpicker.getPicker().setColor(tint);

				//T� meio bugado, ele n�o abre com a cor default
				
				colorbtn.getStage().addActor(colorpicker);
				
				super.clicked(event, x, y);
			}
		});
		
		table.add(colorbtn).pad(5).row();
		
		return table;
	}



	@Override
	public Element getXmlElement(Element parent) {
		Element root = new Element("component", parent);
		root.setAttribute("class", getClass().getCanonicalName());
		
		Element spr = new Element("sprite", root);
		root.addChild(spr);
		spr.setAttribute("loc", getLocalPath(spriteHandle));
		spr.setAttribute("flipX", flipX + "");
		spr.setAttribute("flipY", flipY + "");
		
		if(!tint.equals(Color.WHITE)) {
			Element col = new Element("tint", spr);
			col.setAttribute("r", tint.r + "");
			col.setAttribute("g", tint.g + "");
			col.setAttribute("b", tint.b + "");
			col.setAttribute("a", tint.a + "");
			spr.addChild(col);
		}
			
		return root;
	}

	@Override
	public String getValidationCause() {
		return "Object already has a SpriteComponent!";
	}


	

}
