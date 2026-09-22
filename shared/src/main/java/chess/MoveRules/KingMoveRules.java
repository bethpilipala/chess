package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveRules extends MoveRules {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSingleMoves(board, position, 0, -1, moves); //left
        addSingleMoves(board, position, 0, 1, moves); //right
        addSingleMoves(board, position, 1, 0, moves); //up
        addSingleMoves(board, position, -1, 0, moves); //down
        addSingleMoves(board, position, 1, -1, moves); //up and left
        addSingleMoves(board, position, -1, -1, moves); //down and left
        addSingleMoves(board, position, 1, 1, moves); //up and right
        addSingleMoves(board, position, -1, 1, moves); //down and right

        return moves;
    }

}