package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveRules extends MoveRules {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSingleMoves(board, position, 2, 1, moves);
        addSingleMoves(board, position, 2, -1, moves);
        addSingleMoves(board, position, -2, 1, moves);
        addSingleMoves(board, position, -2, -1, moves);
        addSingleMoves(board, position, 1, 2, moves);
        addSingleMoves(board, position, 1, -2, moves);
        addSingleMoves(board, position, -1, 2, moves);
        addSingleMoves(board, position, -1, -2, moves);

        return moves;
    }

}