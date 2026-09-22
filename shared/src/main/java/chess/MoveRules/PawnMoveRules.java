package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveRules extends MoveRules {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSingleMoves(board, position, 1, 0, moves); //up
        addSingleMoves(board, position, 2, 0, moves); //up 2
        addSingleMoves(board, position, 1, -1, moves); //up and left
        addSingleMoves(board, position, 1, 1, moves); //up and right

        return moves;
    }
}