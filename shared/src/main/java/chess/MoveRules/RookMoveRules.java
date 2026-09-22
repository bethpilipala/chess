package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveRules extends MoveRules {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSlidingMoves(board, position, 1, 0, moves); //Up
        addSlidingMoves(board, position, 0, 1, moves); //Right
        addSlidingMoves(board, position, -1, 0, moves); //Down
        addSlidingMoves(board, position, 0, -1, moves); //Left

        return moves;
    }

}