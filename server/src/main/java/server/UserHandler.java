package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws BadRequestException {
        UserData userData = ctx.bodyAsClass(UserData.class);

        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Missing username/password");
        }

        try {
            AuthData authData = userService.createUser(userData);
            ctx.status(HttpStatus.OK).json(authData);
        } catch (BadRequestException e) {
            ctx.status(HttpStatus.FORBIDDEN)
                    .json(Map.of("message", "Error: username already in use"));
        }
    }

    public void login(Context ctx) throws UnauthorizedException, BadRequestException {
        UserData userData = ctx.bodyAsClass(UserData.class);

        if (userData == null || userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Missing username/password");
        }

        AuthData authData = userService.loginUser(userData);
        ctx.status(HttpStatus.OK).json(authData);
    }

    public void logout(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("authorization");

        userService.logoutUser(authToken);

        ctx.status(HttpStatus.OK).json(Map.of()); // empty Map.of() returns {}
    }
}
