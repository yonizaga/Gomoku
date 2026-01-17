package com.example.largetictac;

import java.util.ArrayList;
import java.util.Random;

public class Model {
    public static final int EMPTY = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = -1;

    private int[][] board;
    private int currentPlayer;

    /**
     * Input: None
     * Output: A new Model instance
     * Logic: Initializes the game board as a 15x15 grid and sets the starting player to PLAYER_X.
     */
    public Model() {
        board = new int[15][15];
        currentPlayer = PLAYER_X;
    }

    /**
     * Input: None
     * Output: int - The ID of the current player (1 for X, -1 for O).
     * Logic: Returns the value of the private variable tracking whose turn it is.
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Input: row (int), col (int) - The coordinates to check.
     * Output: boolean - True if the cell is empty, False otherwise.
     * Logic: Checks if the board array at the specified indices contains the EMPTY constant.
     */
    public boolean isLegal(int row, int col) {
        return board[row][col] == EMPTY;
    }

    /**
     * Input: row (int), col (int) - The coordinates where the player wants to move.
     * Output: boolean - True if the move was successful, False if invalid.
     * Logic: First validates the move using isLegal(). If valid, updates the board array with the current player's ID.
     */
    public boolean makeMove(int row, int col) {
        if (!isLegal(row, col)) return false;
        board[row][col] = currentPlayer;
        return true;
    }

    /**
     * Input: None
     * Output: None
     * Logic: Multiplies the currentPlayer variable by -1 to switch between PLAYER_X (1) and PLAYER_O (-1).
     */
    public void changePlayer() {
        currentPlayer *= -1;
    }

    /**
     * Input: None
     * Output: boolean - True if the game is a tie, False otherwise.
     * Logic: Iterates through the entire 15x15 board. If it finds any EMPTY cell, it returns false (game continues).
     * If the loop finishes without finding empty cells, it returns true.
     */
    public boolean isTie() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == EMPTY) return false;
            }
        }
        return true;
    }

    /**
     * Input: None
     * Output: None
     * Logic: Loops through the entire board and sets every cell to EMPTY. Resets current player to X.
     */
    public void resetGame() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = EMPTY;
            }
        }
        currentPlayer = PLAYER_X;
    }

    // ==================================================================================
    //                              WIN CHECKING LOGIC
    // ==================================================================================

    /**
     * Input: row (int), col (int) - The position of the last placed stone.
     * Output: int - The ID of the winning player, or EMPTY if no winner found.
     * Logic: Checks all 4 directions (Vertical, Horizontal, Diagonal, Anti-Diagonal) from the given point.
     * If any direction has 5 or more consecutive stones of the same player, that player is returned as the winner.
     */
    public int checkWin(int row, int col) {
        int player = board[row][col];
        if (player == EMPTY) return EMPTY;

        // Check Vertical (1, 0)
        if (countConsecutiveStones(row, col, 1, 0, player) >= 5) return player;

        // Check Horizontal (0, 1)
        if (countConsecutiveStones(row, col, 0, 1, player) >= 5) return player;

        // Check Diagonal ↘ (1, 1)
        if (countConsecutiveStones(row, col, 1, 1, player) >= 5) return player;

        // Check Anti-Diagonal ↙ (1, -1)
        if (countConsecutiveStones(row, col, 1, -1, player) >= 5) return player;

        return EMPTY;
    }

    /**
     * Input: row, col (int) - start point; dr, dc (int) - direction deltas; player (int) - ID to count.
     * Output: int - The number of consecutive stones.
     * Logic: Scans strictly forward and backward along the defined vector (dr, dc) counting matching stones.
     * Stops immediately upon hitting a border or a different stone. Does not count empty spaces.
     */
    private int countConsecutiveStones(int row, int col, int dr, int dc, int player) {
        int count = 1; // Start with the stone at (row, col)

        // 1. Count forward
        int r = row + dr;
        int c = col + dc;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }

        // 2. Count backward
        r = row - dr;
        c = col - dc;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == player) {
            count++;
            r -= dr;
            c -= dc;
        }

        return count;
    }

    // ==================================================================================
    //                              AI & HEURISTIC HELPERS
    // ==================================================================================

    /**
     * Input: row, col (int) - start point; targetLength (int) - length to find.
     * Output: boolean - True if a line of that length exists AND is viable (not blocked).
     * Logic: Checks all 4 directions using getViableStoneCount. Unlike checkWin, this ensures the line
     * has enough potential empty space to eventually become a winning line of 5.
     */
    public boolean isLineOfLength(int row, int col, int targetLength) {
        int player = board[row][col];
        if (player == EMPTY) return false;

        if (getViableStoneCount(row, col, 1, 0, player) >= targetLength) return true;
        if (getViableStoneCount(row, col, 0, 1, player) >= targetLength) return true;
        if (getViableStoneCount(row, col, 1, 1, player) >= targetLength) return true;
        if (getViableStoneCount(row, col, 1, -1, player) >= targetLength) return true;

        return false;
    }

    /**
     * Input: row, col, dr, dc, player.
     * Output: int - The count of stones if the line is viable, or 0 if blocked.
     * Logic: Counts connected stones forward/backward. Then counts adjacent empty spaces forward/backward.
     * If (stones + empty_spaces) < 5, the line is "dead" and returns 0. Otherwise returns the stone count.
     */
    private int getViableStoneCount(int row, int col, int dr, int dc, int player) {
        int stones = 1;

        // 1. Count stones forward
        int r = row + dr;
        int c = col + dc;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == player) {
            stones++;
            r += dr;
            c += dc;
        }
        int fR = r, fC = c;

        // 2. Count stones backward
        r = row - dr;
        c = col - dc;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == player) {
            stones++;
            r -= dr;
            c -= dc;
        }
        int bR = r, bC = c;

        // 3. Count empty spaces (Potential)
        int potential = stones;

        // Forward potential
        r = fR; c = fC;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == EMPTY) {
            potential++;
            r += dr;
            c += dc;
        }

        // Backward potential
        r = bR; c = bC;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == EMPTY) {
            potential++;
            r -= dr;
            c -= dc;
        }

        // If total room is less than 5, this line is useless.
        if (potential < 5) return 0;

        return stones;
    }

    /**
     * Input: row, col (location of a stone), player (owner of stone).
     * Output: int - A heuristic score.
     * Logic: Looks at a specific stone ALREADY on the board. For every viable line passing through it,
     * adds 2^(stone_count) to the total score. This rewards having multiple intersecting lines.
     */
    private int getScoreCurrentState(int row, int col, int player) {
        int totalValue = 0;
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};

        for (int[] dir : directions) {
            int count = getViableStoneCount(row, col, dir[0], dir[1], player);
            if (count > 0) {
                // 1 << count is bitwise for 2^count
                totalValue += (1 << count);
            }
        }
        return totalValue;
    }
    /**
     * Scans the whole board to calculate the advantage.
     * Positive = AI is winning. Negative = Human is winning.
     */
    private double evaluateBoardState(int aiPlayer) {
        double aiScore = 0;
        double humanScore = 0;
        int humanPlayer = aiPlayer * -1; // The opposite of AI

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == aiPlayer) {
                    aiScore += getScoreCurrentState(i, j, aiPlayer);
                } else if (board[i][j] == humanPlayer) {
                    humanScore += getScoreCurrentState(i, j, humanPlayer);
                }
            }
        }
        // Return the difference. If AI has more threats, this is positive.
        return aiScore - humanScore;
    }

    /**
     * The Minimax Algorithm:
     * Checks future moves assuming the opponent plays perfectly.
     * * depth: How many moves ahead to look (e.g., 2 or 3)
     * isMaximizing: true if it's AI's turn, false if it's Opponent's turn
     * alpha, beta: Used for "Pruning" (optimizing speed)
     */
    private double minimax(int depth, boolean isMaximizing, double alpha, double beta, int aiPlayer) {
        // 1. Check for Terminal States (Game Over)
        // We need to know who just moved to check if they won.
        // If isMaximizing is true, it means the Opponent (Human) just moved.
        int humanPlayer = aiPlayer * -1;

        // Check if the previous move created a win
        // Note: This is a simplification. Ideally, we pass the last move coordinates to checkWin
        // effectively, but for now we rely on the board state.

        // If depth is 0, we stop and evaluate the board.
        if (depth == 0) {
            return evaluateBoardState(aiPlayer);
        }

        ArrayList<Move> moves = getPossibleAdjacentMoves();

        // If no moves left, it's a draw
        if (moves.isEmpty()) return 0;

        if (isMaximizing) {
            // AI's Turn (Try to get the highest score)
            double maxEval = -10000000.0; // Start very low

            for (Move move : moves) {
                board[move.row][move.col] = aiPlayer; // Make move

                // If this move wins immediately, take it!
                if (checkWin(move.row, move.col) == aiPlayer) {
                    board[move.row][move.col] = EMPTY;
                    return 1000000.0 + depth; // Prefer winning sooner
                }

                double eval = minimax(depth - 1, false, alpha, beta, aiPlayer);

                board[move.row][move.col] = EMPTY; // Undo move

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Pruning (Stop checking bad branches)
            }
            return maxEval;

        } else {
            // Human's Turn (Human tries to give AI the lowest score)
            double minEval = 10000000.0; // Start very high

            for (Move move : moves) {
                board[move.row][move.col] = humanPlayer; // Make move

                // If Human wins here, that's terrible for AI (-1,000,000)
                if (checkWin(move.row, move.col) == humanPlayer) {
                    board[move.row][move.col] = EMPTY;
                    return -1000000.0 - depth;
                }

                double eval = minimax(depth - 1, true, alpha, beta, aiPlayer);

                board[move.row][move.col] = EMPTY; // Undo move

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Pruning
            }
            return minEval;
        }
    }

    /**
     * Input: aiPlayer (int), humanPlayer (int) - IDs of the bot and the opponent.
     * Output: Move - The best calculated coordinates for the AI to play.
     * Logic: Generates all "adjacent" moves (moves near existing stones). For each candidate, calculates:
     * 1. Attack Score (How good is this move for AI?)
     * 2. Defense Score (How good is this move for Human? i.e., blocking)
     * Sums these scores and picks the move with the highest total.
     */
    public Move getHeuristicMove(int aiPlayer, int humanPlayer) {
        ArrayList<Move> moves = getPossibleAdjacentMoves();

        // Fallback for empty board
        if (moves.isEmpty()) return new Move(7, 7);

        Move bestMove = null;
        double bestValue = -100000000.0; // Very low number

        // Loop through all immediate moves for the AI
        for (Move move : moves) {
            board[move.row][move.col] = aiPlayer;

            // Immediate win check (Critical!)
            if (checkWin(move.row, move.col) == aiPlayer) {
                board[move.row][move.col] = EMPTY;
                return move;
            }

            // Call Minimax with depth 2 (Look ahead: Human move -> AI move)
            // isMaximizing is FALSE because the next turn is Human's
            double moveValue = minimax(2, false, -100000000.0, 100000000.0, aiPlayer);

            board[move.row][move.col] = EMPTY; // Undo

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }

        return (bestMove != null) ? bestMove : moves.get(0);
    }

    /**
     * Input: None
     * Output: ArrayList<Move> - List of all empty slots on the board.
     * Logic: Scans the full board and adds every EMPTY cell to a list. Used mostly for random fallback.
     */
    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == EMPTY) {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    }

    /**
     * Input: row, col (int)
     * Output: int - count of non-empty neighbors.
     * Logic: Checks the 8 surrounding cells of a coordinate (ignoring bounds issues) and counts how many contain stones.
     */
    public int getNumOfAdjacentSlots(int row, int col) {
        int sum = 0;
        for (int R = row - 1; R <= row + 1; R++) {
            for (int C = col - 1; C <= col + 1; C++) {
                if (R < 0 || R > 14 || C < 0 || C > 14) continue;
                if (R == row && C == col) continue;
                if (board[R][C] != EMPTY) sum++;
            }
        }
        return sum;
    }

    /**
     * Input: None
     * Output: ArrayList<Move> - List of empty slots that are touching at least one existing stone.
     * Logic: Optimizes search space by only returning empty cells that are adjacent to existing stones.
     * This prevents the AI from checking useless moves in the corners of the board.
     */
    public ArrayList<Move> getPossibleAdjacentMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == EMPTY && getNumOfAdjacentSlots(i, j) > 0) {
                    moves.add(new Move(i, j));
                }
            }
        }

        // If the board is completely empty (start of game), this list is empty.
        // We must return the center move or all moves to prevent a crash.
        if (moves.isEmpty()) {
            return getPossibleMoves();
        }

        return moves;
    }
}