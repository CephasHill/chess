package chess;

import java.util.ArrayList;
import java.util.List;
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
        // BISHOP and QUEEN
        if (type == PieceType.BISHOP || type == PieceType.QUEEN) {
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
                        up_right_clear = isClear(board, newRow, newCol);
                        if (up_right_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                !friendlyFire(board, newRow, newCol)) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
                // down-left
                if (down_left_clear) {
                    newRow = myPosition.getRow() - i;
                    newCol = myPosition.getColumn() - i;
                    if (inBounds(newRow,newCol)) {
                        down_left_clear = isClear(board, newRow, newCol);
                        if (down_left_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                !friendlyFire(board, newRow, newCol)) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
                // up-left
                if (up_left_clear) {
                    newRow = myPosition.getRow() + i;
                    newCol = myPosition.getColumn() - i;
                    if (inBounds(newRow,newCol)) {
                        up_left_clear = isClear(board, newRow, newCol);
                        if (up_left_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                !friendlyFire(board, newRow, newCol)) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
                // down-right
                if (down_right_clear) {
                    newRow = myPosition.getRow() - i;
                    newCol = myPosition.getColumn() + i;
                    if (inBounds(newRow,newCol)) {
                        down_right_clear = isClear(board, newRow, newCol);
                        if (down_right_clear) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                        else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                                !friendlyFire(board, newRow, newCol)) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), type));
                        }
                    }
                }
            }
        }
        // PAWN
        if (type == PieceType.PAWN) {
            int adv = 1;
            int start = 2;
            int promote = 8;
            if (pieceColor == ChessGame.TeamColor.BLACK) {
                adv = -1;
                start = 7;
                promote = 1;
            }
            // base moves
            ChessPosition pos1 = new ChessPosition(myPosition.getRow() + adv, myPosition.getColumn());
            ChessPosition pos2 = new ChessPosition(myPosition.getRow() + 2*adv, myPosition.getColumn());
            if (isClear(board, pos1.getRow(), pos1.getColumn())) { // check 1 space ahead
                if (pos1.getRow() != promote) {
                    moves.add(new ChessMove(myPosition, pos1, type));
                }
                else {
                    moves.add(new ChessMove(myPosition, pos1, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, pos1, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, pos1, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, pos1, PieceType.KNIGHT));
                }
                if (myPosition.getRow() == start && isClear(board, pos2.getRow(), pos2.getColumn())) { // check 2 space ahead, if @start
                    moves.add(new ChessMove(myPosition, pos2, type));
                }
            }
            // capture moves
            // row+ col+
            if (inBounds(myPosition.getRow() + adv, myPosition.getColumn() + adv)) {
                ChessPosition pos = new ChessPosition(myPosition.getRow() + adv, myPosition.getColumn() + adv);
                if (board.getPiece(pos) != null
                        && board.getPiece(pos).pieceColor != pieceColor) {
                    if (pos.getRow() != promote) {
                        moves.add(new ChessMove(myPosition, pos, type));
                    }
                    else {
                        moves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
                    }
                }
            }
            // row+ col-
            if (inBounds(myPosition.getRow() + adv, myPosition.getColumn() - adv)) {
                ChessPosition pos = new ChessPosition(myPosition.getRow() + adv, myPosition.getColumn() - adv);
                if (board.getPiece(pos) != null
                        && board.getPiece(pos).pieceColor != pieceColor) {
                    if (pos.getRow() != promote) {
                        moves.add(new ChessMove(myPosition, pos, type));
                    }
                    else {
                        moves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
                    }
                }
            }
        }
        // KNIGHT
        if (type == PieceType.KNIGHT) {
            List<ChessPosition> positions = new ArrayList<ChessPosition>();
            if (inBounds(myPosition.getRow() + 2, myPosition.getColumn() + 1) && !friendlyFire(board, myPosition.getRow() + 2, myPosition.getColumn() + 1)) { // up right
                positions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1));
            }
            if (inBounds(myPosition.getRow() + 1, myPosition.getColumn() + 2) && !friendlyFire(board, myPosition.getRow() + 1, myPosition.getColumn() + 2)) { // right up
                positions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2));
            }
            if (inBounds(myPosition.getRow() - 1, myPosition.getColumn() + 2) && !friendlyFire(board, myPosition.getRow() - 1, myPosition.getColumn() + 2)) { // right down
                positions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2));
            }
            if (inBounds(myPosition.getRow() - 2, myPosition.getColumn() + 1) && !friendlyFire(board, myPosition.getRow() - 2, myPosition.getColumn() + 1)) { // down right
                positions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1));
            }
            if (inBounds(myPosition.getRow() - 2, myPosition.getColumn() - 1) && !friendlyFire(board, myPosition.getRow() - 2, myPosition.getColumn() - 1)) { // down left
                positions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1));
            }
            if (inBounds(myPosition.getRow() - 1, myPosition.getColumn() - 2) && !friendlyFire(board, myPosition.getRow() - 1, myPosition.getColumn() - 2)) { // left down
                positions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2));
            }
            if (inBounds(myPosition.getRow() + 1, myPosition.getColumn() - 2) && !friendlyFire(board, myPosition.getRow() + 1, myPosition.getColumn() - 2)) { // left up
                positions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2));
            }
            if (inBounds(myPosition.getRow() + 2, myPosition.getColumn() - 1) && !friendlyFire(board, myPosition.getRow() + 2, myPosition.getColumn() - 1)) { // up left
                positions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1));
            }
            // add all possible moves in positions list
            for (ChessPosition pos : positions) {
                moves.add(new ChessMove(myPosition, pos, type));
            }
        }
        // ROOK and QUEEN
        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            boolean up_clear = true;
            boolean down_clear = true;
            boolean left_clear = true;
            boolean right_clear = true;
            for (int i = 1 ; i <= 8 ; i++) {
                // right moves
                if (inBounds(myPosition.getRow(), myPosition.getColumn() + i) && right_clear) {
                    if (isClear(board, myPosition.getRow(), myPosition.getColumn() + i)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i), type));
                    }
                    else {
                        if (!friendlyFire(board, myPosition.getRow(), myPosition.getColumn() + i)) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i), type));
                        }
                        right_clear = false;
                    }
                }
                // left moves
                if (inBounds(myPosition.getRow(), myPosition.getColumn() - i) && left_clear) {
                    if (isClear(board, myPosition.getRow(), myPosition.getColumn() - i)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i), type));
                    }
                    else {
                        if (!friendlyFire(board, myPosition.getRow(), myPosition.getColumn() - i)) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i), type));
                        }
                        left_clear = false;
                    }
                }
                // up moves
                if (inBounds(myPosition.getRow() + i, myPosition.getColumn()) && up_clear) {
                    if (isClear(board, myPosition.getRow() + i, myPosition.getColumn())) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()), type));
                    }
                    else {
                        if (!friendlyFire(board, myPosition.getRow() + i, myPosition.getColumn())) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()), type));
                        }
                        up_clear = false;
                    }
                }
                // down moves
                if (inBounds(myPosition.getRow() - i, myPosition.getColumn()) && down_clear) {
                    if (isClear(board, myPosition.getRow() - i, myPosition.getColumn())) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()), type));
                    }
                    else {
                        if (!friendlyFire(board, myPosition.getRow() - i, myPosition.getColumn())) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()), type));
                        }
                        down_clear = false;
                    }
                }
            }
        }
        return moves;
    }

    private boolean isClear(ChessBoard board, int newRow, int newCol) {
        return board.getPiece(new ChessPosition(newRow, newCol)) == null;
    }
    private boolean inBounds(int newRow, int newCol) {
        return newRow >= 1 && newRow < 9 && newCol >= 1 && newCol < 9;
    }
    private boolean friendlyFire(ChessBoard board, int row, int col) {
        if (board.getPiece(new ChessPosition(row,col)) != null) {
            return board.getPiece(new ChessPosition(row,col)).getTeamColor() == pieceColor;
        }
        else return false;
    }
}
