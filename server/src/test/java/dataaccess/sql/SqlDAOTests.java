package dataaccess.sql;

import dataaccess.DatabaseSchema;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDAOTests {
    private SqlUserDAO userDAO;
    private SqlAuthDAO authDAO;
    private SqlGameDAO gameDAO;

    @BeforeAll
    static void initializeDatabase() throws Exception {
        DatabaseSchema.initialize();
    }

    @BeforeEach
    void setUp() throws Exception {
        userDAO = new SqlUserDAO();
        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();

        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    void createAndGetUser() throws Exception {
        UserData user = new UserData("beth", "hashedPasswordHehe", "beth@byu.edu");

        userDAO.createUser(user);

        assertEquals(user, userDAO.getUser("beth"));
    }

    @Test
    void getMissingUserReturnsNull() throws Exception {
        assertNull(userDAO.getUser("unknown"));
    }

    @Test
    void createAndGetAuth() throws Exception {
        AuthData auth = new AuthData("token123", "beth");

        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("token123"));
    }

    @Test
    void deleteAuth() throws Exception {
        authDAO.createAuth(new AuthData("token123", "beth"));

        authDAO.deleteAuth("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void createAndGetGame() throws Exception {
        int id = gameDAO.createGame("My Game Test");

        GameData game = gameDAO.getGame(id);
        assertNotNull(game);
        assertEquals("My Game Test", game.gameName());
    }

    @Test
    void listGames() throws Exception {
        gameDAO.createGame("Game Uno");
        gameDAO.createGame("Game Dos");

        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    void updateGamePlayers() throws Exception {
        int id = gameDAO.createGame("Pride and Prejudice");
        GameData game = gameDAO.getGame(id);
        GameData updated = new GameData(id, "elizabeth", "darcy", game.gameName(), game.game());

        gameDAO.updateGame(updated);

        assertEquals("elizabeth", gameDAO.getGame(id).whiteUsername());
    }
}