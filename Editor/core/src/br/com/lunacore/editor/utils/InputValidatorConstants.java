package br.com.lunacore.editor.utils;

import com.kotcrab.vis.ui.util.InputValidator;

public class InputValidatorConstants {
	
	public static InputValidator floatValidator;
	public static InputValidator intValidator;
	
	static {
		
		floatValidator = new InputValidator() {
			public boolean validateInput(String input) {
				try {
					Float.parseFloat(input);
					return true;
				}
				catch (Exception e) {
					return false;
				}
			}
		};
		
		intValidator = new InputValidator() {
			public boolean validateInput(String input) {
				try {
					Integer.parseInt(input);
					return true;
				}
				catch (Exception e) {
					return false;
				}
			}
		};
	}

}
