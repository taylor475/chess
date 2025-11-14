package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class BoardUi {
    ChessGame game;

    public BoardUi(ChessGame game) {
        this.game = game;
    }

    public void updateGame(ChessGame game) {
        this.game = game;
    }

    public void printBoard(ChessGame.TeamColor color, ChessPosition selectedPos) {
        StringBuilder output = new StringBuilder();
        output.append(SET_TEXT_BOLD);

        Collection<ChessMove> possibleMoves = selectedPos != null ? game.validMoves(selectedPos) : null;
        HashSet<ChessPosition> possiblePositions = HashSet.newHashSet(possibleMoves != null ? possibleMoves.size() : 0);
        if (possibleMoves != null) {
            for (ChessMove move : possibleMoves) {
                possiblePositions.add(move.getEndPosition());
            }
        }

        boolean isBlack = color == ChessGame.TeamColor.BLACK;

        output.append(firstRowBuilder(isBlack));
        for (int i = 8; i > 0; i--) {
            int row = !isBlack ? i : (i * -1) + 9;
            output.append(buildRow(row, isBlack, selectedPos, possiblePositions));
        }
        output.append(firstRowBuilder(isBlack));

        output.append(RESET_TEXT_BOLD_FAINT);
        out.println(output);
        out.printf("Turn: %s\n", game.getTeamTurn().toString());
    }

    private String firstRowBuilder(boolean isBlack) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);

        output.append("   ");
        if (isBlack) {
            for (char c = 'h'; c >= 'a'; c--) {
                output.append(" ").append(c).append(" ");
            }
        } else {
            for (char c = 'a'; c <= 'h'; c++) {
                output.append(" ").append(c).append(" ");
            }
        }
        output.append("   ");

        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);
        output.append("\n");
        return output.toString();
    }

    private String buildRow(int row, boolean isBlack, ChessPosition startingSquare, HashSet<ChessPosition> highlightedSquares) {
        StringBuilder output = new StringBuilder();
        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(" %d ".formatted(row));

        for (int i = 1; i < 9; i++) {
            int col = !isBlack ? i : (i * -1) + 9;
            output.append(squareColor(row, col, startingSquare, highlightedSquares));
            output.append(pieceChar(row, col));
        }

        output.append(SET_BG_COLOR_BLACK);
        output.append(SET_TEXT_COLOR_BLUE);
        output.append(" %d ".formatted(row));
        output.append(RESET_BG_COLOR);
        output.append(RESET_TEXT_COLOR);
        output.append("\n");
        return output.toString();
    }

    private String squareColor(int row, int col, ChessPosition startingSquare, HashSet<ChessPosition> highlightedSquares) {
        ChessPosition square = new ChessPosition(row, col);
        if (square.equals(startingSquare)) {
            return SET_BG_COLOR_BLUE;
        } else if (highlightedSquares.contains(square)) {
            return SET_BG_COLOR_YELLOW;
        } else if (Math.ceilMod(row, 2) == 0) {
            if (Math.ceilMod(col, 2) == 0) {
                return SET_BG_COLOR_DARK_GREY;
            } else {
                return SET_BG_COLOR_LIGHT_GREY;
            }
        } else {
            if (Math.ceilMod(col, 2) == 0) {
                return SET_BG_COLOR_LIGHT_GREY;
            } else {
                return SET_BG_COLOR_DARK_GREY;
            }
        }
    }

    private String pieceChar(int row, int col) {
        StringBuilder output = new StringBuilder();
        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = game.getBoard().getPiece(pos);

        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                output.append(SET_TEXT_COLOR_WHITE);

                switch (piece.getPieceType()) {
                    case KING -> output.append(" K ");
                    case QUEEN -> output.append(" Q ");
                    case BISHOP -> output.append(" B ");
                    case KNIGHT -> output.append(" N ");
                    case ROOK -> output.append(" R ");
                    case PAWN -> output.append(" P ");
                }
            } else {
                output.append(SET_TEXT_COLOR_BLACK);

                switch (piece.getPieceType()) {
                    case KING -> output.append(" K ");
                    case QUEEN -> output.append(" Q ");
                    case BISHOP -> output.append(" B ");
                    case KNIGHT -> output.append(" N ");
                    case ROOK -> output.append(" R ");
                    case PAWN -> output.append(" P ");
                }
            }
        } else {
            output.append("   ");
        }

        return output.toString();
    }
}
