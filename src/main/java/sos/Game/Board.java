package sos.game;

public class Board {

    private final Cell[][] grid;
    private int filled = 0;

    public Board(int size) {
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        this.grid = new Cell[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = Cell.EMPTY;
            }
        }
    }

    public int size() { return grid.length; }

    public Cell get(int r, int c) {
        check(r, c);
        return grid[r][c];
    }

    // preventing the double coutign issue
    private static final int[][] Direction = {
        {0, 1}, {1, 0}, {1, 1}, {1, -1}
    };

    //how many sos formed by placing a s
    public int place(Cell letter, int r, int c) {
        check(r, c);
        if (grid[r][c] != Cell.EMPTY) {
            throw new IllegalStateException("Cell already filled at (" + r + "," + c + ")");
        }

        grid[r][c] = letter;
        filled++;

        int made = 0;

        if (letter == Cell.O) {
            //logic might be off need to test
            for (int[] d : Direction) {
                int r1 = r - d[0], c1 = c - d[1];
                int r2 = r + d[0], c2 = c + d[1];
                if (in(r1, c1) && in(r2, c2)
                        && grid[r1][c1] == Cell.S
                        && grid[r2][c2] == Cell.S) {
                    made++;
                }
            }
        } else if (letter == Cell.S) {
            // S can be START or END 
            for (int[] d : Direction) {
                int rO = r + d[0],   cO = c + d[1];
                int rS = r + 2*d[0], cS = c + 2*d[1];
                if (in(rO, cO) && in(rS, cS)
                        && grid[rO][cO] == Cell.O
                        && grid[rS][cS] == Cell.S) {
                    made++;
                }
                int rS2 = r - 2*d[0], cS2 = c - 2*d[1];
                int rO2 = r - d[0],   cO2 = c - d[1];
                if (in(rO2, cO2) && in(rS2, cS2)
                        && grid[rO2][cO2] == Cell.O
                        && grid[rS2][cS2] == Cell.S) {
                    made++;
                }
            }
        }
        return made;
    }

    public boolean isFull() { return filled == size() * size(); }

    private boolean in(int r, int c) {
        return r >= 0 && r < size() && c >= 0 && c < size();
    }

    private void check(int r, int c) {
        int n = size();
        if (r < 0 || r >= n || c < 0 || c >= n) {
            throw new IndexOutOfBoundsException("(" + r + "," + c + ") outside " + n + "×" + n);
        }
    }
}
