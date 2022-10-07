package io.github.jadefalke2;

import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.Button;
import io.github.jadefalke2.util.CorruptedScriptException;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class InputLine {

	// the buttons currently pressed
	public EnumSet<Button> buttons = EnumSet.noneOf(Button.class);

	// the stick positions
	private StickPosition stickL, stickR;


	// Contructors

	public InputLine() {
		stickL = new StickPosition(0,0);
		stickR = new StickPosition(0,0);
	}

	public InputLine(String full) throws CorruptedScriptException {
		if (full.isEmpty()){
			throw new CorruptedScriptException("Empty InputLine", -1);
		}

		int frame = 0;
		try {
			String[] components = full.split(" ");

			frame = Integer.parseInt(components[0]);
			String buttons = components[1];
			String[] buttonsPressed = buttons.split(";");

			for (String s : buttonsPressed) {
				if(!s.equals("NONE")) {
					this.buttons.add(Button.valueOf(s));
				}
			}

			stickL = new StickPosition(components[2]);
			stickR = new StickPosition(components[3]);
		} catch (Exception e) {
			throw new CorruptedScriptException("Script corrupted", frame, e);
		}
	}

	/**
	 * Returns a new Inputline with no buttons pressed and both sticks at 0;0
	 * @return the new input line
	 */
	public static InputLine getEmpty (){
		return new InputLine();
	}


	// getter + setter (special)

	/**
	 * creates and returns a new string that contains all information about this input line
	 * @return the created string
	 */
	public String getFull(int frame) {
		return frame + " "
			+ (buttons.isEmpty() ? "NONE" : buttons.stream().map(Enum::name).collect(Collectors.joining(";"))) + " "
			+ getStickL().toCartString() + " "
			+ getStickR().toCartString();
	}

	/**
	 * creates and returns an array with all data of the lines, used to display it in a JTable
	 * @return the Object array
	 */
	public Object[] getArray (int frame){
		Object[] tmp = new Object[3+Button.values().length];
		tmp[0] = frame;
		tmp[1] = stickL.toCartString();
		tmp[2] = stickR.toCartString();

		for (int i=0;i<Button.values().length;i++) {
			tmp[i + 3] = buttons.contains(Button.values()[i]) ? Button.values()[i].toString() : "";
		}
		return tmp;
	}

	/**
	 * returns wether the input line has no buttons pressed and its stick positions are on 0;0
	 * @return if the line is empty
	 */
	public boolean isEmpty (){
		return buttons.isEmpty() && stickR.isZeroZero() && stickL.isZeroZero();
	}

	// Getter + setter (normal)

	public StickPosition getStickL() {
		return stickL;
	}

	public StickPosition getStickR() {
		return stickR;
	}

	public void setStickL(StickPosition stickL) {
		this.stickL = stickL;
	}

	public void setStickR(StickPosition stickR) {
		this.stickR = stickR;
	}


	// Overwriting methods

	/**
	 * returns a new Inputline with excatly the same data as the current one
	 * @return the new Input line
	 */
	@Override
	public InputLine clone(){
		InputLine newLine = new InputLine();
		newLine.buttons = buttons.clone();
		newLine.stickL = stickL.clone();
		newLine.stickR = stickR.clone();
		return newLine;
	}
}
