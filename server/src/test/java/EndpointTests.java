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
}