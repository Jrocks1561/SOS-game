package sos.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// "Easy" computer player.

//note the more complex logic for o is because o needs to control the board more than s,
//so that the game can be a bit more fair.
public class EasyComputerPlayer extends ComputerPlayer {

    @Override
    public int[] chooseMove(Board board, int size, int currentPlayerIndex) {

        // Player 1 places S, Player 2 places O
        boolean isSPlayer = (currentPlayerIndex == 0);
        Cell opponentLetter = isSPlayer ? Cell.O : Cell.S;

        
        // S player simple offensice logic
        if (isSPlayer) {
            // Look for any O and try to place S next to it only (4 directions)
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (board.get(row, col) == opponentLetter) {
                        int[] neighborCell = findEmptyOrthogonalNeighbor(board, size, row, col);
                        if (neighborCell != null) {
                            return neighborCell;
                        }
                    }
                }
            }
            // If nothing found, place in a random empty space
        }

        // o player logic defsense behavior
        if (!isSPlayer) {
            //dont let s win immediately, if possible place o in the middle of s _ s
            int[] blockingMove = findBlockSpotForS(board, size);
            if (blockingMove != null) {
                return blockingMove;
            }

            // if no scoring then place away from s beacuse o needs control of board
            List<int[]> safeSpots = new ArrayList<>();
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (isEmpty(board.get(row, col)) && !isNearS(board, size, row, col)) {
                        safeSpots.add(new int[]{row, col});
                    }
                }
            }

            if (!safeSpots.isEmpty()) {
                int randomSafeIndex = ThreadLocalRandom.current().nextInt(safeSpots.size());
                return safeSpots.get(randomSafeIndex);
            }

            // handle no spots 
        }

        
        // if no move place in a random empty cell
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isEmpty(board.get(row, col))) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(emptyCells.size());
            return emptyCells.get(randomIndex);
        }

        return null;
    }

    // Helpers

    // Cell is empty if it is not an S and not an O
    private boolean isEmpty(Cell cell) {
        return cell != Cell.S && cell != Cell.O;
    }

    // Returns an empty up/down/left/right neighbor if any exist else null
    private int[] findEmptyOrthogonalNeighbor(Board board, int size, int row, int col) {
        int[][] directionOffsets = {
            {-1, 0},  // up
            { 1, 0},  // down
            { 0,-1},  // left
            { 0, 1}   // right
        };

        for (int[] offset : directionOffsets) {
            int neighborRow = row + offset[0];
            int neighborCol = col + offset[1];

            if (neighborRow < 0 || neighborRow >= size || neighborCol < 0 || neighborCol >= size) continue;

            else if (isEmpty(board.get(neighborRow, neighborCol))) {
                return new int[] { neighborRow, neighborCol };
            }
        }

        return null;
    }

    // For O find the middle of any S _ S pattern all 8 directions 
    private int[] findBlockSpotForS(Board board, int size) {
        int[][] directionOffsets = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},   // straight
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}    // diagonals
        };

        // Treat each empty cell as a potential middle of S _ S
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!isEmpty(board.get(row, col))) continue;

                for (int[] offset : directionOffsets) {
                    int dr = offset[0], dc = offset[1];

                    int sRow1 = row + dr;
                    int sCol1 = col + dc;
                    int sRow2 = row - dr;
                    int sCol2 = col - dc;

                    if (!inBounds(size, sRow1, sCol1) || !inBounds(size, sRow2, sCol2)) continue;

                    else if (board.get(sRow1, sCol1) == Cell.S && board.get(sRow2, sCol2) == Cell.S) {
                        // This empty cell is between two S: S _ S
                        return new int[]{row, col};
                    }
                }
            }
        }
        return null;
    }

    // Check if a coordinate is on the board
    private boolean inBounds(int size, int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

     // Checks if this empty spot is right next to any S in 8 directions
    private boolean isNearS(Board board, int size, int row, int col) {
        int[][] directionOffsets = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1},
            {-1,-1}, {-1, 1}, {1,-1}, {1, 1}
        };

        for (int[] offset : directionOffsets) {
            int neighborRow = row + offset[0];
            int neighborCol = col + offset[1];
            if (!inBounds(size, neighborRow, neighborCol)) continue;
            else if (board.get(neighborRow, neighborCol) == Cell.S) return true;
        }
        return false;
    }
}
