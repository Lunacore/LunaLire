package br.com.lunacore.lunalire.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NinePatchLoader {
	
	public static NinePatch loadNinePatch(FileHandle handle) {
		Pixmap pm = new Pixmap(handle);
		
		int originalWidth = pm.getWidth() - 2;
		int originalHeight = pm.getHeight() - 2;
		
		//Calculate left and right
		Color pixColor = new Color(0, 0, 0, 0);
		int x = -1;
		do{
			x++;
			pixColor = new Color(pm.getPixel(x, 0));
		}while(pixColor.a == 0);
		int leftWidth = x - 1;
		do{
			x++;
			pixColor = new Color(pm.getPixel(x, 0));
		}while(pixColor.a == 1);
		int rightWidth = originalWidth - x - 1;
		
		//Calculate top and bottom
		pixColor = new Color(0, 0, 0, 0);
		int y = -1;
		do{
			y++;
			pixColor = new Color(pm.getPixel(0, y));
		}while(pixColor.a == 0);
		int topHeight = y - 1;
		do{
			y++;
			pixColor = new Color(pm.getPixel(0, y));
		}while(pixColor.a == 1);
		int bottomHeight = originalHeight - y - 1;

		Texture tex = new Texture(handle);
		
		NinePatch n = new NinePatch(
				//Top
				new TextureRegion(tex, 1, 1, leftWidth, topHeight),
				new TextureRegion(tex, 1 + leftWidth, 1, originalWidth - leftWidth - rightWidth, topHeight),
				new TextureRegion(tex, 1 + originalWidth - rightWidth, 1, rightWidth, topHeight),
				
				//Center
				new TextureRegion(tex, 1, 1 + topHeight, leftWidth, originalHeight - topHeight - bottomHeight),
				new TextureRegion(tex, 1 + leftWidth, 1 + topHeight, originalWidth - leftWidth - rightWidth, originalHeight - topHeight - bottomHeight),
				new TextureRegion(tex, 1 + originalWidth - rightWidth, 1 + topHeight, rightWidth, originalHeight - topHeight - bottomHeight),
				
				//Bottom
				new TextureRegion(tex, 1, 1 + originalHeight - bottomHeight, leftWidth, bottomHeight),
				new TextureRegion(tex, 1 + leftWidth, 1 + originalHeight - bottomHeight, originalWidth - leftWidth - rightWidth, bottomHeight),
				new TextureRegion(tex, 1 + originalWidth - rightWidth, 1 + originalHeight - bottomHeight, rightWidth, bottomHeight)
				);
		
		System.out.println("Loaded patch with splits: " + n.getLeftWidth() + ", " + n.getRightWidth() + ", " + n.getTopHeight() + ", " + n.getBottomHeight());
		
		return n;
	}
	

}
