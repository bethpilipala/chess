package chess.MoveRules;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveRules extends MoveRules {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece pawn = board.getPiece(position);

        int direction; //White v black
        int startingRow;
        int promotionRow;

        //Set values based on color
        if(pawn.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startingRow = 2;
            promotionRow = 8;
        }
        else {
            direction = -1;
            startingRow = 7;
            promotionRow = 1;
        }

        int row = position.getRow();
        int col = position.getColumn();

        //Forward stepping logic
        int firstStepRow = row + direction;
        if(inBounds(firstStepRow, col)){
            ChessPosition firstStep = new ChessPosition(firstStepRow, col);

            if(board.getPiece(firstStep) == null) {//empty space
                addPawnMove(moves, position, firstStep, promotionRow);

                int secondStepRow = row + (direction * 2); //only valid if first step is also valid
                if(row == startingRow && inBounds(secondStepRow, col)){
                    ChessPosition secondStep = new ChessPosition(secondStepRow, col);

                    if(board.getPiece(secondStep) == null) //empty space
                        addPawnMove(moves, position, secondStep, promotionRow);
                }
            }
        }

        //Capturing logic
        int captureRow = row + direction;

        if(inBounds(captureRow, col - 1)){ //Left capturing
            ChessPosition leftPosition = new ChessPosition(captureRow, col-1);

            if(isEnemy(board, position, leftPosition))
                addPawnMove(moves,position, leftPosition, promotionRow);
        }

        if(inBounds(captureRow, col + 1)) { //Right capturing
            ChessPosition rightPosition = new ChessPosition(captureRow, col + 1);

            if(isEnemy(board, position, rightPosition))
                addPawnMove(moves, position, rightPosition, promotionRow);
        }

        return moves;
    }

    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow){
        //This primarily checks if it is a promotion piece and then adds the move

        if(end.getRow() == promotionRow){
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        }
        else
            moves.add(new ChessMove(start, end,null));
    }
}