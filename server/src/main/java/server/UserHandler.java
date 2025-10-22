package server;

import service.UserService;

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }
}
