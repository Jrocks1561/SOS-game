package sos.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class HardComputerPlayer extends ComputerPlayer {

    @Override
    public int[] chooseMove(Board board, int size, int currentPlayerIndex) {

        // Player 1 is S, Player 2 is O
        boolean isSPlayer = (currentPlayerIndex == 0);

        // Try the smarter hard-level move first
        if (isSPlayer) {

            // S tries to finish patterns like S-O-_ or _-O-S
            int[] smartSMove = findBestMoveForS(board, size);
            if (smartSMove != null) return smartSMove;
        }

        else {

            // O tries to score, block, or stop forks if possible
            int[] smartOMove = findBestMoveForO(board, size);
            if (smartOMove != null) return smartOMove;
        }

        // No immediate scoring or blocking logic found
        List<int[]> candidates = new ArrayList<>();

        if (isSPlayer) {

            // S still tries to play next to any O
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {

                    if (board.get(row, col) == Cell.O) {
                        int[] pos = findEmptyNeighbor(board, size, row, col, false);
                        if (pos != null) candidates.add(pos);
                    }
                }
            }
        }

        else {

            // O tries to avoid S unless it has to not avoid it
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {

                    if (isEmpty(board.get(row, col)) && !isNearS(board, size, row, col)) {
                        candidates.add(new int[]{row, col});
                    }
                }
            }
        }

        // Pick something from fallback options
        if (!candidates.isEmpty()) {
            int idx = ThreadLocalRandom.current().nextInt(candidates.size());
            return candidates.get(idx);
        }

        // If no other options random empty cell
        List<int[]> empties = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (isEmpty(board.get(row, col))) {
                    empties.add(new int[]{row, col});
                }
            }
        }

        if (!empties.isEmpty()) {
            int randomIdx = ThreadLocalRandom.current().nextInt(empties.size());
            return empties.get(randomIdx);
        }

        return null;
    }

    // S tries to complete S-O-S type patterns
    private int[] findBestMoveForS(Board board, int size) {

        int[][] directions = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1},
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}
        };

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (board.get(row, col) != Cell.O) continue;

                for (int[] direction : directions) {
                    int dRow = direction[0];
                    int dCol = direction[1];

                    int row1 = row + dRow;
                    int col1 = col + dCol;

                    int row2 = row - dRow;
                    int col2 = col - dCol;

                    boolean in1 = inBounds(size, row1, col1);
                    boolean in2 = inBounds(size, row2, col2);

                    // S O _
                    if (in1 && in2 &&
                        board.get(row1, col1) == Cell.S &&
                        isEmpty(board.get(row2, col2))) {

                        return new int[]{row2, col2};
                    }

                    // _ O S
                    if (in1 && in2 &&
                        isEmpty(board.get(row1, col1)) &&
                        board.get(row2, col2) == Cell.S) {

                        return new int[]{row1, col1};
                    }
                }
            }
        }

        return null;
    }

    // O tries to score S-O-S, block S from finishing SO_ or _OS, and stop forks
    private int[] findBestMoveForO(Board board, int size) {

        int[][] directions = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1},
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}
        };

        // Try to score O by making S-O-S (O in the middle)
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                // middle must be empty
                if (!isEmpty(board.get(row, col))) continue;

                for (int[] direction : directions) {
                    int dRow = direction[0];
                    int dCol = direction[1];

                    int row1 = row + dRow;
                    int col1 = col + dCol;

                    int row2 = row - dRow;
                    int col2 = col - dCol;

                    if (!inBounds(size, row1, col1) || !inBounds(size, row2, col2))
                        continue;

                    // S _ S → drop O in the middle
                    if (board.get(row1, col1) == Cell.S &&
                        board.get(row2, col2) == Cell.S) {

                        return new int[]{row, col};
                    }
                }
            }
        }

        // If O can't score, try to block S-O-_ or _-O-S by filling the scoring space
        // We scan all empty cells and see if they complete SO_ or _OS patterns
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (!isEmpty(board.get(row, col))) continue;

                for (int[] direction : directions) {
                    int dRow = direction[0];
                    int dCol = direction[1];

                    // for pattern: S O _ where row,col is _
                    int oRow1 = row - dRow;
                    int oCol1 = col - dCol;
                    int sRow1 = row - 2 * dRow;
                    int sCol1 = col - 2 * dCol;

                    if (inBounds(size, oRow1, oCol1) && inBounds(size, sRow1, sCol1)) {
                        if (board.get(oRow1, oCol1) == Cell.O &&
                            board.get(sRow1, sCol1) == Cell.S) {

                            return new int[]{row, col};
                        }
                    }

                    // for pattern: _ O S where row,col is _
                    int oRow2 = row + dRow;
                    int oCol2 = col + dCol;
                    int sRow2 = row + 2 * dRow;
                    int sCol2 = col + 2 * dCol;

                    if (inBounds(size, oRow2, oCol2) && inBounds(size, sRow2, sCol2)) {
                        if (board.get(oRow2, oCol2) == Cell.O &&
                            board.get(sRow2, sCol2) == Cell.S) {

                            return new int[]{row, col};
                        }
                    }
                }
            }
        }
        //another attempt to balance the game for o 
        // Extra logic for o try to steal a fork square where S would get 2+ scores
        int[] forkBlock = blockSFork(board, size);
        if (forkBlock != null) {
            return forkBlock;
        }

        return null;
    }

    
      //Find an empty square where, if S played there next,
      //S would create 2 or more one-move SOS threats (a fork).
      //O will play there instead to deny that fork.

    private int[] blockSFork(Board board, int size) {
        int bestRow = -1;
        int bestCol = -1;
        int bestThreats = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (!isEmpty(board.get(row, col))) continue;

                int threats = countThreatsIfSPlays(board, size, row, col);

                // consider as fork if 2 or more patterns
                if (threats >= 2 && threats > bestThreats) {
                    bestThreats = threats;
                    bestRow = row;
                    bestCol = col;
                }
            }
        }

        if (bestRow != -1) {
            return new int[]{bestRow, bestCol};
        }

        return null;
    }


    //count how many threats S would create by playing at (sRow, sCol)
    //chat gpt helped largly here
    private int countThreatsIfSPlays(Board board, int size, int sRow, int sCol) {

        int[][] directions = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1},
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}
        };

        Set<String> threatened = new HashSet<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

            
                if (!isEmpty(board.get(row, col))) continue;
                if (row == sRow && col == sCol) continue;
                for (int[] direction : directions) {
                    int dRow = direction[0];
                    int dCol = direction[1];

                    // looking at  _ O S  
                    int oRow1 = row + dRow;
                    int oCol1 = col + dCol;
                    int sRow1 = row + 2 * dRow;
                    int sCol1 = col + 2 * dCol;

                    if (inBounds(size, oRow1, oCol1) && inBounds(size, sRow1, sCol1)) {
                        Cell oCell = board.get(oRow1, oCol1);
                        Cell sCell = getCellAssumeS(board, sRow, sCol, sRow1, sCol1);

                        if (oCell == Cell.O && sCell == Cell.S) {
                            threatened.add(row + "," + col);
                        }
                    }

                    // looking at S O _ 
                    int sRow2 = row - 2 * dRow;
                    int sCol2 = col - 2 * dCol;
                    int oRow2 = row - dRow;
                    int oCol2 = col - dCol;

                    if (inBounds(size, oRow2, oCol2) && inBounds(size, sRow2, sCol2)) {
                        Cell oCell2 = board.get(oRow2, oCol2);
                        Cell sCell2 = getCellAssumeS(board, sRow, sCol, sRow2, sCol2);

                        if (oCell2 == Cell.O && sCell2 == Cell.S) {
                            threatened.add(row + "," + col);
                        }
                    }
                }
            }
        }

        return threatened.size();
    }

    // In the "what if S plays at (sRow, sCol)" world, return S there, else real board cell
    private Cell getCellAssumeS(Board board, int sRow, int sCol, int row, int col) {
        if (row == sRow && col == sCol) {
            return Cell.S;
        }
        return board.get(row, col);
    }

    private boolean isEmpty(Cell cell) {
        return cell != Cell.S && cell != Cell.O;
    }

    private boolean inBounds(int size, int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    private int[] findEmptyNeighbor(Board board, int size, int row, int col, boolean includeDiagonals) {

        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{-1, 0});
        directions.add(new int[]{ 1, 0});
        directions.add(new int[]{ 0,-1});
        directions.add(new int[]{ 0, 1});

        if (includeDiagonals) {
            directions.add(new int[]{-1,-1});
            directions.add(new int[]{-1,  1});
            directions.add(new int[]{ 1,-1});
            directions.add(new int[]{ 1,  1});
        }

        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = col + direction[1];

            if (!inBounds(size, neighborRow, neighborCol)) continue;

            if (isEmpty(board.get(neighborRow, neighborCol))) {
                return new int[]{neighborRow, neighborCol};
            }
        }

        return null;
    }

    private boolean isNearS(Board board, int size, int row, int col) {

        int[][] directions = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1},
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}
        };

        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = col + direction[1];

            if (!inBounds(size, neighborRow, neighborCol)) continue;

            if (board.get(neighborRow, neighborCol) == Cell.S) return true;
        }

        return false;
    }
}
