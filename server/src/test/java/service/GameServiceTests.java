package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;

public class GameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    private GameService gameService;
    private UserService userService;

    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setup() throws Exception {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();

        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        tokenA = userService.createUser(new UserData("taylor", "12345", "a@a.a")).authToken();
        tokenB = userService.createUser(new UserData("rolyat", "54321", "b@b.b")).authToken();
    }
}
