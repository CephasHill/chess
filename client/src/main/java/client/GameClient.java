package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;

import java.util.Arrays;

import static client.EscapeSequences.RESET;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class GameClient {
    private final String storageType = "sql";

    public String eval(String input, AuthData authData, GameData data) {
        try {
            String auth = authData.authToken();
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "print" -> printBoard(data, authData);
                case "redraw" -> printBoard(data, authData);
                case "leave" -> leave(data, authData);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String leave(GameData data, AuthData authData) {
        return "leaving...";
    }

    public String help() {
        return """
                The following are valid commands:
                - help
                - print / redraw
                - leave
                - quit
                """;
    }
    public String printBoard(GameData gameData, AuthData authData) {
        ChessBoard board = gameData.game().getBoard();
        out.print(ERASE_SCREEN);
        out.print(RESET);

        // Determine perspective: White (8 to 1) or Black (1 to 8)
        boolean isBlack = authData.username() != null && authData.username().equals(gameData.blackUsername());
        int startRow = isBlack ? 1 : 8;
        int endRow = isBlack ? 8 : 1;
        int rowStep = isBlack ? 1 : -1;
        int startCol = isBlack ? 8 : 1;
        int endCol = isBlack ? 1 : 8;
        int colStep = isBlack ? -1 : 1;

        // Print column labels (always a to h)
        out.print("   ");
        for (int col = startCol; isBlack ? col >= endCol : col <= endCol; col += colStep) {
            char colLabel = (char) ('a' + (col - 1)); // Convert 1-8 to a-h
            out.print(" " + colLabel + " ");
        }
        out.print("\n");

        for (int row = startRow; isBlack ? row <= endRow : row >= endRow; row += rowStep) {
            out.print(" " + row + " ");
            for (int col = startCol; isBlack ? col >= endCol : col <= endCol; col += colStep) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                String pieceChar = (piece == null) ? " " : getPieceChar(piece);

                if ((row + col) % 2 == 0) { // Dark squares (a1, c1, etc.)
                    out.print(SET_BG_COLOR_DARK_GREEN);
                    out.print(SET_TEXT_COLOR_WHITE);
                } else { // Light squares (b1, d1, etc.)
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(SET_TEXT_COLOR_BLACK);
                }
                out.print(" " + pieceChar + " ");
                out.print(RESET);
            }
            out.print("\n");
        }

        out.print(RESET);
        out.flush();
        return "";
    }

    private String getPieceChar(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();
        return switch (type) {
            case KING -> color == ChessGame.TeamColor.WHITE ? "K" : "k";
            case QUEEN -> color == ChessGame.TeamColor.WHITE ? "Q" : "q";
            case BISHOP -> color == ChessGame.TeamColor.WHITE ? "B" : "b";
            case KNIGHT -> color == ChessGame.TeamColor.WHITE ? "N" : "n";
            case ROOK -> color == ChessGame.TeamColor.WHITE ? "R" : "r";
            case PAWN -> color == ChessGame.TeamColor.WHITE ? "P" : "p";
        };
    }
}
