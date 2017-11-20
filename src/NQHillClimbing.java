import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class NQHillClimbing {
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
                System.out.println("The N-Queens problem has no solution for a board of size "
                        + n + ". Please try again.");
            } else {
                break;
            }
        }

        /*
            Execute Steepest Ascent Hill Climbing algorithm on n-Queens problem
         */
        long time = System.currentTimeMillis();
        while (true) {
            // Create a random initial node
            ChessBoard initialNode = new ChessBoard(n);

            // Execute the algorithm
            ChessBoard resultNode = hillClimbing(initialNode);

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
     * Execute Steepest Ascent Hill Climbing Algorithm
     *
     * @param currentNode Initial Node
     * @return Result node if found or null if stuck
     */
    public static ChessBoard hillClimbing(ChessBoard currentNode) {
        // Current node is the goal, return
        if (currentNode.conflicts == 0) {
            return currentNode;
        }

        while (true) {
            // Increment state changes count
            stateChanges++;

            // Generate neighbor node with lowest conflict
            ChessBoard neighborNode = currentNode.bestNeighbor();

            /*
                If no neighbor node is found, return null (neighborNode variable)
                otherwise return neighbor node itself, if the conflict is 0
             */
            if (neighborNode == null || neighborNode.conflicts == 0) {
                return neighborNode;
            }

            // Next iteration with neighborNode as currentNode
            currentNode = neighborNode;
        }
    }

    /**
     * Display the solution of execution
     *
     * @param n Size of chessboard
     * @param resultNode Result node
     * @param time Time of execution
     */
    public static void output(int n, ChessBoard resultNode, long time) {
        System.out.println("\n" + n + "-Queens Solution (with Hill Climbing)");
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

        /**
         * Copy constructor to create a clone of ChessBoard
         *
         * @param original Original ChessBoard instance
         */
        private ChessBoard(ChessBoard original) {
            this.n = original.n;
            this.board = original.board.clone();
            this.conflicts = original.conflicts;
        }

        /**
         * Initializes the chessboard with random queen positions
         *
         * @param n Size of the chessboard
         */
        public ChessBoard(int n) {
            this.n = n;
            this.board = new int[n];

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

            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    // Check if queens at columns i & j are in conflict
                    if (this.board[i] == this.board[j] || (i - this.board[i]) == (j - this.board[j])
                            || (i + this.board[i]) == (j + this.board[j])) {
                        conflicts++;
                    }
                }
            }
        }

        /**
         * Generate highest value neighbor of current node
         *
         * @return Chessboard instance with lowest number of conflicts
         * or null if no such chessboard found
         */
        private ChessBoard bestNeighbor() {
            // Create a clone of current chessboard
            ChessBoard neighbor = this.clone();
            /*
                Initialize minimum conflict with current number of conflicts
                as we need the next state to have lesser number of conflicts
             */
            int minConflict = this.conflicts;
            // Selected chess board
            ChessBoard selected = null;

            // Permute with different positions of queens in every column
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Skip position same as current
                    if (j == this.board[i]) {
                        continue;
                    }

                    neighbor.board[i] = j;
                    // Calculate conflicts for generated neighbor
                    neighbor.conflicts();

                    if (neighbor.conflicts < minConflict) {
                        // Select the neighbor with minimum number of conflicts
                        selected = neighbor.clone();
                    } else {
                        // Revert the position of queen
                        neighbor.board[i] = this.board[i];
                    }
                }
            }

            return selected;
        }

        @Override
        /**
         * Create deep copy of current Chess Board
         */
        public ChessBoard clone() {
            return new ChessBoard(this);
        }

        @Override
        /**
         * Display the chess board as string
         *
         * @return String output
         */
        public String toString() {
            return Arrays.toString(this.board);
        }
    }
}