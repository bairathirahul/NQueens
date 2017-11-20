import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class NQMinConflicts {
    /*
        Count of random restarts
     */
    private static int randomRestarts = 0;

    /*
        Counts of State Changes in current restart
     */
    private static int stateChanges = 0;

    /**
     * Read input from user and execute the Steepest-Ascent hill climbing
     *
     * @param args Command Line parameters (not required)
     */
    public static void main(String[] args) {
        int n = 0;
        /*
            Read size of chess board. The valid values are either 1 or greater than 4
            For chess boards of size 2 and 3, there are no solutions.
            The code will loop until a valid input is provided
         */
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Please enter the size of board (1 or at least 4): ");
            n = scanner.nextInt();
            if (n < 4 && n != 1) {
                System.out.println("The N-Queens problem has no solution for a board of size " + n + ". Please try again.");
            } else {
                break;
            }
        }

        /*
            Execute min-conflicts with random restart to solve n-Queens problem
         */
        long time = System.currentTimeMillis();
        while (true) {
            // Create a random initial node
            ChessBoard initialNode = new ChessBoard(n);

            // Execute the algorithm
            ChessBoard resultNode = minConflict(n, initialNode);

            // Result is found, display result
            if (resultNode != null) {
                output(n, resultNode, System.currentTimeMillis() - time);
                break;
            }

            // Increment random restarts count
            randomRestarts++;

            // Re-initialize state changes
            stateChanges = 0;
        }
    }

    /**
     * Execute Min-conflict algorithm for N-Queens problem
     *
     * @param n Size of chess board
     * @param currentNode Initial Node
     * @return Modified initial node if result found or null if stuck
     */
    public static ChessBoard minConflict(int n, ChessBoard currentNode) {
        // Current node is the goal, return
        if (currentNode.conflicts == 0) {
            return currentNode;
        }

        // Max number of steps is taken as square of n
        int steps = n * n;
        Random random = new Random();

        // Loop for each step
        while (steps > 0) {
            int col = 0;

            // Choose a column randomly that has conflicts
            if(currentNode.conflictingCols.size() > 0) {
                int randomIndex = random.nextInt(currentNode.conflictingCols.size());
                col = currentNode.conflictingCols.get(randomIndex);
            }

            // Try to reassign
            if(currentNode.reassign(col)) {
                // If reassigned, increment the state change
                stateChanges++;

                // If the resultant node is target, return
                if(currentNode.conflicts == 0) {
                    return currentNode;
                }
            }
            steps--;
        }

        // Return null, if no result is found
        return null;
    }

    /**
     * Display the solution of execution
     *
     * @param n Size of chessboard
     * @param resultNode Result node
     * @param time Time of execution
     */
    public static void output(int n, ChessBoard resultNode, long time) {
        System.out.println("\n" + n + "-Queens Solution (with Min Conflict CSP)");
        System.out.println("Execution Time: " + time + " milliseconds");
        System.out.println("Number of Restarts: " + randomRestarts);
        System.out.println("Number of State Changes: " + stateChanges);

        System.out.println("\nLinear Representation of the Solution: ");
        for (int i = 0; i < n; i++) {
            System.out.print("+---");
        }
        System.out.println("+");
        for (int i = 0; i < n; i++) {
            System.out.format("|%3s", resultNode.board[i] + 1);
        }
        System.out.println("|");
        for (int i = 0; i < n; i++) {
            System.out.print("+---");
        }
        System.out.println("+");

        System.out.println("\nGrid Representation of the Solution: ");
        int[] transposedBoard = new int[n];
        for(int i = 0; i < n; i++) {
            transposedBoard[resultNode.board[i]] = i;
        }
        for(int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print("+---");
            }
            System.out.println("+");

            for (int j = 0; j < n; j++) {
                if(transposedBoard[i] == j) {
                    System.out.print("| o ");
                } else {
                    System.out.print("|   ");
                }
            }
            System.out.println("|");
        }
        for (int j = 0; j < n; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    /**
     * ChessBoard class indicates a node
     */
    private static class ChessBoard implements Cloneable {
        // Size of the chess board
        private int n;

        /*
            Position of queens in the chessboard
            board[i] indicates row number of queen in column i
         */
        public int[] board;

        // Conflicts in this chessboard
        public int conflicts = 0;

        // Conflits of each column
        public int[] colConflicts;

        // List of conflicting columns
        public List<Integer>conflictingCols;

        /**
         * Initializes the chessboard with random queen positions
         *
         * @param n Size of the chessboard
         */
        public ChessBoard(int n) {
            this.n = n;
            this.board = new int[n];
            this.colConflicts = new int[n];
            this.conflictingCols = new ArrayList<Integer>(n);

            // Generate random positions for queens
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                this.board[i] = random.nextInt(n);
            }
            this.conflicts();
        }

        /**
         * Calculate conflicts in current chessboard.
         * A pair of queens conflict if either they are in the same row, or same diagonal
         */
        private void conflicts() {
            // Reinitialize number of conflicts
            this.conflicts = 0;

            // Calculate total conflicts for entire board
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    // Check if queens at columns i & j are in conflict
                    if (this.board[i] == this.board[j] || (i - this.board[i]) == (j - this.board[j]) || (i + this.board[i]) == (j + this.board[j])) {
                        conflicts++;
                    }
                }
            }

            // Calculate conflicts for individual columns
            this.conflictingCols.clear();
            for(int i = 0; i < n; i++) {
                this.colConflicts[i] = this.cellConflicts(this.board[i], i);
                // Add conflicting columns to the list
                if(this.colConflicts[i] > 0) {
                    this.conflictingCols.add(i);
                }
            }
        }

        /**
         * Number of directions in which conflict exists for the given cell
         *
         * @param row Row number of the cell
         * @param col Column number of the cell
         * @return Number of directions with conflict
         */
        private int cellConflicts(int row, int col) {
            /*
                There are 6 possible directions. The last 6 bits of integer directions
                is used to keep track of directions in which conflict exists
             */
            int directions = 0;

            for(int i = 0; i < col; i++) {
                if ((row - col) == (this.board[i] - i)) { // Northwest conflict
                    directions = directions | 1;
                } else if (this.board[i] == row) { // West conflict
                    directions = directions | 2;
                } else if ((row + col) == (this.board[i] + i)) { // Southwest
                    directions = directions | 4;
                }
            }

            for(int i = col + 1; i < n; i++) {
                if ((row - col) == (this.board[i] - i)) { // Southeast conflict
                    directions = directions | 8;
                } else if (this.board[i] == row) { // East conflict
                    directions = directions | 16;
                } else if ((row + col) == (this.board[i] + i)) { // Northeast conflict
                    directions = directions | 32;
                }
            }

            // Count number of set bits using Brian Kernighan’s Algorithm
            int conflicts = 0;
            while(directions != 0) {
                directions &= directions - 1;
                conflicts++;
            }
            return conflicts;
        }

        /**
         * Reassign the queen at the given column, if there is a better place
         *
         * @param col Column for reassignment
         * @return true if reassignment is done, otherwise false
         */
        public boolean reassign(int col) {
            // Get current rows conflict
            int minConflict = this.colConflicts[col];

            // Determine a row with lesser conflicts
            int selectedRow = -1;
            for(int i = 0; i < n; i++) {
                if(i == this.board[col]) {
                    continue;
                }

                int rowConflict = this.cellConflicts(i, col);
                if(rowConflict < minConflict) {
                    selectedRow = i;
                    // If conflict is zero, simply select the row
                    if(rowConflict == 0) {
                        break;
                    }
                }
            }

            if(selectedRow != -1) {
                // Assign selected row
                this.board[col] = selectedRow;
                // Recompute conflicts
                this.conflicts();
                return true;
            }

            // Return false if no reassignment done
            return false;
        }

        /**
         * Display the chess board as string
         * @return String output
         */
        @Override
        public String toString() {
            return Arrays.toString(this.board);
        }
    }
}