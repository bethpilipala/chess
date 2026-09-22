package chess.MoveRules;

import chess.*;

import java.util.Collection;

public abstract class MoveRules {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    protected boolean inBounds(int row, int column) {
        if (row >= 1 && row <= 8 && column >= 1 && column <= 8)
            return true;
        else
            return false;
    }

    protected boolean isEnemy(ChessBoard board, ChessPosition start, ChessPosition end){
        ChessPiece startPiece = board.getPiece(start);
        ChessPiece endPiece = board.getPiece(end);

        if(endPiece == null) //Not occupied
            return false;

        if(startPiece.getTeamColor() != endPiece.getTeamColor()) //opposite color
            return true;
        else
            return false;
    }

    protected void addSlidingMoves(ChessBoard board, ChessPosition start, int rowChange, int colChange, Collection<ChessMove> moves){
        ChessPiece startPiece = board.getPiece(start);
    }

}