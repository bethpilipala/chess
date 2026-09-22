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

        //Set based on color
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

        //Add simple forward moves
        addSingleMoves(board, position, direction, 0, moves);
        if(row == startingRow)
            addSingleMoves(board, position, (direction*2), 0, moves);

        return moves;
    }
}