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

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Context ctx) throws BadRequestException {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);

        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Missing username/password");
        }

        try {
            AuthData authData = userService.createUser(userData);
            ctx.status(HttpStatus.OK);
            ctx.contentType("application/json");
            return new Gson().toJson(authData);
        } catch (BadRequestException e) {
            ctx.status(HttpStatus.FORBIDDEN);
            return "{ \"message\": \"Error: username already in use\" }";
        }
    }

    public Object login(Context ctx) throws UnauthorizedException, BadRequestException {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);

        AuthData authData = userService.loginUser(userData);

        ctx.status(HttpStatus.OK);
        return new Gson().toJson(authData);
    }
}
