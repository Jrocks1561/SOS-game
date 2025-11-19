package sos.game;

/**
 * Base class for computer-controlled players.
 * Subclasses decide which row/col to play.
 */
public abstract class ComputerPlayer {
    public abstract int[] chooseMove(Board board, int size, int currentPlayerIndex);
}
