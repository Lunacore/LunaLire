package br.com.lunacore.custom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField.VisTextFieldStyle;

import br.com.lunacore.Editor;

public class EditorRuler extends VisTable{

	ShapeRenderer sr;
	BitmapFont font;
	
	boolean vertical;
	
	float step = 100;
	
	public EditorRuler(boolean vertical) {
		this.vertical = vertical;
		sr = new ShapeRenderer();
		font = getSkin().get(VisTextFieldStyle.class).font;
		
		if(vertical) {
			setWidth(30);
		}
		else {
			setHeight(30);
		}
		
		
	}
	
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		step = (float) Math.pow(10, (int)(Math.log10(Editor.getInstance().getStage().getWorldCamera().zoom * 100)/Math.log10(10)));
		
		if(vertical) {
			Vector3 bottom = new Vector3(getX(), Gdx.graphics.getHeight() - getY(), 0);
			Editor.getInstance().getStage().getWorldCamera().unproject(bottom);
			bottom.y = (int)(bottom.y / step) * step;
			
			Vector3 top = new Vector3(getX(), Gdx.graphics.getHeight() - (getY() + getHeight()), 0);
			Editor.getInstance().getStage().getWorldCamera().unproject(top);
			top.y = (int)(top.y / step) * step;
			
			batch.end();
			sr.setProjectionMatrix(batch.getProjectionMatrix());
			sr.begin(ShapeType.Filled);
				sr.setColor(.3f, .3f, .3f, .7f);
				sr.rect(getX(), getY(), getWidth(), getHeight());
			sr.end();
			sr.begin(ShapeType.Line);
			sr.setColor(Color.WHITE);
			for(float i = bottom.y; i <= top.y; i += step) {
				
				Vector3 bottomScreen = Editor.getInstance().getStage().getWorldCamera().project(new Vector3(bottom.x, i, 0));
				
				if(i % (step*5) == 0) {
					sr.line(getX() + getWidth(), bottomScreen.y, getX() + getWidth()/2f, bottomScreen.y);
					sr.end();
					
					batch.begin();
					font.draw(batch, i+"", getX()+2, bottomScreen.y);
					batch.end();
					
					sr.begin(ShapeType.Line);
				}
				else {
					sr.line(getX() + getWidth(), bottomScreen.y, getX() + 3*getWidth()/4f, bottomScreen.y);
				}
				
				
			}
			sr.line(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight());
			sr.line(getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight());
			sr.end();
			batch.begin();
		}
		else {
			Vector3 left = new Vector3(getX(), Gdx.graphics.getHeight() - getY(), 0);
			Editor.getInstance().getStage().getWorldCamera().unproject(left);
			left.x = (int)(left.x / step) * step;
			
			Vector3 right = new Vector3(getX() + getWidth(), Gdx.graphics.getHeight() - getY(), 0);
			Editor.getInstance().getStage().getWorldCamera().unproject(right);
			right.x = (int)(right.x / step) * step;
			
			batch.end();
			sr.setProjectionMatrix(batch.getProjectionMatrix());
			sr.begin(ShapeType.Filled);
				sr.setColor(.3f, .3f, .3f, .7f);
				sr.rect(getX(), getY(), getWidth(), getHeight());
			sr.end();
			sr.begin(ShapeType.Line);
			sr.setColor(Color.WHITE);
			for(float i = left.x; i <= right.x; i += step) {
				
				Vector3 leftScreen = Editor.getInstance().getStage().getWorldCamera().project(new Vector3(i, left.y, 0));
				
				if(i % (step*5) == 0) {
					sr.line(leftScreen.x, getY(), leftScreen.x, getY() + getHeight()/2f);
					sr.end();
					
					batch.begin();
					font.draw(batch, i+"", leftScreen.x+3, getY() + getHeight());
					batch.end();
					
					sr.begin(ShapeType.Line);
				}
				else {
					sr.line(leftScreen.x, getY(), leftScreen.x, getY() + getHeight()/4f);
				}
				
				
			}
			sr.line(getX(), getY(), getX() + getWidth(), getY());
			sr.line(getX(), getY(), getX(), getY() + getHeight());
			sr.end();
			batch.begin();
		}
	}
	
}
