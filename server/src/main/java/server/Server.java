package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;

import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private final Javalin javalin;

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    static UserService userService;
    static GameService gameService;

    private UserHandler userHandler;
    private GameHandler gameHandler;

    static ConcurrentHashMap<Session, Integer> gameSessions = new ConcurrentHashMap<>();

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);

        // UserHandler endpoints
        javalin.post("/user", ctx -> userHandler.register(ctx));
        javalin.post("/session", ctx -> userHandler.login(ctx));
        javalin.delete("/session", ctx -> userHandler.logout(ctx));

        // GameHandler endpoints
        javalin.get("/game", ctx -> gameHandler.listGames(ctx));
        javalin.post("/game", ctx -> gameHandler.createGame(ctx));
        javalin.put("/game", ctx -> gameHandler.joinGame(ctx));

        // Exception handlers
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        });
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
        });
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).result("{ \"message\": \"Error: %s\" }".formatted(e.getMessage()));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private Object clear(Context ctx) {
        ctx.status(HttpStatus.OK);
        return "{}";
    }
}
