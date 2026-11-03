package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlGameDAO implements GameDAO {
    private static final String SELECT_GAMES =
            "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games";
    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        // TRUNCATE resets AUTO_INCREMENT so ids restart at 1, like the memory DAO
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("TRUNCATE TABLE games")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to clear games", e);
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (NULL, NULL, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameName);
            ps.setString(2, gson.toJson(new ChessGame()));
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DataAccessException("Error: unable to create game");
                }
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(SELECT_GAMES + " WHERE gameID = ?")) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? readGame(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to get game", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(SELECT_GAMES);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                games.add(readGame(rs));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to list games", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (game == null || getGame(game.gameID()) == null) {
            throw new DataAccessException("Error: bad request");
        }

        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gson.toJson(game.game()));
            ps.setInt(5, game.gameID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to update game", e);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        return new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                gson.fromJson(rs.getString("chessGame"), ChessGame.class));
    }
}