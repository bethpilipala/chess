package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveRules extends MoveRules {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        addSlidingMoves(board, position, 1, 1, moves); //Diagonally up and right
        addSlidingMoves(board, position, 1, -1, moves); //Diagonally up and left
        addSlidingMoves(board, position, -1, 1, moves); //Diagonally down and right
        addSlidingMoves(board, position, -1, -1, moves); //Diagonally down and left

        return moves;
    }
}
