package sos.game;

// Manages the game state and logic
// including the board, player turns, and move validation.
//AI helped convert my code to this structure I verified it works with the UI code

public class GameManager {
    private final int size;
    private final String mode;
    private final String[][] grid;
    private boolean isPlayerOneTurn = true;

    public GameManager(int size, String mode) {
        this.size = size;
        this.mode = mode;
        this.grid = new String[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = "";
            }
        }
    }

    public int getSize() { return size; }
    public String getMode() { return mode; }
    public boolean isPlayerOneTurn() { return isPlayerOneTurn; }
    public String getCell(int r, int c) { return grid[r][c]; }


    public boolean placeMove(int r, int c) {
        if (!grid[r][c].isEmpty()) return false;
        grid[r][c] = isPlayerOneTurn ? "S" : "O";  
        isPlayerOneTurn = !isPlayerOneTurn;
        return true;
    }

    public void reset() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                grid[r][c] = "";
        isPlayerOneTurn = true;
    }
}
