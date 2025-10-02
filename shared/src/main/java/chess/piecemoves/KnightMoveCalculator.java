package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMoveCalculator implements MoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] relativeMoves = {{-1, 2}, {1, 2}, {1, -2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        return MoveCalculator.generateShortMoves(currentPosition, relativeMoves, board);
    }
}
