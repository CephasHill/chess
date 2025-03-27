package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;

import static client.EscapeSequences.RESET;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class GameClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final String storageType = "sql";
    private String auth = null;
    public GameClient(int port) {
        server = new ServerFacade(port);
    }
    public String eval(String input, String auth, GameData data) {
        try {
            this.auth = auth;
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "print" -> printBoard(data);
//                case "list" -> listGames(params);
//                case "create" -> createGame(params);
//                case "join" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public String help() {
        return """
                The following are valid commands:
                - help
                - quit
                """;
    }
    public String printBoard(GameData data) {
        ChessBoard board = data.game().getBoard();
        StringBuilder sb = new StringBuilder();
        out.print(ERASE_SCREEN);

        // print column labels
        out.print("  ");
        for (char col = 'a'; col <= 'h'; col++) {
            out.print(" " + col + " ");
        }
        out.print("\n");

        for (int row = 1; row <= 8; row++) {
            out.print(row + " ");
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                String pieceChar = (piece == null) ? EMPTY : getPieceChar(piece);

                if ((row + col) % 2 == 0) {
                    //dark
                    out.print(SET_BG_COLOR_DARK_GREEN);
                } else {
                    out.print(SET_BG_COLOR_WHITE);
                }
                out.print(EMPTY + pieceChar + EMPTY);
                sb.append(EMPTY).append(pieceChar).append(EMPTY);
            }
            out.print(RESET);
            out.print("\n");
            sb.append("\n");
        }

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        return sb.toString();
    }

    private String getPieceChar(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();
        return switch (type) {
            case KING -> color == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> color == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> color == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> color == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> color == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> color == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}
