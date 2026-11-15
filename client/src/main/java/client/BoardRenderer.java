package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

public class BoardRenderer {
    private BoardRenderer() {}

    public static void render(ChessGame game, boolean blackPerspective) {
        ChessBoard board = game.getBoard();

        printFiles(blackPerspective);

        for (int displayRow = 0; displayRow < 8; displayRow++) {
            int row = blackPerspective ? displayRow + 1 : 8 - displayRow;
            System.out.printf("%d ", row);

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int col = blackPerspective ? 8 - displayCol : displayCol + 1;
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                boolean lightSquare = (row + col) % 2 == 1;
                String background = lightSquare
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                System.out.print(background + pieceSymbol(piece)
                        + EscapeSequences.RESET_BG_COLOR);
            }

            System.out.printf(" %d%n", row);
        }

        printFiles(blackPerspective);
    }

    private static void printFiles(boolean blackPerspective) {
        System.out.print("  ");
        for (int displayCol = 0; displayCol < 8; displayCol++) {
            int col = blackPerspective ? 8 - displayCol : displayCol + 1;
            System.out.print(" " + (char) ('a' + col - 1) + " ");
        }
        System.out.println();
    }

    private static String pieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}