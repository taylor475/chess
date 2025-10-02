package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] boardLayout;

    public ChessBoard() {
        boardLayout = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardLayout[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardLayout[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Gets the team of a chess piece on the chessboard
     *
     * @param position The position of the piece to check the team of
     * @return Either the team of the piece at the position, or null if no piece
     * is at that position
     */
    public ChessGame.TeamColor getPositionTeam(ChessPosition position) {
        ChessPiece currentPiece = getPiece(position);
        if (currentPiece != null) {
            return currentPiece.getTeamColor();
        } else {
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardLayout = new ChessPiece[8][8];

        ChessPiece.PieceType[] backrow = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        // Set the White pieces
        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, backrow[i - 1]));
        }
        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // Set the Black pieces
        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, backrow[i - 1]));
        }
        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(boardLayout, that.boardLayout);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardLayout);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            output.append("|");
            for (int col = 0; col < 8; col++) {
                output.append(boardLayout[row][col] != null ? boardLayout[row][col].toString() : " ");
                output.append("|");
            }
            output.append("\n");
        }
        return output.toString();
    }
}
