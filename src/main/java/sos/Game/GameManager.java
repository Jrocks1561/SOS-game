package sos.game;

import java.awt.Color;

public class GameManager {
    private final Game game;

    // how many SOS lines were made by the last move
    private int lastMade = 0;

    public GameManager(int size, String mode) {
        Player p1 = Player.of("Player 1", Color.BLUE);
        Player p2 = Player.of("Player 2", Color.YELLOW);

        if ("General".equalsIgnoreCase(mode)) {
            this.game = new GeneralGame(size, p1, p2);
        } else {
            this.game = new SimpleGame(size, p1, p2);
        }
    }

    public boolean placeMove(int r, int c) {
        // no more moves after game ends
        if (game.isOver()) return false;

        try {
            // index 0 = Player 1 (S), index 1 = Player 2 (O)
            Cell letter = (game.currentIndex() == 0) ? Cell.S : Cell.O;
            lastMade = game.move(letter, r, c);
            return true;
        } catch (IllegalStateException ex) {
            lastMade = 0;
            return false;
        }
    }

    public String getCell(int r, int c) {
        Cell cell = game.board().get(r, c);
        if (cell == Cell.S) return "S";
        if (cell == Cell.O) return "O";
        return "";
    }

    public boolean isPlayerOneTurn() {
        return game.currentIndex() == 0;
    }

    public Game getGame() {
        return game;
    }

    // ---- Score/Status helpers used by GameScreen ----

    public boolean isOver() {
        return game.isOver();
    }

    public String status() {
        return game.statusText();
    }

    public int scoreP1() {
        if (game instanceof GeneralGame) {
            return ((GeneralGame) game).score(0);
        }
        return 0;
    }

    public int scoreP2() {
        if (game instanceof GeneralGame) {
            return ((GeneralGame) game).score(1);
        }
        return 0;
    }

    // how many lines made by last move
    public int lastMade() {
        return lastMade;
    }

    // let the UI access the board (for SOS line drawing, if needed)
    public Board getBoard() {
        return game.board();
    }

    // let the UI access colored SOS lines
    public java.util.List<Game.ScoredLine> getScoredLines() {
        return game.getScoredLines();
    }
}
