package client;

import chess.*;
import model.AuthData;

import java.util.List;
import java.util.Scanner;

public class ClientMain {
    private final ServerFacade facade = new ServerFacade(8080);
    private final Scanner scanner = new Scanner(System.in);
    private String authToken;
    private List<ServerFacade.GameSummary> games = List.of();

    public static void main(String[] args) {
        ClientMain client = new ClientMain();

        var piece = new ChessPiece(
                ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.PAWN
        );
        System.out.println("♕ 240 Chess Client: " + piece);

        client.run();
    }

    private void run() {
        System.out.println("Welcome to Chess! Type help to see commands.");

        while (true) {
            String prompt = authToken == null ? "[signed out] > " : "[signed in] > ";
            String command = readLine(prompt);

            if (command == null || command.trim().equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                return;
            }

            if (authToken == null) {
                handleSignedOut(command.trim().toLowerCase());
            } else {
                handleSignedIn(command.trim().toLowerCase());
            }
        }
    }

    private void login() {
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        if (username == null || password == null) {
            return;
        }

        try {
            AuthData auth = facade.login(username, password);
            authToken = auth.authToken();
            System.out.println("You are signed in.");
        } catch (ServerFacade.FacadeException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private void logout() {
        try {
            facade.logout(authToken);
            authToken = null;
            games = List.of();
            System.out.println("You are signed out.");
        } catch (ServerFacade.FacadeException e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void handleSignedOut(String command) {
        switch (command) {
            case "help" -> System.out.println(
                    "Commands: help, register, login, quit");
            case "register" -> register();
            case "login" -> login();
            default -> System.out.println("Unknown command. Type help.");
        }
    }

    private void register() {
        String username = readLine("Username: ");
        String password = readLine("Password: ");
        String email = readLine("Email: ");

        if (username == null || password == null || email == null) {
            return;
        }

        try {
            AuthData auth = facade.register(username, password, email);
            authToken = auth.authToken();
            System.out.println("Registration successful. You are signed in.");
        } catch (ServerFacade.FacadeException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void handleSignedIn(String command) {
        switch (command) {
            case "help" -> System.out.println(
                    "Commands: help, create game, list games, logout, quit");
            case "create game" -> createGame();
            case "list games" -> listGames();
            case "logout" -> logout();
            default -> System.out.println("Unknown command. Type help.");
        }
    }

    private void createGame() {
        String name = readLine("Game name: ");

        if (name == null) {
            return;
        }

        if (name.isBlank()) {
            System.out.println("Please enter a game name.");
            return;
        }

        try {
            facade.createGame(authToken, name.trim());
            games = List.of();
            System.out.println("Game created. Use list games to see it.");
        } catch (ServerFacade.FacadeException e) {
            System.out.println("Could not create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            games = facade.listGames(authToken);

            if (games.isEmpty()) {
                System.out.println("No games found.");
                return;
            }

            for (int i = 0; i < games.size(); i++) {
                var game = games.get(i);
                String white = game.whiteUsername() == null
                        ? "open"
                        : game.whiteUsername();
                String black = game.blackUsername() == null
                        ? "open"
                        : game.blackUsername();

                System.out.printf("%d. %s — White: %s, Black: %s%n",
                        i + 1, game.gameName(), white, black);
            }
        } catch (ServerFacade.FacadeException e) {
            System.out.println("Could not list games: " + e.getMessage());
        }
    }
}