package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMoveCalculator implements MoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] moveDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        return MoveCalculator.generateLongMoves(currentPosition, moveDirections, board);
    }
}
