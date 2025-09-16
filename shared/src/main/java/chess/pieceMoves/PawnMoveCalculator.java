package chess.pieceMoves;

import chess.*;

import java.util.HashSet;

import static chess.pieceMoves.MoveCalculator.*;

public class PawnMoveCalculator {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        HashSet<ChessMove> moves = HashSet.newHashSet(16);

        int currentX = currentPosition.getColumn();
        int currentY = currentPosition.getRow();
        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{null};

        ChessGame.TeamColor team = board.getPositionTeam(currentPosition);
        int moveIncrement = team == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean willPromote = (team == ChessGame.TeamColor.WHITE && currentY == 7) || (team == ChessGame.TeamColor.BLACK && currentY == 2);
        if (willPromote) {
            promotionPieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK};
        }

        for (ChessPiece.PieceType promotionPiece: promotionPieces) {
            // Check for forward movement
            ChessPosition forwardPosition = new ChessPosition(currentY + moveIncrement, currentX);
            if (isValidSquare(forwardPosition) && board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currentPosition, forwardPosition, null));
            }
            // Check for left attack
            ChessPosition leftPosition = new ChessPosition(currentY + moveIncrement, currentX - 1);
            if (isValidSquare(leftPosition) && board.getPiece(leftPosition) != null && board.getPositionTeam(leftPosition) != team) {
                moves.add(new ChessMove(currentPosition, leftPosition, null));
            }
            // Check for right attack
            ChessPosition rightPosition = new ChessPosition(currentY + moveIncrement, currentX);
            if (isValidSquare(rightPosition) && board.getPiece(rightPosition) != null && board.getPositionTeam(rightPosition) != team) {
                moves.add(new ChessMove(currentPosition, rightPosition, null));
            }
            // Check for first double move
            ChessPosition doubleForwardPosition = new ChessPosition(currentY + moveIncrement * 2, currentX);
            if (isValidSquare(doubleForwardPosition) && ((team == ChessGame.TeamColor.WHITE && currentY == 2) || (team == ChessGame.TeamColor.BLACK && currentY == 7)) && board.getPiece(forwardPosition) == null && board.getPiece(doubleForwardPosition) == null) {
                moves.add(new ChessMove(currentPosition, doubleForwardPosition, null));
            }
        }

        return moves;
    }
}
