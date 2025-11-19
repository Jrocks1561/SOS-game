package sos.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * "Easy" computer player.
 * Chooses simple moves: try to extend patterns, otherwise play randomly.
 */
public class EasyComputerPlayer extends ComputerPlayer {

    @Override
    public int[] chooseMove(Board board, int size, int currentPlayerIndex) {

        // Player 1 places S and Player 2 places O
        boolean playsS = (currentPlayerIndex == 0);

        
        // Look around the board for a simple opportunity
        // If we see an O (for S-player) or an S (for O-player) try placing next to it to form or extend patterns
        //**might change this logic a bit i feel like it is harder for O to score */
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Cell cell = board.get(r, c);

                // S-player looks for O and O-player looks for S
                if (playsS && cell == Cell.O) {
                    int[] pos = findEmptyNeighbor(board, size, r, c);
                    if (pos != null) return pos;

                } else if (!playsS && cell == Cell.S) {
                    int[] pos = findEmptyNeighbor(board, size, r, c);
                    if (pos != null) return pos;
                }
            }
        }

       
        // If nothing useful is found, choose a random empty cell to keep the game moving
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (isEmpty(board.get(r, c))) {
                    empties.add(new int[]{r, c});
                }
            }
        }

        if (!empties.isEmpty()) {
            int idx = ThreadLocalRandom.current().nextInt(empties.size());
            return empties.get(idx);
        }

        // No available move
        return null;
    }

    // Check if a cell is still unfilled
    private boolean isEmpty(Cell cell) {
        return cell != Cell.S && cell != Cell.O;
    }

    // Look for any empty neighbor around (r, c)
    private int[] findEmptyNeighbor(Board board, int size, int r, int c) {
        int[][] dirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];

            // skip positions off the board
            if (nr < 0 || nr >= size || nc < 0 || nc >= size) continue;

            if (isEmpty(board.get(nr, nc))) {
                return new int[]{nr, nc};
            }
        }
        return null;
    }
}
