package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameId = 1;

    @Override
    public void clear() {
        games.clear();
        nextGameId = 1; //completley reset it
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        int id = nextGameId++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (game == null || !games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: bad request");
        }

        games.put(game.gameID(), game);
    }
}