package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public interface MoveCalculator {

    static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        return null;
    }

    static boolean isOnBoard(ChessPosition position) {
        return (position.getRow() >= 1 && position.getRow() <= 8) && (position.getColumn() >= 1 && position.getColumn() <= 8);
    }

    static HashSet<ChessMove> generateShortMoves(ChessPosition currentPosition, int[][] relativeMoves, ChessBoard board) {
        HashSet<ChessMove> moves = HashSet.newHashSet(8);

        int currentX = currentPosition.getColumn();
        int currentY = currentPosition.getRow();

        ChessGame.TeamColor team = board.getPositionTeam(currentPosition);
        for (int[] relativeMove : relativeMoves) {
            ChessPosition possiblePosition = new ChessPosition(currentY + relativeMove[1], currentX + relativeMove[0]);
            if (isOnBoard(possiblePosition) && board.getPositionTeam(possiblePosition) != team) {
                moves.add(new ChessMove(currentPosition, possiblePosition, null));
            }
        }

        return moves;
    }

    static HashSet<ChessMove> generateLongMoves(ChessPosition currentPosition, int[][] moveDirections, ChessBoard board) {
        HashSet<ChessMove> moves = HashSet.newHashSet(27);

        int currentX = currentPosition.getColumn();
        int currentY = currentPosition.getRow();

        ChessGame.TeamColor team = board.getPositionTeam(currentPosition);
        for (int[] direction : moveDirections) {
            boolean obstructed = false;
            int i = 1;
            while (!obstructed) {
                ChessPosition possiblePosition = new ChessPosition(currentY + direction[1] * i, currentX + direction[0] * i);
                if (!isOnBoard(possiblePosition)) {
                    obstructed = true;
                } else if (board.getPiece(possiblePosition) == null) {
                    moves.add(new ChessMove(currentPosition, possiblePosition, null));
                } else if (board.getPositionTeam(possiblePosition) != team) {
                    moves.add(new ChessMove(currentPosition, possiblePosition, null));
                    obstructed = true;
                } else if (board.getPositionTeam(possiblePosition) == team) {
                    obstructed = true;
                } else {
                    obstructed = true;
                }
                i++;
            }
        }

        return moves;
    }
}
