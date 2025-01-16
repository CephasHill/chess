package chess;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public ArrayList<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        // BISHOP
        if (type == PieceType.BISHOP) {
            boolean up_right_clear = true;
            boolean down_left_clear = true;
            boolean up_left_clear = true;
            boolean down_right_clear = true;
            for (int i = 1; i < 8; i++) {
                // up-right
                int newRow = 0;
                int newCol = 0;
                if (up_right_clear) {
                    newRow = myPosition.getRow() + i;
                    newCol = myPosition.getColumn() + i;
                    if (inBounds(newRow,newCol)) {
                        up_right_clear = isClear(board, myPosition, moves, up_right_clear, newRow, newCol);
                        if (up_right_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() != pieceColor &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getPieceType() != PieceType.KING) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
                // down-left
                if (down_left_clear) {
                    newRow = myPosition.getRow() - i;
                    newCol = myPosition.getColumn() - i;
                    if (inBounds(newRow,newCol)) {
                        down_left_clear = isClear(board, myPosition, moves, down_left_clear, newRow, newCol);
                        if (down_left_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() != pieceColor &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getPieceType() != PieceType.KING) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
                // up-left
                if (up_left_clear) {
                    newRow = myPosition.getRow() + i;
                    newCol = myPosition.getColumn() - i;
                    if (inBounds(newRow,newCol)) {
                        up_left_clear = isClear(board, myPosition, moves, up_left_clear, newRow, newCol);
                        if (up_left_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() != pieceColor &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getPieceType() != PieceType.KING) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
                // down-right
                if (down_right_clear) {
                    newRow = myPosition.getRow() - i;
                    newCol = myPosition.getColumn() + i;
                    if (inBounds(newRow,newCol)) {
                        down_right_clear = isClear(board, myPosition, moves, down_right_clear, newRow, newCol);
                        if (down_right_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getTeamColor() != pieceColor &&
                                board.getPiece(new ChessPosition(newRow, newCol)).getPieceType() != PieceType.KING) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
            }
        }
        // PAWN
        if (type == PieceType.PAWN) {
            int adv = 1;
            if (pieceColor == ChessGame.TeamColor.BLACK) {
                adv = -1;
            }
            if (myPosition.getRow() == 1) {
                if (isClear(board, myPosition, moves, true, myPosition.getRow() + adv, myPosition.getColumn()) &&
                        isClear(board, myPosition, moves, true, myPosition.getRow() + 2*adv, myPosition.getColumn())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2*adv, myPosition.getColumn()), type));
                }
            }
            if (isClear(board, myPosition, moves, true, myPosition.getRow() + adv, myPosition.getColumn())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + adv, myPosition.getColumn()), type));
            }
        }
        return moves;
    }

    private boolean isClear(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves, boolean clear, int newRow, int newCol) {
        return board.getPiece(new ChessPosition(newRow, newCol)) == null;
    }
    private boolean inBounds(int newRow, int newCol) {
        return newRow >= 1 && newRow < 9 && newCol >= 1 && newCol < 9;
    }
}
