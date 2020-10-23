package assignment3;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI implements MouseListener{
	JToggleButton [] buttons = new JToggleButton [64];
	Minesweeper board;
	JFrame frame = new JFrame("Mines");
	boolean firstMove;
	boolean solved = false;
	JPanel panel = new JPanel();
	GridBagConstraints constraints = new GridBagConstraints();
	Solver solver;
	
	/*
	 * Creates an instance of a GUI with game state
	 */
	public GUI(Minesweeper mine) {
		board = mine;
		firstMove = true;
		initButtons();
	}
	
	/*
	 * Creates new JToggleButtons and puts them in an array so they can be referenced and changed when the user makes a move
	 */
	public void initButtons() {
		for(int i = 0; i<buttons.length; i++) {
			buttons[i] = new JToggleButton();
		}
	}
	
	/*
	 * shows the GUI and sets its overall size.
	 */
	public void createAndShowGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setPreferredSize(new Dimension(600, 650));
		
		addComponentsToPane(frame.getContentPane());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	/*
	 * Sets text of the JToggleButtons which the user clicks on to play the game to "  " and adds them to a JPanel which is placed
	 * at the top of the GUI. The Solve button is placed in another JPanel and placed at the bottom of the GUI
	 */
	public void addComponentsToPane(Container pane) {
		panel.setLayout(new GridBagLayout());
		for(int r = 0; r<8; r++) {
			for(int c = 0; c<8; c++) {
				constraints.ipadx = 10;
				constraints.ipady = 35;
				constraints.gridx = c;
				constraints.gridy = r;
				JToggleButton button = buttons[8*r+c];
				button.setText("  ");
				button.addMouseListener(this);
				panel.add(button, constraints);
			}
		}
		JPanel panel2 = new JPanel();
		JButton solveButton = new JButton("Solve");
		solveButton.setPreferredSize(new Dimension(80, 40));
		GUI gui = this;
		solveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)  {
				solver = new Solver(board, 4, 0, gui);
				solver.solve();
				solver.sendSolutions();
			}
		});
		panel2.add(solveButton);
		
		pane.add(panel, BorderLayout.NORTH);
		pane.add(panel2, BorderLayout.SOUTH);
	}
	
	/*
	 * Whenever the user left clicks on a square, this method is called. It sets sets the button that user pressed to "pressed"
	 * and sets the text of that button to the number of mines that button borders. At the end it checks to see if the user has 
	 * won the game
	 */
	public void updateGUI() {
		long tempBoard = board.getClickBoard();
		for(int row = 0; row<8; row++) {
			for(int col = 0; col<8; col++) {
				if((tempBoard & (1L << (8*row+col)))!=0) {
					int numborders = board.borders(row, col);
					switch(numborders) {
						case 0: buttons[8*row+col].setText("  ");
						break;
						case 1: buttons[8*row+col].setText("1");
						break;
						case 2: buttons[8*row+col].setText("2");
						break;
						case 3: buttons[8*row+col].setText("3");
						break;
						case 4: buttons[8*row+col].setText("4");
						break;
						case 5: buttons[8*row+col].setText("5");
						break;
						case 6: buttons[8*row+col].setText("6");
						break;
						case 7: buttons[8*row+col].setText("7");
						break;
						case 8: buttons[8*row+col].setText("8");
						break;
					}
					buttons[8*row+col].setSelected(true);
				}
			}
			
		}
		if(board.isSolved()) {
			System.out.println("YOU WIN!!!");
		}
		System.out.println(board);
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Based on which mouse button that the user clicked on square with different actions are performed. If the user used the left
	 * mouse button, the clickSquare() method is called in the Minesweeper class to determine if the move was valid. If the 
	 * right mouse button is clicked, then the square is marked with an "M" or unmarked if it was already marked.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		//change to right click to mark?? figure it out!!
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3) {
			JToggleButton button = (JToggleButton)(e.getComponent());
			int spot = findButton(button);
			int row = spot/8;
			int col = spot%8;
			boolean marked = board.markSquare(row, col);
			if(!marked) {
				button.setText("  ");
			}
			else {
				button.setText("M");
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON1) {
			if(firstMove) {
				JToggleButton button = (JToggleButton)(e.getComponent());
				int spot = findButton(button);
				int row = spot/8;
				int col = spot%8;
				board.initBoard(row, col);
				board.clickSquare(row, col);
				updateGUI();
				firstMove = false;
				return;
			}
			JToggleButton button = (JToggleButton)(e.getComponent());
			int spot = findButton(button);
			int row = spot/8;
			int col = spot%8;
			//System.out.println("("+row+", "+col+")");
			boolean endgame = board.clickSquare(row, col);
			if(endgame) {
				System.out.println("YOU LOSE");
				System.exit(0);
			}
			else {
				updateGUI();
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	} 
	
	/*
	 * finds the index where this button is in the array. This is necessary because the array is laid out the same way as the 
	 * long containing where the user has clicked in the Minesweeper class. When a button is clicked, the index of where that
	 * button is in the array is needed to call clickSquare() in order to check if the move was valid. 
	 */
	public int findButton(JToggleButton button) {
		for(int i = 0; i<buttons.length; i++) {
			if(button == buttons[i])
				return i;
		}
		return -1;
	} 
	
	/*
	 * sets the current game state to a new game state
	 */
	public void setBoard(Minesweeper m) {
		board = m;
	}
}
