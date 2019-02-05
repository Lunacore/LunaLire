package br.com.lunacore.lunalire.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import br.com.lunacore.math.Transform;

public class UIConstants {
	
	public static VisTable getTransformConfigTable(Transform transform) {
		VisTable table = new VisTable();
		table.align(Align.topLeft);
		
		VisLabel posLbl = new VisLabel("Position");
		table.add(posLbl).pad(5);

		VisLabel posXlbl = new VisLabel("X");
		table.add(posXlbl).pad(5).maxWidth(10).minWidth(10).prefWidth(10);
		final VisValidatableTextField xTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		xTxt.setText(transform.getPosition().x + "");
		xTxt.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(xTxt.isInputValid()) {
					float posx = Float.parseFloat(xTxt.getText());
					transform.getPosition().x = posx;
				}
			}
		});
		table.add(xTxt).maxWidth(50).minWidth(50).pad(5);
		
		VisLabel posYlbl = new VisLabel("Y");
		table.add(posYlbl).pad(5).maxWidth(10).minWidth(10);
		final VisValidatableTextField yTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		yTxt.setText(transform.getPosition().y + "");
		yTxt.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(yTxt.isInputValid()) {
					float posy = Float.parseFloat(yTxt.getText());
					transform.getPosition().y = posy;
				}
			}
		});
		table.add(yTxt).maxWidth(50).minWidth(50).pad(5);
		
		table.row();
		
		VisLabel rotLbl = new VisLabel("Rotation");
		table.add(rotLbl).pad(5);
		final VisValidatableTextField angleTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		angleTxt.setText(transform.getAngle() + "");
		angleTxt.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(angleTxt.isInputValid()) {
					float angle = Float.parseFloat(angleTxt.getText());
					transform.setAngle(angle);
				}
			}
		});
		table.add(angleTxt).colspan(4).pad(5);
		
		table.row();
		
		VisLabel sclLbl = new VisLabel("Scale");
		table.add(sclLbl).pad(5);

		VisLabel sclXlbl = new VisLabel("X");
		table.add(sclXlbl).pad(5).maxWidth(10).minWidth(10).prefWidth(10);
		final VisValidatableTextField sxTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		sxTxt.setText(transform.getScale().x + "");
		sxTxt.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(sxTxt.isInputValid()) {
					float val = Float.parseFloat(sxTxt.getText());
					transform.getScale().x = val;
				}
			}
		});
		table.add(sxTxt).maxWidth(50).minWidth(50).pad(5);
		
		VisLabel sclYlbl = new VisLabel("Y");
		table.add(sclYlbl).pad(5).maxWidth(10).minWidth(10);
		final VisValidatableTextField syTxt = new VisValidatableTextField(InputValidatorConstants.floatValidator);
		syTxt.setText(transform.getScale().y + "");
		syTxt.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(syTxt.isInputValid()) {
					float val = Float.parseFloat(syTxt.getText());
					transform.getScale().y = val;
				}
			}
		});
		table.add(syTxt).maxWidth(50).minWidth(50).pad(5);
		
		return table;
	}

}
