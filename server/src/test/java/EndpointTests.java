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
}