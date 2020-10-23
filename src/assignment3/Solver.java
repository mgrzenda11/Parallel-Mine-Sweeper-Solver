package assignment3;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * A parallel move evaluator for the game Mines from Simon Tathom's Portable Puzzle Collection.
 */
public class Solver {
	private class SplitterTask extends RecursiveTask<Minesweeper>{
		Minesweeper origin;
		int currentDepth;
		final int MAXDEPTH;
		ConcurrentHashMap<Integer, Minesweeper> movesMade = new ConcurrentHashMap<>();
		
		/*
		 * Creates a new SplitterTask with the current Minesweeper state, current depth, max depth, and the current 
		 * ConcurrentHashTable
		 */
		public SplitterTask (Minesweeper origin, int depth, int currentDepth, ConcurrentHashMap<Integer, Minesweeper> movesMade) {
			this.origin = origin;
			MAXDEPTH = depth;
			this.currentDepth = currentDepth;
			this.movesMade = movesMade;
		}
		
		/*
		 * Main algorithm for finding the solution to a game of Mines
		 * 
		 * First, This method takes the current state of the game of Mines and calls method getPossibleMoves() to obtain every
		 * possible legal move that can be made from that state. Second, For each move in the list of possible moves,
		 * that move is performed and then a new SplitterTask is created with the new game state and added to a list of 
		 * ForkJoinTasks. Third, each SplitterTask in the ArrayList of ForkJoinTasks is then forked off and the process repeats
		 * until the maximum depth of the dag. When this happens, every game that is at a leaf is directly solved and the 
		 * leaf's game state is returned. All tasks are then joined, and a solution sequence is found. 
		 */
		@Override
		public Minesweeper compute() {
			if(currentDepth == MAXDEPTH) { //Base case
				solveDirectly();
				return origin;
			}
			else {
				currentDepth++;
				List<ForkJoinTask<Minesweeper>> tasks = new ArrayList<ForkJoinTask<Minesweeper>>();
				List<Integer> moves = getPossibleMoves();
				for(Integer m: moves) {
					Minesweeper mine = new Minesweeper(origin.getMinesBoard(), origin.getClickBoard(), origin.getMarks(), origin.getMineCount());
					mine.clickSquare(m/8, m%8);
					Minesweeper temp = new Minesweeper(mine.getMinesBoard(), mine.getClickBoard(), mine.getMarks(), mine.getMineCount());
					movesMade.putIfAbsent(Long.bitCount(mine.getClickBoard()), temp);
					tasks.add(new SplitterTask(mine, MAXDEPTH, currentDepth, movesMade));
				}
				ForkJoinTask.invokeAll(tasks);
	
				List<Minesweeper> results = new ArrayList<>();
				for(ForkJoinTask<Minesweeper> f: tasks) {
					Minesweeper m = f.join();
					results.add(m);
					movesMade.putIfAbsent(Long.bitCount(m.getClickBoard()), m);
				}
				return compute();
			}
			
		}
		
		/*
		 * finds all possible moves that can be made in the current game state. A legal move is one where mine will not 
		 * explode when that space is clicked, if that space has not already been clicked, and a space that has not 
		 * been marked. 
		 */
		public ArrayList<Integer> getPossibleMoves() {
			//System.out.println(origin);
			ArrayList<Integer> moves = new ArrayList<>();
			long mines = origin.getMinesBoard();
			long spacesClicked = origin.getClickBoard();
			long marks = origin.getMarks();
			
			for(int i = 0; i<64; i++) {
				if((spacesClicked & (1L << i)) == 0 && (mines & (1L << i)) == 0 
						&&(marks & (1L << i)) == 0) {
					moves.add(i);
				}
			}
			return moves;
		}
		
		/*
		 * Solves the Mines game in one bitwise operation. The current game state is set to the complement of the 
		 * long which contains the location of the mines.
		 */
		public void solveDirectly() {
			long answer = ~origin.getMinesBoard();
			origin.setClicks(answer);
		}
	} 
	ForkJoinPool pool;
	Minesweeper origin;
	final int finalDepth;
	ConcurrentHashMap<Integer, Minesweeper> solutions = new ConcurrentHashMap<>();
	GUI gui;
	
	/*
	 * Creates a new Solver with the a game state, maximum depth, starting depth, and an instance of the GUI for display purposes
	 */
	public Solver(Minesweeper sweep, int depth, int currentDepth, GUI gui) {
		pool = ForkJoinPool.commonPool();
		origin = sweep;
		finalDepth = depth;
		this.gui = gui;
	}
	
	
	/*
	 * Creates a new instance of a SplitterTask and runs it by putting it into a ForkJoinPool
	 */
	public void solve() {
		if(solutions.isEmpty()) {
			pool.invoke(new SplitterTask(origin, finalDepth, 0, solutions));
		}
	}
	
	/*
	 * Attempts to update the GUI with a new game state. This method looks through the ConcurrentHashTable which contains the 
	 * solution, finds the solution with the least number of clicked spaces. This method is called every time the start button 
	 * is pressed.
	 */
	public void sendSolutions() {
		Collection<Minesweeper> ss = solutions.values();
		Minesweeper current = origin;
		for(Minesweeper m: ss) {
			current = origin;
			if(Long.bitCount(m.getClickBoard())<Long.bitCount(current.getClickBoard())) {
				current = m;
			}
		}
		gui.setBoard(current);
		gui.updateGUI();
		
	}

	
}
