package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class BishopMoveCalculator implements MoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] moveDirections = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        return MoveCalculator.generateLongMoves(currentPosition, moveDirections, board);
    }
}
