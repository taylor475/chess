package ui;

import client.ServerFacade;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PreloginRepl {
    ServerFacade server;
    PostloginRepl postloginRepl;

    public PreloginRepl(ServerFacade server) {
        this.server = server;
        postloginRepl = new PostloginRepl(server);
    }

    public void run() {
        boolean loggedIn = false;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to Chess! Enter 'help' to get started.");
        while (!loggedIn) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "quit":
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "login":
                    if (input.length != 3) {
                        out.println("Please provide a username and password.");
                        printLoginInstr();
                        break;
                    }
                    if (server.login(input[1], input[2])) {
                        out.println("You are now logged in.");
                        loggedIn = true;
                    } else {
                        out.println("Username or password incorrect, please try again.");
                        printLoginInstr();
                    }
                    break;
                case "register":
                    if (input.length != 4) {
                        out.println("Please provide a username, password, and email.");
                        printRegisterInstr();
                        break;
                    }
                    if (server.register(input[1], input[2], input[3])) {
                        out.println("You are now registered and logged in.");
                        loggedIn = true;
                    } else {
                        out.println("Username already in use, please select a different username.");
                        printRegisterInstr();
                    }
                    break;
                default:
                    out.println("Command not recognized, please try again.");
                    printHelpMenu();
                    break;
            }
        }
        postloginRepl.run();
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED OUT] >>> ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return new String[] {""};
        return line.split("\\s+");
    }

    private void printHelpMenu() {
        printRegisterInstr();
        printLoginInstr();
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printRegisterInstr() {
        out.println("register <USERNAME> <PASSWORD> <EMAIL> - create a new user");
    }

    private void printLoginInstr() {
        out.println("login <USERNAME> <PASSWORD> - login to an existing user");
    }
}
