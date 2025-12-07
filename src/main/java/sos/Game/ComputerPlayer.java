package sos.Game;

 //Base class for computer players.

public abstract class ComputerPlayer {
    public abstract int[] chooseMove(Board board, int size, int currentPlayerIndex);
}
