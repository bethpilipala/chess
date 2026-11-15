package client;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email)
            throws FacadeException {
        String body = send("POST", "/user", null,
                new RegisterRequest(username, password, email));
        return parseAuth(body);
    }

    public AuthData login(String username, String password)
            throws FacadeException {
        String body = send("POST", "/session", null,
                new LoginRequest(username, password));
        return parseAuth(body);
    }

    public void logout(String authToken) throws FacadeException {
        send("DELETE", "/session", authToken, null);
    }

    public int createGame(String authToken, String gameName)
            throws FacadeException {
        String body = send("POST", "/game", authToken,
                new CreateGameRequest(gameName));
        CreateGameResponse result = parse(body, CreateGameResponse.class);
        if (result == null || result.gameID() == null) {
            throw new FacadeException("The server returned an invalid game response.");
        }
        return result.gameID();
    }

    public List<GameSummary> listGames(String authToken)
            throws FacadeException {
        String body = send("GET", "/game", authToken, null);
        GameListResponse result = parse(body, GameListResponse.class);
        if (result == null || result.games() == null) {
            throw new FacadeException("The server returned an invalid game list.");
        }
        return result.games();
    }

    public void joinGame(String authToken, int gameId, String color)
            throws FacadeException {
        send("PUT", "/game", authToken, new JoinGameRequest(color, gameId));
    }

    private String send(String method, String path, String authToken, Object requestBody)
            throws FacadeException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Accept", "application/json");

        if (authToken != null) {
            // The server expects the raw token, without a "Bearer " prefix.
            request.header("Authorization", authToken);
        }

        HttpRequest.BodyPublisher bodyPublisher;
        if (requestBody == null) {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        } else {
            request.header("Content-Type", "application/json");
            bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody));
        }
        request.method(method, bodyPublisher);

        try {
            HttpResponse<String> response = httpClient.send(
                    request.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new FacadeException(messageForStatus(response.statusCode()));
            }
            return response.body();
        } catch (IOException e) {
            throw new FacadeException("Could not reach the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FacadeException("The request was interrupted.", e);
        }
    }

    private AuthData parseAuth(String body) throws FacadeException {
        AuthData result = parse(body, AuthData.class);
        if (result == null || result.authToken() == null || result.username() == null) {
            throw new FacadeException("The server returned invalid login information.");
        }
        return result;
    }

    private <T> T parse(String body, Class<T> type) throws FacadeException {
        try {
            return gson.fromJson(body, type);
        } catch (JsonParseException e) {
            throw new FacadeException("The server returned an invalid response.", e);
        }
    }

    private String messageForStatus(int statusCode) {
        return switch (statusCode) {
            case 400 -> "The request was invalid.";
            case 401 -> "You are not logged in. Please log in and try again.";
            case 403 -> "That username or game option is unavailable.";
            default -> "The server could not complete the request.";
        };
    }

    public record GameSummary(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName
    ) {}

    private record GameListResponse(List<GameSummary> games) {}
    private record CreateGameResponse(Integer gameID) {}
    private record RegisterRequest(String username, String password, String email) {}
    private record LoginRequest(String username, String password) {}
    private record CreateGameRequest(String gameName) {}
    private record JoinGameRequest(String playerColor, Integer gameID) {}

    public static class FacadeException extends Exception {
        private FacadeException(String message) {
            super(message);
        }

        private FacadeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}