package sos.Game.gamerecorderstuff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseGameRecorder implements GameRecorder {
    // Inside DatabaseGameRecorder.java, near the top (but inside the class):

public static class GameInfo {
    public final int gameId;
    public final String mode;
    public final int boardSize;
    public final String result;

    public GameInfo(int gameId, String mode, int boardSize, String result) {
        this.gameId = gameId;
        this.mode = mode;
        this.boardSize = boardSize;
        this.result = result;
    }
}

// Get info for one game
public GameInfo loadGameInfo(int gameId) {
    String sql = "SELECT game_id, mode, board_size, result " +
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
                return new GameInfo(id, mode, size, result);
            }
        }
    } catch (SQLException e) {
        System.err.println("DB: Error in loadGameInfo");
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
        System.err.println("DB: Error in getLastGameId");
        e.printStackTrace();
    }
    return -1;
}


    private static final String DB_URL = "jdbc:postgresql://localhost:5432/my_sos_game";
    private static final String USER   = "postgres";
    private static final String PASS   = "IELP#9XES459";

    private int currentGameId = -1;
    

    public DatabaseGameRecorder() {
        System.out.println("DB: DatabaseGameRecorder constructor called");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("DB: PostgreSQL driver loaded");
        } catch (ClassNotFoundException e) {
            System.err.println("DB: PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    @Override
    public void startGame(int boardSize, String mode) {
        String sql = "INSERT INTO sos_game_info (mode, board_size) " +
                     "VALUES (?, ?) RETURNING game_id";

        currentGameId = -1;
        System.out.println("DB: startGame() – connecting to DB…");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("DB: Connected to DB in startGame");

            pstmt.setString(1, mode);
            pstmt.setInt(2, boardSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentGameId = rs.getInt("game_id");
                    System.out.println("DB: New game started. game_id=" +
                            currentGameId + ", mode=" + mode + ", size=" + boardSize);
                } else {
                    System.err.println("DB: startGame did not return a game_id");
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

        String sql = "INSERT INTO game_moves " +
                     "(game_id, move_number, row_index, col_index, letter, player_index) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentGameId);
            stmt.setInt(2, moveNumber);
            stmt.setInt(3, row);
            stmt.setInt(4, col);
            stmt.setString(5, String.valueOf(letter)); // letter
            stmt.setInt(6, playerIndex);               // player_index

            stmt.executeUpdate();

            System.out.println("DB: Recorded move " + moveNumber +
                    " (game_id=" + currentGameId + "), row=" + row +
                    ", col=" + col + ", letter=" + letter +
                    ", playerIndex=" + playerIndex);

        } catch (SQLException e) {
            System.err.println("DB: Error in recordMove while inserting game_moves");
            e.printStackTrace();
        }
    }

    @Override
    public void endGame(String result) {
        System.out.println("DB: endGame entered, currentGameId=" + currentGameId);
        if (currentGameId <= 0) {
            System.out.println("DB: endGame called but currentGameId <= 0, skipping");
            return;
        }

        String sql = "UPDATE sos_game_info " +
                     "SET result = ?, ended_at = NOW() " +
                     "WHERE game_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, result);
            stmt.setInt(2, currentGameId);

            stmt.executeUpdate();
            System.out.println("DB: Game " + currentGameId + " ended with result: " + result);

        } catch (SQLException e) {
            System.err.println("DB: Error in endGame while updating sos_game_info");
            e.printStackTrace();
        }
    }

    @Override
    public List<RecordedMove> loadGame(int gameId) {
        List<RecordedMove> moves = new ArrayList<>();

        String sql = "SELECT move_number, row_index, col_index, letter, player_index " +
                     "FROM game_moves " +
                     "WHERE game_id = ? " +
                     "ORDER BY move_number ASC";

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
            System.err.println("DB: Error in loadGame while querying game_moves");
            e.printStackTrace();
        }

        return moves;
    }

    @Override
    public int getCurrentGameId() {
        System.out.println("DB: getCurrentGameId() returning " + currentGameId);
        return currentGameId;
    }
}
