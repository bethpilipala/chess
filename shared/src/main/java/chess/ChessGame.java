package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard(); //put all the pieces on the board! (empty otherwise)
        teamTurn = TeamColor.WHITE; //Chess always starts with white

    }

    public static void main(String[] args) { //Included for testing purposes and will be deleted!
        ChessBoard testBoard = new ChessBoard();

        // White king at (1,5)
        testBoard.addPiece(
                new ChessPosition(1, 5),
                new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING)
        );

        // Black rook at (1,1)
        testBoard.addPiece(
                new ChessPosition(1, 1),
                new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK)
        );

        ChessGame game = new ChessGame();

        System.out.println(game.isInCheck(testBoard, TeamColor.WHITE));
        System.out.println(game.isInCheck(testBoard, TeamColor.BLACK));
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null)
            return null;

        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : pieceMoves) {
            ChessBoard testBoard = board.makeTestingBoard();
            makeTestMove(testBoard, move);
            if (!isInCheck(testBoard, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        //checking move prerequisites
        if (piece == null)
            throw new InvalidMoveException("No piece at start position");
        if (piece.getTeamColor() != teamTurn)
            throw new InvalidMoveException("Not your turn");

        //is it a valid move?
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (!legalMoves.contains(move))
            throw new InvalidMoveException("Move is not legal");

        makeTestMove(this.board, move); //now implementing that test move

        //end move, now switching turns
        if (teamTurn == TeamColor.WHITE)
            teamTurn = TeamColor.BLACK;
        else
            teamTurn = TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }

    private ChessPosition findKing(ChessBoard board, TeamColor color){
        //Find where the king is at on the board for a particular team
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == color && piece.getPieceType() == ChessPiece.PieceType.KING)
                    return position;
            }
        }
        return null;//if king not found (shouldn't happen)
    }

    private static void makeTestMove(ChessBoard board, ChessMove move){
        //Move a piece on the test board (does not validate the move)

        ChessPiece piece = board.getPiece(move.getStartPosition()); //get the piece at the start
        board.addPiece(move.getStartPosition(), null); //clear the start square

        //Handling promotion pieces
        if (move.getPromotionPiece() != null)
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());

        board.addPiece(move.getEndPosition(), piece);
    }

    private boolean isInCheck(ChessBoard board, TeamColor color){
        //using method overloading principle so I can check on my copy of the board as well
        ChessPosition kingPos = findKing(board, color);
        if (kingPos == null)
            return false; //shouldnt exist but checking anyways

        //parse over the board and check if an enemy can get the king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() != color)
                {
                    for (ChessMove move : piece.pieceMoves(board, position)) {
                        if (move.getEndPosition().equals(kingPos))
                            return true;
                    }

                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
