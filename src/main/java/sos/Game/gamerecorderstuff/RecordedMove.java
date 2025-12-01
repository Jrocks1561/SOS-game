package sos.Game.gamerecorderstuff;

public class RecordedMove {

    private final int moveNumber;
    private final int row;
    private final int col;
    private final char letter;
    private final int playerIndex;

    public RecordedMove(int moveNumber, int row, int col, char letter, int playerIndex) {
        this.moveNumber = moveNumber;
        this.row = row;
        this.col = col;
        this.letter = letter;
        this.playerIndex = playerIndex;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getLetter() {
        return letter;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }
}
