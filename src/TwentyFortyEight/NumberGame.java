package src.TwentyFortyEight;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/*************************************************************************
 * Number Game uses the interface Number Slider interface to create the 
 * logic behind the 2048 game. This is done by using a board of cells to
 * represent the game board. Then the user can slide the board in any
 * direction to combine numbers until they reach 2048.
 * 
 * @author Nathan VandenHoek
 * @version 2/16/2017
 ************************************************************************/
public class NumberGame implements NumberSlider {

	// The game board.
	private Cell[][] board;

	// All values within the current state of the board.
	private int[] allValues;

	// Multiple place-holders used in many methods.
	private int h, w, row, col, winningValue, length, fail, value;
	
	//The value of each cell entering the board.
	private double val;

	// Used to place random values on the game board.
	private Random random;

	// The current status of the game.
	private GameStatus status;

	// A stack of current states of the game board used in the undo method
	private Stack<ArrayList<Cell>> s;

	// A stack to keep track of the current locations and values of cells.
	private Stack<int[][]> location;

	/*********************************************************************
	 * Constructor will initialize the stacks.
	 ********************************************************************/
	public NumberGame() {
		location = new Stack<int[][]>();
		s = new Stack<ArrayList<Cell>>();
	}

	/*********************************************************************
	 * Reset the game to handle a board of a given dimension
	 *
	 * @param height
	 *            the number of rows in the board
	 * @param width
	 *            the number of columns in the board
	 * @param winningValue
	 *            the value that must appear on the board to win the game
	 * @throws IllegalArgumentException
	 *             when the winning value is not power of two, or negative
	 ********************************************************************/
	public void resizeBoard(int height, int width, int winningValue) {
		if (winningValue < 0 || winningValue % 2 != 0) {
			throw new IllegalArgumentException();
		}

		board = new Cell[height][width];
		this.winningValue = winningValue;
	}

	/*********************************************************************
	 * Remove all numbered tiles from the board and place two non-zero
	 * values at random location.
	 ********************************************************************/
	public void reset() {
		for (h = 0; h < this.board.length; h++) {
			for (w = 0; w < this.board[0].length; w++) {
				board[h][w] = null;
			}
		}

		this.placeRandomValue();
		this.placeRandomValue();
		this.s.clear();
		this.location.clear();
	}

	/*********************************************************************
	 * Set the game board to the desired values given in the 2D array.
	 * This method is mainly used by the JUnit tester.
	 * 
	 * @param ref
	 *            the values the board needs to be set to.
	 ********************************************************************/
	public void setValues(final int[][] ref) {
		if (ref.length * ref[0].length != board.length * board[0].length)
			throw new IllegalArgumentException();

		for (h = 0; h < board.length; h++) {
			for (w = 0; w < board[0].length; w++) {
				if (ref[h][w] == 0) {
					board[h][w] = null;
				} else if (board[h][w] == null) {
					board[h][w] = new Cell(h, w, ref[h][w]);
				} else {
					board[h][w].value = ref[h][w];
				}
			}
		}
	}

	/*********************************************************************
	 * This method will obtain the row, column, and values of
	 * a given cell, and sore it in an array.
	 * 
	 * @param help
	 * 				the current state of the board.
	 * @return an array of cell locations and values.
	 ********************************************************************/
	private int[][] getCellLocation(ArrayList<Cell> help) {
		int[][] location = new int[help.size()][3];
		int count = 0;

		for (Cell c : help) {
			location[count][0] = c.row;
			location[count][1] = c.column;
			location[count][2] = c.value;
			count++;
		}
		return location;
	}
	
	/*********************************************************************
	 * Slide all the tiles in the board in the requested direction
	 * 
	 * @param dir
	 *            the direction that the tiles need to be moved in
	 *
	 * @return true when the board changes
	 ********************************************************************/
	public boolean slide(SlideDirection dir) {
		boolean slide = false;
		int rowHelp = 0, colHelp = 0, count = 0;
		Cell[] help;
		s.push(this.getNonEmptyTiles());
		location.push(this.getCellLocation(s.peek()));

		switch (dir) {
		case RIGHT:
			fail = 0;

			for (rowHelp = 0; rowHelp < board.length; rowHelp++) {
				allValues = this.getValuesInRow(rowHelp);

				help = new Cell[allValues.length];
				count = 0;

				for (w = board[0].length - 1; w >= 0; w--) {
					if (board[rowHelp][w] != null) {
						help[count] = board[rowHelp][w];
						count++;
					}
				}

				help = this.calcValues(help);

				if (this.wasChange(help, SlideDirection.RIGHT)) {
					this.clearRow(rowHelp);
					count = 1;

					for (h = 0; h < help.length; h++) {
						if (help[h] != null) {
							board[rowHelp][board[0].length - count]
									= help[h];
							board[rowHelp][board[0].length - count].column
							= board[0].length - count;
							count++;
						}
					}
				} else {
					fail++;
				}
			}

			if (fail == board.length) {
				slide = false;
			} else {
				slide = true;
				this.placeRandomValue();
			}

			break;

		case LEFT:
			fail = 0;

			for (rowHelp = 0; rowHelp < board.length; rowHelp++) {
				allValues = this.getValuesInRow(rowHelp);

				help = new Cell[allValues.length];
				count = 0;

				for (w = 0; w < board[0].length; w++) {
					if (board[rowHelp][w] != null) {
						help[count] = board[rowHelp][w];
						count++;
					}
				}

				help = this.calcValues(help);

				if (this.wasChange(help, SlideDirection.LEFT)) {
					this.clearRow(rowHelp);
					count = 0;

					for (h = 0; h < help.length; h++) {
						if (help[h] != null) {
							board[rowHelp][count] = help[h];
							board[rowHelp][count].column = count;
							count++;
						}
					}
				} else {
					fail++;
				}
			}

			if (fail == board.length) {
				slide = false;
			} else {
				slide = true;
				this.placeRandomValue();
			}

			break;

		case UP:
			fail = 0;

			for (colHelp = 0; colHelp < board[0].length; colHelp++) {
				allValues = this.getValuesInCol(colHelp);

				help = new Cell[allValues.length];
				count = 0;

				for (w = 0; w < board.length; w++) {
					if (board[w][colHelp] != null) {
						help[count] = board[w][colHelp];
						count++;
					}
				}

				help = this.calcValues(help);

				if (this.wasChange(help, SlideDirection.UP)) {
					count = 0;
					this.clearCol(colHelp);

					for (h = 0; h < help.length; h++) {
						if (help[h] != null) {
							board[count][colHelp] = help[h];
							board[count][colHelp].row = count;
							count++;
						}
					}
				} else {
					fail++;
				}
			}

			if (fail == board[0].length) {
				slide = false;
			} else {
				slide = true;
				this.placeRandomValue();
			}

			break;
		case DOWN:
			fail = 0;

			for (colHelp = 0; colHelp < board[0].length; colHelp++) {
				allValues = this.getValuesInCol(colHelp);

				help = new Cell[allValues.length];
				count = 0;

				for (w = board.length - 1; w >= 0; w--) {
					if (board[w][colHelp] != null) {
						help[count] = board[w][colHelp];
						count++;
					}
				}

				help = this.calcValues(help);

				if (this.wasChange(help, SlideDirection.DOWN)) {
					this.clearCol(colHelp);
					count = 1;

					for (h = 0; h < help.length; h++) {
						if (help[h] != null) {
							board[board.length - count][colHelp]
									= help[h];
							board[board.length - count][colHelp].row
							= board.length - count;
							count++;
						}
					}
				} else {
					fail++;
				}
			}

			if (fail == board[0].length) {
				slide = false;
			} else {
				slide = true;
				this.placeRandomValue();
			}

			break;
		default:
			throw new IllegalArgumentException();
		}

		return slide;
	}

	/*********************************************************************
	 * This method will calculate all values that can be combined in a 
	 * given row or column.
	 * 
	 * @param help
	 * 				All cells in the given row or column.
	 * @return All values that can be combined.
	 ********************************************************************/
	private Cell[] calcValues(Cell[] help) {

		for (h = 0; h < help.length; h++) {
			if (help[h] != null)
				if (h + 1 == help.length)
					break;
			if (help[h].value * help[h + 1].value ==
					Math.pow(help[h].value, 2)) {
				help[h].value = help[h].value * 2;
				help[h + 1] = null;
				h++;
			}
		}
		return help;
	}

	/*********************************************************************
	 * Makes sure there is no possible movement along a certain row or 
	 * column for a given direction.
	 * 
	 * @param help
	 *            the current cells in a given row or column
	 * @param dir
	 *            the direction wished to be slid
	 * @return true if there is a possible change in the game board 
	 * available
	 ********************************************************************/
	public boolean wasChange(Cell[] help, SlideDirection dir) {
		boolean change = false;
		int count = 0;
		for (w = 0; w < help.length; w++) {
			if (help[w] == null) {
				change = true;
				return change;
			}
		}

		switch (dir) {
		case RIGHT:
			count = 1;
			for (Cell c : help) {
				if (c.column != board[0].length - count) {
					change = true;
					return change;
				}
				count++;
			}
			break;
		case LEFT:
			for (Cell c : help) {
				if (c.column != count) {
					change = true;
					return change;
				}
				count++;
			}
			break;
		case DOWN:
			count = 1;
			for (Cell c : help) {
				if (c.row != board.length - count) {
					change = true;
					return change;
				}
				count++;
			}
			break;
		case UP:
			for (Cell c : help) {
				if (c.row != count) {
					change = true;
					return change;
				}
				count++;
			}
			break;
		default:
		}

		return change;
	}

	/*********************************************************************
	 * Clears a given row by setting the board to null.
	 * 
	 * @param row
	 *            the given row wished to be cleared
	 ********************************************************************/
	private void clearRow(int row) {
		for (w = 0; w < board[0].length; w++) {
			board[row][w] = null;
		}
	}

	/*********************************************************************
	 * Clears a given column by setting the board to null.
	 * 
	 * @param col
	 *            the given column wished to be cleared.
	 ********************************************************************/
	private void clearCol(int col) {
		for (w = 0; w < board.length; w++) {
			board[w][col] = null;
		}
	}

	/*********************************************************************
	 * This method will obtain all the values within a column that is
	 * above zero, and return them.
	 * 
	 * @param col
	 *            the given column wished to be searched.
	 * @return all the values in the given column not equal to zero.
	 ********************************************************************/
	private int[] getValuesInCol(int col) {
		int[] help;
		length = 0;
		int count = 0;

		for (h = 0; h < board.length; h++) {
			if (board[h][col] != null)
				length++;
		}

		help = new int[length];

		for (h = 0; h < board.length; h++) {
			if (board[h][col] != null) {
				help[count] = board[h][col].value;
				count++;
			}
		}

		return help;
	}

	/*********************************************************************
	 * This method will obtain all the values within a row that is
	 * above zero and return them.
	 * 
	 * @param row
	 *            the given row wished to be searched.
	 * @return all the values in the given row not equal to zero.
	 ********************************************************************/
	private int[] getValuesInRow(int row) {
		int[] help;
		length = 0;
		int count = 0;

		for (w = 0; w < board[0].length; w++) {
			if (board[row][w] != null)
				length++;
		}

		help = new int[length];

		for (w = 0; w < board[0].length; w++) {
			if (board[row][w] != null) {
				help[count] = board[row][w].value;
				count++;
			}
		}

		return help;
	}

	/*********************************************************************
	 * Insert one random tile into an empty spot on the board.
	 *
	 * @return a Cell object with its row, column, and value attributes
	 *         initialized properly
	 *
	 * @throws IllegalStateException
	 *             when the board has no empty cell
	 ********************************************************************/
	public Cell placeRandomValue() {
		boolean emptyVal = false;
		random = new Random();

		if (this.isGameOver()) {
			throw new IllegalStateException();
		}

		while (!emptyVal) {
			this.row = random.nextInt(board.length);
			this.col = random.nextInt(board[0].length);
			this.val = random.nextDouble();

			if (board[row][col] == null)
				emptyVal = true;
		}

		if(val <= .8){
			this.value = 2;
		}else{
			this.value = 4;
		}
		
		Cell cell1 = new Cell(row, col, value);
		board[row][col] = cell1;
		return cell1;
	}

	/*********************************************************************
	 * Gather all tiles with a cell, and add them to the list.
	 * 
	 * @return an Array list of Cells. Each cell holds the (row,column)
	 * and value of a tile
	 ********************************************************************/
	public ArrayList<Cell> getNonEmptyTiles() {
		ArrayList<Cell> cellList = new ArrayList<Cell>();

		for (h = 0; h < this.board.length; h++) {
			for (w = 0; w < this.board[0].length; w++) {
				if (this.board[h][w] != null) {
					cellList.add(board[h][w]);
				}
			}
		}

		return cellList;
	}

	/*********************************************************************
	 * Return the current state of the game
	 * 
	 * @return one of the possible values of GameStatus enumeration
	 ********************************************************************/
	public GameStatus getStatus() {
		for (Cell c : this.getNonEmptyTiles()) {
			if (c.value >= this.winningValue) {
				status = GameStatus.USER_WON;
				return status;
			}
		}

		if (this.isGameOver()) {
			status = GameStatus.USER_LOST;
			return status;
		}

		status = GameStatus.IN_PROGRESS;
		return status;
	}

	/*********************************************************************
	 * This method will check to see if there is a move left for the user.
	 * If not, it will return true.
	 * 
	 * @return true if no moves are possible.
	 ********************************************************************/
	private boolean isGameOver() {
		int[] help;

		if (this.getNonEmptyTiles().size() < 
				board.length * board[0].length) {
			return false;
		}

		for (h = 0; h < board.length; h++) {
			help = this.getValuesInRow(h);

			for (w = 0; w < help.length; w++) {
				if (w + 1 == help.length)
					break;
				if (help[w] * help[w + 1] == Math.pow(help[w], 2)) {
					return false;
				}
			}
		}

		for (w = 0; w < board[0].length; w++) {
			help = this.getValuesInCol(w);

			for (h = 0; h < help.length; h++) {
				if (h + 1 == help.length)
					break;
				if (help[h] * help[h + 1] == Math.pow(help[h], 2)) {
					return false;
				}
			}
		}

		return true;
	}

	/*********************************************************************
	 * Undo the most recent action, i.e. restore the board to its previous
	 * state. Calling this method multiple times will ultimately restore 
	 * the game to the very first initial state of the board holding two 
	 * random values. Further attempt to undo beyond this state will
	 * throw an IllegalStateException.
	 *
	 * @throws IllegalStateException
	 *             when undo is not possible
	 ********************************************************************/
	public void undo() {
		if (s.isEmpty()) {
			throw new IllegalStateException();
		}

		int[][] locations = this.location.peek();
		int count = 0;

		for (h = 0; h < board.length; h++) {
			this.clearRow(h);
		}

		for (Cell c : s.peek()) {
			c.row = locations[count][0];
			c.column = locations[count][1];
			c.value = locations[count][2];
			board[c.row][c.column] = c;
			count++;
		}

		this.s.pop();
		this.location.pop();
	}
}
