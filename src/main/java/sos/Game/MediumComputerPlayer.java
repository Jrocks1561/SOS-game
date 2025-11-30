package sos.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MediumComputerPlayer extends ComputerPlayer {

    @Override
    public int[] chooseMove(Board board, int size, int currentPlayerIndex) {

        // Player 1 is S, Player 2 is O
        boolean isSPlayer = (currentPlayerIndex == 0);


        // Try the smarter medium-level move first
        if (isSPlayer) {

            // S tries to finish patterns like S-O-_ or _-O-S
            int[] smartSMove = findBestMoveForS(board, size);
            if (smartSMove != null) return smartSMove;
        } 

        else {

            // O tries to react to S setups if possible
            int[] smartOMove = findBestMoveForO(board, size);
            if (smartOMove != null) return smartOMove;
        }


        // no imediate scoring logic
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


        // if no other options random empty cell
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

                    if (in1 && in2 &&
                        board.get(row1, col1) == Cell.S &&
                        isEmpty(board.get(row2, col2))) {

                        return new int[]{row2, col2};
                    }

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

    // O tries to score S-O-S or block S from finishing SO_ or _OS
    //again more advanced logic for O since game is unbalanced if given same logic as S
    private int[] findBestMoveForO(Board board, int size) {

        int[][] directions = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1},
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}
        };

        // Try to score O by making S-O-S
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

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

                    if (board.get(row1, col1) == Cell.S &&
                        board.get(row2, col2) == Cell.S) {

                        return new int[]{row, col};
                    }
                }
            }
        }


        // If O can't score, try to block S setups
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

                    if (!inBounds(size, row1, col1) || !inBounds(size, row2, col2))
                        continue;

                    if (board.get(row1, col1) == Cell.S && isEmpty(board.get(row2, col2))) {
                        return new int[]{row2, col2};
                    }

                    if (isEmpty(board.get(row1, col1)) && board.get(row2, col2) == Cell.S) {
                        return new int[]{row1, col1};
                    }
                }
            }
        }

        return null;
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
            directions.add(new int[]{-1, 1});
            directions.add(new int[]{ 1,-1});
            directions.add(new int[]{ 1, 1});
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
