package cs310.games;

// It's OK to put Object methods in an interface
// See JDK Set.java at http://hg.openjdk.java.net/jdk8u/jdk8u/jdk/file/0eb62e4a75e6/src/share/classes/java/util/Set.java
// It has equals and hashCode, to document their behavior
public interface Game {
	// These constants describe values for the side parameter
	// and the meaning of the winner return value
	 static final int HUMAN = 0;
	 static final int COMPUTER = 1;
	 static final int GAME_NOT_DONE = 2;
	 static final int DRAWN_GAME = 3;  // can't use DRAW, it's value is 1 in TicTacToe

	/**
	 * Set up the position ready to play.
	 */
	void init();
	
	/**
	 * Has side won the game? 
	 * Alternate name: isAWin
	 * 
	 * @return true if side is the winner.
	 */
	boolean isWinner(int side);
	
	/**
	 * Has the game finished as a draw? 
	 * Alternate name: isADraw
	 * 
	 * @return true if a draw
	 */
	boolean isDraw();
	
	/**
	 * Make a move
	 * Alternate name: playMove
	 * 
	 * @param side of player making move
	 * @param row 
	 * @param number of stars taken, or number of column.
	 * @returns false if move is illegal.
	 */
	public boolean makeMove(int side, int row, int number);
	
	/**
	 * Find the best move by backtracking
	 * 
	 * @param side player making move
	 */
	public BestMove chooseMove(int side, int depth);
	
	/**
	 * This method displays current position (game state)
	 */
	public void printBoard();
}
