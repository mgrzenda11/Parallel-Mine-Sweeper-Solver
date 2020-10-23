package assignment3;

public class Main {
	/*
	 * This is the class which runs the program
	 */
	public static void main (String args []) {
		GUI gui = new GUI(new Minesweeper(10));
		gui.createAndShowGUI();
	}
}
