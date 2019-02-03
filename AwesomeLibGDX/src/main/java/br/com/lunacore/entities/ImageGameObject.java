package br.com.lunacore.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.lunacore.helper.Helper;
import br.com.lunacore.states.State;

public class ImageGameObject extends GameObject{
	
	protected TiledMapTileMapObject imgObj;
	protected Body body;
	
	//called by tiled object instancer
	public ImageGameObject(ObjectInfo info, MapProperties properties) {
		super(info, properties);
		imgObj = get("this", TiledMapTileMapObject.class);
		body = get("body", Body.class);
		
		if(body != null) {
			transform.setPosition(0, 0);			
			transform.setAngle(0);
			transform.setScale(new Vector2(imgObj.getScaleX(), imgObj.getScaleY()));
			
			Vector2 relat = body.getPosition().cpy().sub(body.getWorldCenter()).rotate(imgObj.getRotation());
			
			body.setTransform(body.getPosition().add(relat), body.getAngle());
		}
	}
	
	public ImageGameObject(ObjectInfo info, TiledMapTileMapObject imgObj) {
		super(info, imgObj.getProperties());
		this.imgObj = imgObj;
		
		body = get("body", Body.class);
		
		if(body != null) {
			transform.getPosition().set(0, 0);
			transform.setAngle(0);
			transform.setScale(new Vector2(imgObj.getScaleX(), imgObj.getScaleY()));
		}
	}
	
	public Vector2 getWorldPosition() {
		
		if(body != null) {
			return body.getWorldCenter();
		}
		
		return transform.getPosition();
		
	}

	public void render(SpriteBatch sb, ShapeRenderer sr, OrthographicCamera camera) {
		if(imgObj.getProperties().get("render") == null || imgObj.getProperties().get("render", Boolean.class) ) {
			TextureRegion region = imgObj.getTile().getTextureRegion();
			if(imgObj.getTile() instanceof AnimatedTiledMapTile) {
				region = ((AnimatedTiledMapTile)imgObj.getTile()).getCurrentFrame().getTextureRegion();
				AnimatedTiledMapTile.updateAnimationBaseTime();
			}
			
			if(body != null) {
				renderBodyRegion(sb, region, body, imgObj.isFlipHorizontally(), imgObj.isFlipVertically());
				
				//Desenha uma linha entre o bodyPosition e o bodyCenter
				/*
				sb.end();
				
				sr.begin(ShapeType.Filled);
				sr.setColor(Color.WHITE);
				
				sr.rectLine(body.getPosition(), body.getWorldCenter(), 5 / State.PHYS_SCALE);
				
				sr.end();
				
				sb.begin();
				*/
			}
			else {
				Helper.renderRegion(
						sb,
						region,
						new Vector2(imgObj.getX(), imgObj.getY()).cpy().scl(getMapScale()/State.PHYS_SCALE).add(transform.getPosition()),
						360 - imgObj.getRotation() + transform.getAngle(),
						new Vector2(getMapScale() * imgObj.getScaleX() / State.PHYS_SCALE, getMapScale() * imgObj.getScaleY() / State.PHYS_SCALE).scl(transform.getScale()),
						imgObj.isFlipHorizontally(),
						imgObj.isFlipVertically(),
						new Vector2(getMapScale() * imgObj.getOriginX() / State.PHYS_SCALE, getMapScale() * imgObj.getOriginY() / State.PHYS_SCALE)
						);
			}
	}
		
	}

	@Override
	public boolean update(float delta) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		
		if(body != null) {
			getState().deleteBody(body);
		}
		
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

}
