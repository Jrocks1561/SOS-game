package sos.game;

public class GameManager {
    private final Game game;

    // how many Sos lines were made by the last move
    private int lastMade = 0;

    public GameManager(int size, String mode) {
        Player p1 = Player.of("Player 1");
        Player p2 = Player.of("Player 2");
        if ("General".equalsIgnoreCase(mode)) {
            this.game = new GeneralGame(size, p1, p2);
        } else {
            this.game = new SimpleGame(size, p1, p2);
        }
    }

    public boolean placeMove(int r, int c) {
        //no more after gmae ends
        if (game.isOver()) return false; 
        try {
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

    public boolean isPlayerOneTurn() { return game.currentIndex() == 0; }
    public Game getGame() { return game; }

    // ---- Score/Status helpers used by GameScreen ----
    public boolean isOver() { return game.isOver(); }
    public String status() { return game.statusText(); }

    public int scoreP1() {
        if (game instanceof GeneralGame) return ((GeneralGame) game).score(0);
        return 0;
    }

    public int scoreP2() {
        if (game instanceof GeneralGame) return ((GeneralGame) game).score(1);
        return 0;
    }

    //how mnay lines made by last move
    public int lastMade() { return lastMade; }
}
