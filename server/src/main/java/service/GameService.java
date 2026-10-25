package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest req, String authToken)
            throws UnauthorizedException, BadRequestException, DataAccessException {

        requireAuth(authToken);

        if (req == null || req.gameName() == null || req.gameName().isBlank()) {
            throw new BadRequestException("Error: bad request");
        }

        int id = gameDAO.createGame(req.gameName());
        return new CreateGameResult(id);
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException {
        requireAuth(authToken);

        List<GameListEntry> games = new ArrayList<>();
        for (GameData g : gameDAO.listGames()) {
            games.add(new GameListEntry(
                    g.gameID(),
                    g.whiteUsername(),
                    g.blackUsername(),
                    g.gameName()
            ));
        }
        return new ListGamesResult(games);
    }

    public void joinGame(JoinGameRequest req, String authToken)
            throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {

        AuthData auth = requireAuth(authToken);

        if (req == null || req.gameID() == null || req.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }

        String color = req.playerColor();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new BadRequestException("Error: bad request");
        }

        GameData game = gameDAO.getGame(req.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        GameData updated;
        if (color.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }

            updated = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }

            updated = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }

        gameDAO.updateGame(updated);
    }

    private AuthData requireAuth(String authToken) throws UnauthorizedException {
        if (authToken == null || authToken.isBlank()) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        return auth;
    }
}