package sos.Game;

import java.util.ArrayList;
import java.util.Collections;


//can be use din both sos game modes
public abstract class Game {
    protected final Board board;
    protected final Player[] players = new Player[2];
    protected int current = 0;

    //scored SOS lines with players who scored them
    public static final class ScoredLine {
        public final Board.Line line;
        public final Player player;

        public ScoredLine(Board.Line line, Player player) {
            this.line = line;
            this.player = player;
        }
    }

    protected final java.util.List<ScoredLine> scoredLines = new ArrayList<>();

    public java.util.List<ScoredLine> getScoredLines() {
        return Collections.unmodifiableList(scoredLines);
    }

    public Game(int size, Player p1, Player p2) {
        this.board = new Board(size);
        this.players[0] = p1;
        this.players[1] = p2;
    }

    public Board board()            { return board; }
    public Player currentPlayer()   { return players[current]; }
    public int currentIndex()       { return current; }
    protected void swapTurn()       { current = 1 - current; }

    //making a move
    public abstract int move(Cell letter, int row, int col);

    //True when game is finshd
    public abstract boolean isOver();

    public abstract String statusText();

    public abstract String modeName();
}
