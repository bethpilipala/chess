package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    static void startServer() {
        server = new Server();
        port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/db"))
                .DELETE()
                .build();

        var response = HttpClient.newHttpClient().send(
                request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void successfulRequests() throws Exception {
        var registered = facade.register("alice", "password", "alice@example.com");
        assertEquals("alice", registered.username());
        assertFalse(registered.authToken().isBlank());

        var loggedIn = facade.login("alice", "password");
        String token = loggedIn.authToken();
        assertFalse(token.isBlank());

        int gameId = facade.createGame(token, "My game");
        var games = facade.listGames(token);
        assertEquals(1, games.size());
        assertEquals(gameId, games.get(0).gameID());
        assertEquals("My game", games.get(0).gameName());

        facade.joinGame(token, gameId, "WHITE");
        assertEquals("alice", facade.listGames(token).get(0).whiteUsername());

        assertDoesNotThrow(() -> facade.logout(token));
    }

    @Test
    void rejectedRequests() throws Exception {
        facade.register("alice", "password", "alice@example.com");

        assertThrows(ServerFacade.FacadeException.class,
                () -> facade.register("alice", "password", "alice@example.com"));
        assertThrows(ServerFacade.FacadeException.class,
                () -> facade.login("alice", "wrong"));
        assertThrows(ServerFacade.FacadeException.class,
                () -> facade.logout("invalid-token"));
        assertThrows(ServerFacade.FacadeException.class,
                () -> facade.createGame("invalid-token", "My game"));
        assertThrows(ServerFacade.FacadeException.class,
                () -> facade.listGames("invalid-token"));
        assertThrows(ServerFacade.FacadeException.class,
                () -> facade.joinGame("invalid-token", 1, "WHITE"));
    }
}