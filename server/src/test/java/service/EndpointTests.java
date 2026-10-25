package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EndpointTests {

    @Test
    public void clearEndpointTest() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("lucille", "password", "lucilleball@gmail.com"));
        authDAO.createAuth(new AuthData("token", "lucille"));
        int gameId = gameDAO.createGame("My First Game!!");

        //sanity check!
        assertNotNull(userDAO.getUser("lucille"));
        assertNotNull(authDAO.getAuth("token"));
        assertNotNull(gameDAO.getGame(gameId));

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        //make sure everything is gone
        assertNull(userDAO.getUser("lucille"));
        assertNull(authDAO.getAuth("token"));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void registerSuccessTest() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        RegisterRequest req = new RegisterRequest("newUser", "pw123", "new@mail.com");

        AuthResult result = userService.register(req);

        assertNotNull(result);
        assertEquals("newUser", result.username());
        assertNotNull(result.authToken());

        assertNotNull(userDAO.getUser("newUser"));

        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void registerDuplicateUserTest() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        RegisterRequest req = new RegisterRequest("sameUser", "pw", "a@mail.com");
        userService.register(req);

        AlreadyTakenException ex = assertThrows(AlreadyTakenException.class, () ->
                userService.register(new RegisterRequest("sameUser", "pw2", "b@mail.com"))
        );

        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void registerBadRequestTest() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        RegisterRequest bad = new RegisterRequest("user", null, "user@mail.com");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.register(bad)
        );

        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void loginSuccessTest() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        userService.register(new RegisterRequest("lizzie", "ilovedarcy", "elizabethbennett@mail.com"));

        AuthResult result = userService.login(new LoginRequest("lizzie", "ilovedarcy"));

        assertNotNull(result);
        assertEquals("lizzie", result.username());
        assertNotNull(result.authToken());
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void loginWrongPasswordTest() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        userService.register(new RegisterRequest("lizzie", "ilovedarcy", "elizabethbennett@mail.com"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("lizzie", "ihatedarcy"))
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void loginBadRequestTest() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        //missing username
        LoginRequest bad = new LoginRequest(null, "password");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.login(bad)
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void logoutSuccessTest() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        AuthResult reg = userService.register(new RegisterRequest("harry", "wizardsrock123", "potter@gmail.com"));
        String token = reg.authToken();
        assertNotNull(authDAO.getAuth(token));

        userService.logout(token);

        assertNull(authDAO.getAuth(token));
    }

    @Test
    public void logoutUnauthorizedBadTokenTest() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                userService.logout("notatoken")
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void listGamesSuccessTest() throws Exception {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        authDAO.createAuth(new model.AuthData("token1", "nephi"));
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");

        ListGamesResult result = gameService.listGames("token1");

        assertNotNull(result);
        assertNotNull(result.games());
        assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesUnauthorizedTest() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                gameService.listGames("badtoken")
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void createGameSuccessTest() throws Exception {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        authDAO.createAuth(new model.AuthData("createtoken", "helaman"));

        CreateGameResult result = gameService.createGame(new CreateGameRequest("My New Game"), "createtoken");

        assertNotNull(result);
        assertNotNull(result.gameID());
        assertTrue(result.gameID() > 0);
        assertNotNull(gameDAO.getGame(result.gameID()));
    }

    @Test
    public void createGameUnauthorizedTest() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                gameService.createGame(new CreateGameRequest("Game"), "badtoken")
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void createGameBadRequestTest() throws Exception {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        authDAO.createAuth(new model.AuthData("createtoken2", "helaman"));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                gameService.createGame(new CreateGameRequest("   "), "createtoken2")
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void joinGameSuccessTest() throws Exception {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        authDAO.createAuth(new model.AuthData("tokentojoin", "adam"));
        int gameId = gameDAO.createGame("Joinable Game");

        gameService.joinGame(new JoinGameRequest("WHITE", gameId), "tokentojoin");

        var game = gameDAO.getGame(gameId);
        assertEquals("adam", game.whiteUsername());
    }

    @Test
    public void joinGameUnauthorizedTest() throws Exception {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);
        int gameId = gameDAO.createGame("Joinable Game");

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                gameService.joinGame(new JoinGameRequest("BLACK", gameId), "badtoken")
        );
        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    public void joinGameColorAlreadyTakenTest() throws Exception {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(authDAO, gameDAO);

        authDAO.createAuth(new model.AuthData("token1", "adam"));
        authDAO.createAuth(new model.AuthData("token2", "eve"));
        int gameId = gameDAO.createGame("AlreadyTakenColor");

        gameService.joinGame(new JoinGameRequest("WHITE", gameId), "token1");

        AlreadyTakenException ex = assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(new JoinGameRequest("WHITE", gameId), "token2")
        );
        assertTrue(ex.getMessage().contains("Error"));
    }
}