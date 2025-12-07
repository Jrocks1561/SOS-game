package sos.Game.gamerecorderstuff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseGameRecorder implements GameRecorder {

    // info about one game (used for resume / replay)
    public static class GameInfo {
        public final int gameId;
        public final String mode;
        public final int boardSize;
        public final String result;
        public final boolean p1IsComputer;
        public final boolean p2IsComputer;
        public final String difficultyName;

        public GameInfo(int gameId, String mode, int boardSize, String result,
                        boolean p1IsComputer, boolean p2IsComputer, String difficultyName) {
            this.gameId = gameId;
            this.mode = mode;
            this.boardSize = boardSize;
            this.result = result;
            this.p1IsComputer = p1IsComputer;
            this.p2IsComputer = p2IsComputer;
            this.difficultyName = difficultyName;
        }
    }

    // Get info for one game
    public GameInfo loadGameInfo(int gameId) {
        String sql =
            "SELECT game_id, mode, board_size, result, " +
            "       p1_is_computer, p2_is_computer, difficulty " +
            "FROM sos_game_info WHERE game_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("game_id");
                    String mode = rs.getString("mode");
                    int size = rs.getInt("board_size");
                    String result = rs.getString("result");
                    boolean p1IsComp = rs.getBoolean("p1_is_computer");
                    boolean p2IsComp = rs.getBoolean("p2_is_computer");
                    String diffName = rs.getString("difficulty");
                    return new GameInfo(id, mode, size, result, p1IsComp, p2IsComp, diffName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error in loadGameInfo");
            e.printStackTrace();
        }
        return null;
    }

    // Get the most recent game (highest started_at)
    public int getLastGameId() {
        String sql = "SELECT game_id FROM sos_game_info " +
                     "ORDER BY started_at DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("game_id");
            }
        } catch (SQLException e) {
            System.err.println("Database Error in getLastGameId");
            e.printStackTrace();
        }
        return -1;
    }

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/my_sos_game";
    private static final String USER   = "postgres";
    private static final String PASS   = "IELP#9XES459";

    private int currentGameId = -1;

    public DatabaseGameRecorder() {
        System.out.println("Database DatabaseGameRecorder constructor called");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Database PostgreSQL driver loaded");
        } catch (ClassNotFoundException e) {
            System.err.println("Database PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    @Override
    public void startGame(int boardSize,
                          String mode,
                          boolean p1IsComputer,
                          boolean p2IsComputer,
                          String difficultyName) {

        String sql =
            "INSERT INTO sos_game_info " + "  (mode, board_size, p1_is_computer, p2_is_computer, difficulty) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING game_id";

        currentGameId = -1;
        System.out.println("Database startGame() – connecting to Database-->");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("Database Connected to the DB in startGame");

            pstmt.setString(1, mode);
            pstmt.setInt(2, boardSize);
            pstmt.setBoolean(3, p1IsComputer);
            pstmt.setBoolean(4, p2IsComputer);
            pstmt.setString(5, difficultyName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentGameId = rs.getInt("game_id");
                    System.out.println( "Database  New game started. game_id=" + currentGameId + ", mode=" + mode + ", size=" + boardSize + ", p1IsComputer=" + p1IsComputer + ", p2IsComputer=" + p2IsComputer + ", difficulty=" + difficultyName);
                } else {
                    System.err.println("Database startGame did not return a game_id");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB: Error starting game in database: " +
                               e.getMessage() + " (SQLState=" + e.getSQLState() + ")");
            e.printStackTrace();
            currentGameId = -1;
        }

        System.out.println("DB: startGame() finished, currentGameId=" + currentGameId);
    }

    @Override
    public void recordMove(int moveNumber, int row, int col, char letter, int playerIndex) {
        System.out.println("DB: recordMove entered, currentGameId=" + currentGameId);
        if (currentGameId <= 0) {
            System.out.println("DB: recordMove called but currentGameId <= 0, skipping");
            return;
        }

        String sql = "INSERT INTO game_moves " + "(game_id, move_number, row_index, col_index, letter, player_index) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentGameId);
            stmt.setInt(2, moveNumber);
            stmt.setInt(3, row);
            stmt.setInt(4, col);
            stmt.setString(5, String.valueOf(letter)); // letter
            stmt.setInt(6, playerIndex);               // player_index

            stmt.executeUpdate();

            System.out.println("Database: Recorded move " + moveNumber + " (game_id=" + currentGameId + "), row=" + row + ", col=" + col + ", letter=" + letter + ", playerIndex=" + playerIndex);

        } catch (SQLException e) {
            System.err.println("Database Error in recordMove while inserting game_moves");
            e.printStackTrace();
        }
    }

    @Override
    public void endGame(String result) {
        System.out.println("Databse endGame, currentGameId=" + currentGameId);
        if (currentGameId <= 0) {
            System.out.println("Database endGame called but currentGameId <= 0, skipping");
            return;
        }

        String sql = "UPDATE sos_game_info " + "SET result = ?, ended_at = NOW() " + "WHERE game_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, result);
            stmt.setInt(2, currentGameId);

            stmt.executeUpdate();
            System.out.println("Database Game " + currentGameId + " ended with result: " + result);

        } catch (SQLException e) {
            System.err.println("Database Error in endGame while updating sos_game_info");
            e.printStackTrace();
        }
    }

    @Override
    public List<RecordedMove> loadGame(int gameId) {
        List<RecordedMove> moves = new ArrayList<>();

        String sql = "SELECT move_number, row_index, col_index, letter, player_index " + "FROM game_moves " + "WHERE game_id = ? " + "ORDER BY move_number ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int moveNumber = rs.getInt("move_number");
                    int row = rs.getInt("row_index");
                    int col = rs.getInt("col_index");
                    char letter = rs.getString("letter").charAt(0);
                    int playerIndex = rs.getInt("player_index");

                    moves.add(new RecordedMove(moveNumber, row, col, letter, playerIndex));
                }
            }

        } catch (SQLException e) {
            System.err.println("Data base Error in loadGame while querying game_moves");
            e.printStackTrace();
        }

        return moves;
    }

    @Override
    public int getCurrentGameId() {
        System.out.println("Data base getCurrentGameId() returning " + currentGameId);
        return currentGameId;
    }
}
