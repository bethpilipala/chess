package chess.MoveRules;

import chess.*;

import java.util.Collection;

public abstract class MoveRules {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

}