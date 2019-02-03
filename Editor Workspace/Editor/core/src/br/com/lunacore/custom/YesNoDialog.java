package br.com.lunacore.custom;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class YesNoDialog extends VisDialog{
	
	public interface DialogListener{
		public void accept(Object value);
		public void reject();
	}

	public YesNoDialog(String title, String text, final DialogListener listener) {
		super(title);

		clear();
					
		VisTable respTable = new VisTable();
		respTable.align(Align.center);
		
		VisTextButton yesButton = new VisTextButton("Yes");
		
		yesButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				listener.accept(null);
				close();
			}
		});
		
		VisTextButton noButton = new VisTextButton("No");
		
		noButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				listener.reject();
				close();
			}
		});
		
		respTable.add(yesButton).align(Align.center).pad(5);
		respTable.add(noButton).align(Align.center).pad(5);
		
		VisLabel lbl = new VisLabel(text);
		lbl.setAlignment(Align.center);
		add(lbl).grow().align(Align.top).row();
		add(respTable).grow().align(Align.bottom);
		centerWindow();
		setWidth(500);
		setResizable(true);
	}

}