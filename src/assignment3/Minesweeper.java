package assignment3;

import java.util.Random;
/*
 * A representation of Mines at a bit level. The game is represented with one long to keep track of where the mines are on the 
 * board, another long to keep track of where marks are on the board, and another long to keep track of where the user has clicked
 */
public class Minesweeper {
	long minesBoard;
	long marksOnBoard;
	long spacesClicked;
	long previousSpaces;
	final int mineNumber;
	int mineCount;
	int numberMarked;
	
	/*
	 * creates a new game with no spaces clicked, no marks on the board, and no mines and mineNumber of mines
	 */
	public Minesweeper(int mineNumber) {
		minesBoard = 0L;
		marksOnBoard = 0L;
		spacesClicked = 0L;
		this.mineNumber = mineNumber;
		mineCount = mineNumber;
		numberMarked = 0;
	}
	
	/*
	 * creates a Mines game with a current state specified by 3 longs which determine where the mines are in the game, what 
	 * spaces have been clicked, and where marks have been placed, and mineNumber number of mines.
	 */
	public Minesweeper(long minesBoard, long spacesClicked, long marksOnBoard, int mineNumber) {
		this.minesBoard = minesBoard;
		this.spacesClicked = spacesClicked;
		this.marksOnBoard = marksOnBoard;
		this.mineNumber = mineNumber;
	}
	
	/*
	 * Places the mines on the board in such a way which guarantees that the user will not click on a mine for their first move.
	 * Also, no mines are placed in any square which borders where the user made his/her first click. This is to achieve the 
	 * "opening up" that happens when you make a first move in Mines.
	 */
	public void initBoard(int row, int col) {
		int clickedSpot = -1;
		int north = -1;
		int northEast = -1;
		int east = -1;
		int southEast = -1;
		int south= -1;
		int southWest = -1;
		int west = -1;
		int northWest = -1;
		
		if(row != 0) north = (8*(row - 1)) + col; //north
		if(col != 7 && row != 0) northEast = (8*(row - 1)) + col+1; //northeast
		if(col != 7); east = (8*(row)) + col+1;//east
		if(col != 7 && row != 7) southEast = (8*(row + 1)) + col+1; //southeast
		if(row != 7) south = (8*(row + 1)) + col; //south
		if(col != 0 && row != 7) southWest = (8*(row + 1)) + col-1; //southwest
		if(col != 0) west = (8*(row)) + col-1; //west
		if(col != 0 && row != 0) northWest = (8*(row - 1)) + col-1; //northwest
		
		int index = 0;
		Random r = new Random();
		while(index < mineNumber) {
			int rrow = r.nextInt(8);
			int rcol = r.nextInt(8);
			int spot = 8*rrow + rcol;
			if(spot != clickedSpot && spot != north && spot != northEast && spot != east && spot != southEast 
					&& spot != south && spot != southWest && spot != west && spot != northWest 
					&& (minesBoard & (1L << spot)) == 0) {
				minesBoard |= (1L << spot);
				index++;
			}
		}
	}
	
	/*
	 * Adds a bit to the long marksOnBoard in the spot that the user clicked if it isn't already marked. Otherwise the mark
	 * is removed. Originally to win the game, I was going to compare the number of mines that the user marked to the number of 
	 * mines on the board, however, this ended up being problematic since you would win the game without clicking all of the safe
	 * squares.
	 */
	public boolean markSquare(int row, int col) {
		int spot = 8*row + col;
		if((marksOnBoard & (1L << spot)) == 0 && (minesBoard & (1L << spot)) != 0 && mineCount != 0) {
			marksOnBoard |= (1L << spot);
			numberMarked++;
			mineCount--;
			return true;
		}
		
		else if((marksOnBoard & (1L << spot)) == 0 && mineCount != 0) {
			marksOnBoard |= (1L << spot);
			mineCount--;
			return true;
		}
		else {
			marksOnBoard &= ~(1L << spot);
			if(mineCount < mineNumber)
				mineCount++;
			return false;
		}
	}
	
	/*
	 * This method checks to see whether or not the game is solved by essentially asking, "Is everything except the mines clicked"
	 */
	public boolean isSolved() {
		long solved = ~spacesClicked;
		if(solved == minesBoard) {
			return true;
		}
		return false;
	}
	
	/*
	 * returns a list of borders that the square located at (row, col) has 
	 */
	public long getborders(int row, int col) {
		int spot = 8*row + col;
		long borders = 0L;
		if(row != 0) borders |= (1L << spot); //north
		
		if(col != 7 && row != 0) borders |= (1L << spot); //northeast
		
		if(col != 7) borders |= (1L << spot); //east
		
		if(col != 7 && row != 7) borders |= (1L << spot); //southeast
		
		if(row != 7) borders |= (1L << spot); //south
		
		if(col != 0 && row != 7) borders |= (1L << spot); //southwest
		
		if(col != 0) borders |= (1L << spot); //west
		
		if(col != 0 && row != 0) borders |= (1L << spot); //northwest
		
		return borders;
	}
	
	/*
	 * returns the number of borders that the sqare located at (row, col) has 
	 */
	public int borders(int row, int col) {
		int spot = 8*row + col;
		int numBorders = 0;
		if(row != 0 && (minesBoard & (1L << spot-8))!=0) numBorders++; //north
		
		if(col != 7 && row != 0 && (minesBoard & (1L << spot-7))!=0) numBorders++; //northeast
		
		if(col != 7 && (minesBoard & (1L << spot+1))!=0) numBorders++; //east
		
		if(col != 7 && row != 7 && (minesBoard & (1L << spot+9))!=0) numBorders++; //southeast
		
		if(row != 7 && (minesBoard & (1L << spot+8))!=0) numBorders++; //south
		
		if(col != 0 && row != 7 && (minesBoard & (1L << spot+7))!=0) numBorders++; //southwest
		
		if(col != 0 && (minesBoard & (1L << spot-1))!=0) numBorders++; //west
		
		if(col != 0 && row != 0 && (minesBoard & (1L << spot-9))!=0) numBorders++; //northwest
		
		return numBorders;
	}
	
	/*
	 * Checks to see if a square has 0 borders, meaning that the square doesn't border a mine, a wall, or any squares that have
	 * already been clicked
	 */
	public boolean bordersNothing(int row, int col) {
		int spot = 8*row+col;
		if(row != 0 && (minesBoard & (1L << spot-8))!=0) return false; //north
		
		if(col != 7 && row != 0 && (minesBoard & (1L << spot-7))!=0) return false; //northeast
		
		if(col != 7 && (minesBoard & (1L << spot+1))!=0) return false; //east
		
		if(col != 7 && row != 7 && (minesBoard & (1L << spot+9))!=0) return false; //southeast
		
		if(row != 7 && (minesBoard & (1L << spot+8))!=0) return false; //south
		
		if(col != 0 && row != 7 && (minesBoard & (1L << spot+7))!=0) return false; //southwest
		
		if(col != 0 && (minesBoard & (1L << spot-1))!=0) return false; //west
		
		if(col != 0 && row != 0 && (minesBoard & (1L << spot-9))!=0) return false; //northwest
		
		return true;
	}
	
	
	/*
	 * Sets the bit at 8*row+col in spacesClicked to 1 if space is not already clicked. If the space contains a mine, this method 
	 * returns true, and the game ends. In the event that a square doesn't border a mine, then this method is called recursively
	 * on all of its neighboring squares.
	 */
	public boolean clickSquare(int row, int col) {
		previousSpaces = spacesClicked;
		int spot = 8*row + col;
		if((spacesClicked & (1L << spot)) != 0){
			return false;
		}
		if((minesBoard & (1L << spot)) != 0) {
			//end game
			return true;
		}
		else {
			if(borders(row, col)==0) {
				spacesClicked |= (1L << spot);
				if((marksOnBoard & (1L << spot))==0) {
					if(row != 0) clickSquare(row-1, col); //north
					if(col != 7 && row != 0) clickSquare(row - 1, col+1); //northeast
					if(col != 7) clickSquare(row, col+1); //east
					if(col != 7 && row != 7) clickSquare(row+1, col+1); //southeast
					if(row != 7) clickSquare(row+1, col); //south
					if(col != 0 && row != 7) clickSquare(row+1, col-1); //southwest
					if(col != 0 ) clickSquare(row, col - 1); //west
					if(col != 0 && row != 0) clickSquare(row-1, col-1); //northwest
					
					return false;
				}
				
			}
			else {
				if((spacesClicked & (1L << spot)) == 0) {
					spacesClicked |= (1L << spot);
				}
			}
			return false;
		}
	
	}
	
	/*
	 * Creates a string representing the game state. The minesBoard, spacesClicked, and marksOnBoard are all concatenated in a 
	 * grid like fashion where 1 represents the long containing the bit and 0 means that the long does not contain the bit
	 * 
	 */
	@Override
	public String toString() {
		String toRet = "";
		toRet += "Mark Board: Size: " + Long.bitCount(marksOnBoard)+ "\n";
		for(int row = 0; row<8; row++) {
			for(int col = 0; col<8; col++) {
				int spot = 8*row + col;
				if((marksOnBoard & (1L << spot)) != 0) {
					toRet += 1 + " ";
				}
				else {
					toRet += 0 + " ";
				}
			}
			toRet += "\n";
		}
		
		toRet+= "\n\nMine Board: Size: " + Long.bitCount(minesBoard)+ "\n";
		for(int row = 0; row<8; row++) {
			for(int col = 0; col<8; col++) {
				int spot = 8*row + col;
				if((minesBoard & (1L << spot)) != 0) {
					toRet += 1 + " ";
				}
				else {
					toRet += 0 + " ";
				}
			}
			toRet += "\n";
		} 
		toRet+= "Click Board: Size: " + Long.bitCount(spacesClicked)+ "\n";
		for(int row = 0; row<8; row++) {
			for(int col = 0; col<8; col++) {
				int spot = 8*row + col;
				if((spacesClicked & (1L << spot)) != 0) {
					toRet += 1 + " ";
				}
				else {
					toRet += 0 + " ";
				}
			}
			toRet += "\n";
		}
		return toRet;
	}
	
	public long getClickBoard() {
		return spacesClicked;
	}
	
	public void setClicks(long click) {
		spacesClicked = click;
	}
	
	public long getMinesBoard() {
		return minesBoard;
	}
	
	public long getMarks() {
		return marksOnBoard;
	}
	
	public long getPreviousBoard() {
		return previousSpaces;
	}
	
	public void reset() {
		spacesClicked = previousSpaces;
	}
	
	public int getMineCount() {
		return mineCount;
	}
}
