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
        //This will check valid moves for both lateral and horizontal movements!
        //Will be used for Rooks, Bishops, and Queens

        int row = start.getRow() + rowChange;
        int col = start.getColumn() + colChange;

        while(inBounds(row, col)){
            ChessPosition end = new ChessPosition(row, col);
            ChessPiece endPiece = board.getPiece(end);

            if(endPiece == null) //If space is not occupied
                moves.add(new ChessMove(start, end, null)); //TODO: might need more promotion piece logic here?
            else { //If it IS occupied
                if(isEnemy(board, start, end))
                    moves.add(new ChessMove(start, end, null));
                break; //Can't move any further past a piece
            }

            row += rowChange;
            col += colChange;
        }
    }

    protected void addSingleMoves(ChessBoard board, ChessPosition start, int rowChange, int colChange, Collection<ChessMove> moves){
        //This will check if a single move is valid
        //Will be used for Pawns, Knights, and Kings

        int row = start.getRow() + rowChange;
        int col = start.getColumn() + colChange;

        if(!inBounds(row, col))
            return;

        ChessPosition end = new ChessPosition(row, col);
        ChessPiece endPiece = board.getPiece(end);

        if(endPiece == null)
            moves.add(new ChessMove(start, end, null));
        else {
            if (isEnemy(board, start, end))
                moves.add(new ChessMove(start, end, null));
        }

    }

}