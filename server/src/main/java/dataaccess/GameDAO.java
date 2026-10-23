package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear();

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameId);

    List<GameData> listGames();

    void updateGame(GameData game) throws DataAccessException;
}