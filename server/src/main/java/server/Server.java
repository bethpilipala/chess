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
        //POST /user
        //POST /session
        //DELETE /session
        //GET /game
        //POST /game
        //PUT /game

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
