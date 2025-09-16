package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KingMoveCalculator implements MoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] relativeMoves = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        return MoveCalculator.generateShortMoves(currentPosition, relativeMoves, board);
    }
}
