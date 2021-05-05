package io.github.jadefalke2.actions;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.util.CorruptedScriptException;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LineAction implements Action{

	public enum Type {
		DELETE,
		INSERT,
		CLONE
	}

	private final Type type;
	private final DefaultTableModel table;
	private final Script script;
	private final int[] rows;
	private final InputLine[] previousLines;

	public LineAction(DefaultTableModel table, Script script, int[] rows, Type type) {

		previousLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			previousLines[i] = script.getInputLines().get(i).clone();
		}

		this.table = table;
		this.script = script;
		this.rows = rows;
		this.type = type;
	}

	//TODO UNDO

	@Override
	public void execute() {
		switch (type){
			case CLONE:
				cloneRows();
				break;

			case DELETE:
				deleteRows();
				break;

			case INSERT:
				insertRows();
				break;
		}

	}

	@Override
	public void revert() {
		switch (type){
			case CLONE:
			case INSERT:
				//deleteRows();
				break;

			case DELETE:
				//
				break;
		}
	}



	private void adjustLines(int start, int amount) {
		for (int i = start; i < table.getRowCount(); i++){

			InputLine currentLine = script.getInputLines().get(i);

			currentLine.setFrame(currentLine.getFrame() + amount);
			table.setValueAt(currentLine.getFrame(),i,0);
		}
	}


	private void cloneRows(){
		InputLine[] tmpLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			try {
				tmpLines[i] = new InputLine(script.getInputLines().get(rows[0] + i).getFull());
			} catch (CorruptedScriptException e) {
				e.printStackTrace();
			}
		}

		insertRows(tmpLines);
	}

	private void deleteRows(){
		for (int i = rows.length - 1; i >= 0; i--){
			int actualIndex = rows[0] + i;

			script.getInputLines().remove(actualIndex);
			table.removeRow(actualIndex);
		}

		adjustLines(rows[0], -rows.length);
	}

	private void insertRows(){

		InputLine[] tmpLines = new InputLine[rows.length];

		for (int i = 0; i < rows.length; i++){
			tmpLines[i] = InputLine.getEmpty(rows[0] + i);
		}

		insertRows(tmpLines);
	}

	private void insertRows (InputLine[] inputLines){

		// script
		for (int i = 0; i < inputLines.length; i++){

			int actualIndex = rows[0] + i;

			script.getInputLines().add(actualIndex, inputLines[i]);
		}

		// table

		for (int i = 0; i < inputLines.length; i++){

			int actualIndex = rows[0] + i;

			table.insertRow(actualIndex, script.getInputLines().get(actualIndex).getArray());
		}

		adjustLines(rows[0] + rows.length, rows.length);

	}


}
