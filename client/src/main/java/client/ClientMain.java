package client;

import chess.*;
import model.AuthData;

import java.util.Scanner;

public class ClientMain {
    private final ServerFacade facade = new ServerFacade(8080);
    private final Scanner scanner = new Scanner(System.in);
    private String authToken;

    public static void main(String[] args) {
        ClientMain client = new ClientMain();

        var piece = new ChessPiece(
                ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.PAWN
        );
        System.out.println("♕ 240 Chess Client: " + piece);

        client.login();

        if (client.authToken != null) {
            client.logout();
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
            System.out.println("You are signed out.");
        } catch (ServerFacade.FacadeException e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }


}
