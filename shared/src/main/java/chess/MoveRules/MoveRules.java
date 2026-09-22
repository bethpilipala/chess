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

}