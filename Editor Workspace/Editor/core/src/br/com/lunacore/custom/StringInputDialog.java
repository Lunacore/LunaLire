package br.com.lunacore.custom;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import br.com.lunacore.custom.YesNoDialog.DialogListener;

public class StringInputDialog extends VisDialog{

	String textInput;
	
	public StringInputDialog(String title, String text, final DialogListener listener) {
		this(title, text, listener, new InputValidator() {
			public boolean validateInput(String input) {
				return true;
			}
		});
	}
	
	public StringInputDialog(String title, String text, final DialogListener listener, InputValidator validator) {
		super(title);

		clear();
		
		VisLabel lbl = new VisLabel(text);
		lbl.setAlignment(Align.center);
		add(lbl).growX().align(Align.top).row();
		
		final VisValidatableTextField field = new VisValidatableTextField(validator);
		add(field).growX().align(Align.center).row();
		
		VisTable respTable = new VisTable();
		respTable.align(Align.center);
		
		VisTextButton yesButton = new VisTextButton("Ok");
		
		yesButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if(field.isInputValid()) {
					textInput = field.getText();
					listener.accept(textInput);
					close();
				}
			}
		});
		
		VisTextButton noButton = new VisTextButton("Cancel");
		
		noButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				listener.reject();
				close();
			}
		});
		
		respTable.add(yesButton).align(Align.center).pad(5);
		respTable.add(noButton).align(Align.center).pad(5);
		
		add(respTable).growX().align(Align.bottom);
		centerWindow();
		setWidth(500);
		setResizable(true);
	}
	
	public String getText() {
		return textInput;
	}
	
	

}
