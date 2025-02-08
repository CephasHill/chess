package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard gameBoard;

    private boolean blackKingMoved;
    private boolean whiteKingMoved;
    private boolean blackQueenSideRookMoved;
    private boolean whiteQueenSideRookMoved;
    private boolean blackKingSideRookMoved;
    private boolean whiteKingSideRookMoved;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
        blackKingMoved = false;
        whiteKingMoved = false;
        blackQueenSideRookMoved = false;
        whiteQueenSideRookMoved = false;
        blackKingSideRookMoved = false;
        whiteKingSideRookMoved = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        Collection<ChessMove> moves = new ArrayList<>(gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition));
        Iterator<ChessMove> iterator = moves.iterator();

        while (iterator.hasNext()) {
            ChessMove move = iterator.next();

            ChessPiece movedPiece = gameBoard.getPiece(move.getStartPosition());
            ChessPiece capturedPiece = gameBoard.getPiece(move.getEndPosition());

            ChessBoard originalBoard = gameBoard.copy();

            // Apply the move
            gameBoard.addPiece(move.getEndPosition(), movedPiece);
            gameBoard.addPiece(move.getStartPosition(), null);

            // Check if the move leaves the king in check
            if (isInCheck(movedPiece.getTeamColor())) {
                iterator.remove(); // Safe removal using iterator
            }

            // Restore original board state
            gameBoard = originalBoard.copy();
        }

//        // Castling
//        ChessPiece piece = gameBoard.getPiece(startPosition);
//
//        // White king
//        if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE) {
//            if (!whiteKingMoved && !whiteQueenSideRookMoved) {
//                boolean space = true;
//                int startRow = 1;
//                for (int i = 1; i <= 3; i++) {
//                    ChessPosition position = new ChessPosition(startRow, startPosition.getColumn() - i);
//                    if (gameBoard.getPiece(position) != null) {
//                        space = false;
//                    }
//                }
//                if (space) {
//                    moves.add(new ChessMove(startPosition, new ChessPosition(startRow, startPosition.getColumn() - 3), null));
//                }
//            }
//            if (!whiteKingMoved && !whiteKingSideRookMoved) {
//                boolean space = true;
//                int startRow = 1;
//                for (int i = 1; i <= 2; i++) {
//                    ChessPosition position = new ChessPosition(startRow, startPosition.getColumn() + i);
//                    if (gameBoard.getPiece(position) != null) {
//                        space = false;
//                    }
//                }
//                if (space) {
//                    moves.add(new ChessMove(startPosition, new ChessPosition(startRow, startPosition.getColumn() + 2), null));
//                }
//            }
//        }
//        // White queen-side rook
//        if (piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == TeamColor.WHITE && startPosition.getColumn() == 1) {
//            if (!whiteKingMoved && !whiteQueenSideRookMoved) {
//                boolean space = true;
//                int startRow = 1;
//                for (int i = 1; i <= 3; i++) {
//                    ChessPosition position = new ChessPosition(startRow, startPosition.getColumn() + i);
//                    if (gameBoard.getPiece(position) != null) {
//                        space = false;
//                    }
//                }
//                if (space) {
//                    moves.add(new ChessMove(startPosition, new ChessPosition(startRow, startPosition.getColumn() + 2), null));
//                }
//            }
//        }
//        // White king-side rook
//        if (piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == TeamColor.WHITE && startPosition.getColumn() == 8) {
//            if (!whiteKingMoved && !whiteKingSideRookMoved) {
//                boolean space = true;
//                int startRow = 1;
//                for (int i = 1; i <= 2; i++) {
//                    ChessPosition position = new ChessPosition(startRow, startPosition.getColumn() - i);
//                    if (gameBoard.getPiece(position) != null) {
//                        space = false;
//                    }
//                }
//                if (space) {
//                    moves.add(new ChessMove(startPosition, new ChessPosition(startRow, startPosition.getColumn() - 2), null));
//                }
//            }
//        }

        return moves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()) == null) {
            throw new InvalidMoveException();
        }
        if (!validMoves(move.getStartPosition()).contains(move)
                || gameBoard.getPiece(move.getStartPosition()) == null
                || gameBoard.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }
        if (gameBoard.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN
                && (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8)) {
            ChessPosition endPos = move.getEndPosition();
            ChessPiece piece = new ChessPiece(getTeamTurn(),move.getPromotionPiece());
            gameBoard.addPiece(endPos, piece);
            gameBoard.addPiece(move.getStartPosition(), null);
        }
        else {
            gameBoard.addPiece(move.getEndPosition(), gameBoard.getPiece(move.getStartPosition()));
            gameBoard.addPiece(move.getStartPosition(), null);
        }

//        // Castling checks
//        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
//        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
//            if (piece.getTeamColor() == TeamColor.BLACK) {
//                blackKingMoved = true;
//            }
//            else {
//                whiteKingMoved = true;
//            }
//        }
//        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
//            if (piece.getTeamColor() == TeamColor.BLACK) {
//                if (move.getStartPosition().getColumn() == 1) {
//                    blackQueenSideRookMoved = true;
//                }
//                else if (move.getStartPosition().getColumn() == 8) {
//                    blackKingSideRookMoved = true;
//                }
//            }
//            else {
//                if (move.getStartPosition().getColumn() == 1) {
//                    whiteQueenSideRookMoved = true;
//                }
//                else if (move.getStartPosition().getColumn() == 8) {
//                    whiteKingSideRookMoved = true;
//                }
//            }
//        }

        // Change turn
        if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
        else setTeamTurn(TeamColor.BLACK);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemyColor = (teamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
        ChessPosition kingPosition = findKingPosition(teamColor);

        if (kingPosition == null) {
            return false; // Should never happen unless the king is missing (invalid game state)
        }

        // Loop through the board looking for enemy pieces
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() == enemyColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard, position);

                    // Check if any move directly targets the king
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true; // King is under attack
                        }
                    }
                }
            }
        }

        return false;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position; // Found the king
                }
            }
        }
        return null;
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return anyValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return anyValidMoves(teamColor);
        }
        return false;
    }

    private boolean anyValidMoves(TeamColor teamColor) {
        Collection<ChessMove> allValidMoves = new ArrayList<ChessMove>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    allValidMoves.addAll(validMoves(position));
                }
            }
        }
        return allValidMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
