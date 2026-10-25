package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    //DAOs
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    //Services
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(authDAO, gameDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        //EXCEPTIONS
        //400: bad request input
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });

        //401: invalid/missing auth token
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });

        //403- username already taken, color already taken, etc.
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });

        //500: data/storage failures
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });

        //500 other
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        });

        //ENDPOINTS (TODO)
        //DELETE /db
        javalin.delete("/db", ctx -> {
            clearService.clear();
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of()));
        });

        //POST /user
        javalin.post("/user", ctx -> {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            AuthResult result = userService.register(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        });

        //POST /session
        javalin.post("/session", ctx -> {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            AuthResult result = userService.login(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        });

        //DELETE /session
        javalin.delete("/session", ctx -> {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of()));
        });

        //GET /game
        javalin.get("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            ListGamesResult result = gameService.listGames(authToken);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        });

        //POST /game
        javalin.post("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);

            CreateGameResult result = gameService.createGame(request, authToken);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        });

        //PUT /game
        javalin.put("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);

            gameService.joinGame(request, authToken);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of()));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
