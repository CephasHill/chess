package chess;

import java.util.ArrayList;
import java.util.Collection;
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

    public ChessPiece copy() {
        return new ChessPiece(this.getTeamColor(), this.getPieceType());
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        // BISHOP and QUEEN
        if (type == PieceType.BISHOP || type == PieceType.QUEEN) {
            getBishopMoves(board, myPosition, moves);
        }
        // PAWN
        if (type == PieceType.PAWN) {
            getPawnMoves(board, myPosition, moves);
        }
        // KNIGHT
        if (type == PieceType.KNIGHT) {
            getKnightMoves(board, myPosition, moves);
        }
        // ROOK and QUEEN
        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            getRookMoves(board, myPosition, moves);
        }
        // KING
        if (type == PieceType.KING) {
            getKingMoves(board, myPosition, moves);
        }
        return moves;
    }

    private void getKingMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        // down left
        if (inBounds(myPosition.getRow() - 1, myPosition.getColumn() - 1)
                && enemy(board, myPosition.getRow() - 1, myPosition.getColumn() - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
        }
        // left
        if (inBounds(myPosition.getRow(), myPosition.getColumn() - 1)
                && enemy(board, myPosition.getRow(), myPosition.getColumn() - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null));
        }
        // up left
        if (inBounds(myPosition.getRow() + 1, myPosition.getColumn() - 1)
                && enemy(board, myPosition.getRow() + 1, myPosition.getColumn() - 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
        }
        // up
        if (inBounds(myPosition.getRow() + 1, myPosition.getColumn())
                && enemy(board, myPosition.getRow() + 1, myPosition.getColumn())) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
        }
        // up right
        if (inBounds(myPosition.getRow() + 1, myPosition.getColumn() + 1)
                && enemy(board, myPosition.getRow() + 1, myPosition.getColumn() + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
        }
        // right
        if (inBounds(myPosition.getRow(), myPosition.getColumn() + 1)
                && enemy(board, myPosition.getRow(), myPosition.getColumn() + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null));
        }
        // down right
        if (inBounds(myPosition.getRow() - 1, myPosition.getColumn() + 1)
                && enemy(board, myPosition.getRow() - 1, myPosition.getColumn() + 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
        }
        // down
        if (inBounds(myPosition.getRow() - 1, myPosition.getColumn())
                && enemy(board, myPosition.getRow() - 1, myPosition.getColumn())) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
        }
    }

    private void getRookMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        boolean upClear = true;
        boolean downClear = true;
        boolean leftClear = true;
        boolean rightClear = true;
        for (int i = 1 ; i <= 8 ; i++) {
            // right moves
            if (inBounds(myPosition.getRow(), myPosition.getColumn() + i) && rightClear) {
                if (isClear(board, myPosition.getRow(), myPosition.getColumn() + i)) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i), null));
                }
                else {
                    if (enemy(board, myPosition.getRow(), myPosition.getColumn() + i)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i), null));
                    }
                    rightClear = false;
                }
            }
            // left moves
            if (inBounds(myPosition.getRow(), myPosition.getColumn() - i) && leftClear) {
                if (isClear(board, myPosition.getRow(), myPosition.getColumn() - i)) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i), null));
                }
                else {
                    if (enemy(board, myPosition.getRow(), myPosition.getColumn() - i)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i), null));
                    }
                    leftClear = false;
                }
            }
            // up moves
            if (inBounds(myPosition.getRow() + i, myPosition.getColumn()) && upClear) {
                if (isClear(board, myPosition.getRow() + i, myPosition.getColumn())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()), null));
                }
                else {
                    if (enemy(board, myPosition.getRow() + i, myPosition.getColumn())) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()), null));
                    }
                    upClear = false;
                }
            }
            // down moves
            if (inBounds(myPosition.getRow() - i, myPosition.getColumn()) && downClear) {
                if (isClear(board, myPosition.getRow() - i, myPosition.getColumn())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()), null));
                }
                else {
                    if (enemy(board, myPosition.getRow() - i, myPosition.getColumn())) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()), null));
                    }
                    downClear = false;
                }
            }
        }
    }

    private void getKnightMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        List<ChessPosition> positions = new ArrayList<ChessPosition>();
        if (inBounds(myPosition.getRow() + 2, myPosition.getColumn() + 1) &&
                enemy(board, myPosition.getRow() + 2, myPosition.getColumn() + 1)) { // up right
            positions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1));
        }
        if (inBounds(myPosition.getRow() + 1, myPosition.getColumn() + 2) &&
                enemy(board, myPosition.getRow() + 1, myPosition.getColumn() + 2)) { // right up
            positions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2));
        }
        if (inBounds(myPosition.getRow() - 1, myPosition.getColumn() + 2) &&
                enemy(board, myPosition.getRow() - 1, myPosition.getColumn() + 2)) { // right down
            positions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2));
        }
        if (inBounds(myPosition.getRow() - 2, myPosition.getColumn() + 1) &&
                enemy(board, myPosition.getRow() - 2, myPosition.getColumn() + 1)) { // down right
            positions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1));
        }
        if (inBounds(myPosition.getRow() - 2, myPosition.getColumn() - 1) &&
                enemy(board, myPosition.getRow() - 2, myPosition.getColumn() - 1)) { // down left
            positions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1));
        }
        if (inBounds(myPosition.getRow() - 1, myPosition.getColumn() - 2) &&
                enemy(board, myPosition.getRow() - 1, myPosition.getColumn() - 2)) { // left down
            positions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2));
        }
        if (inBounds(myPosition.getRow() + 1, myPosition.getColumn() - 2) &&
                enemy(board, myPosition.getRow() + 1, myPosition.getColumn() - 2)) { // left up
            positions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2));
        }
        if (inBounds(myPosition.getRow() + 2, myPosition.getColumn() - 1) &&
                enemy(board, myPosition.getRow() + 2, myPosition.getColumn() - 1)) { // up left
            positions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1));
        }
        // add all possible moves in positions list
        for (ChessPosition pos : positions) {
            moves.add(new ChessMove(myPosition, pos, null));
        }
    }

    private void getPawnMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
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
            promotePawn(myPosition, moves, promote, pos1);
            if (myPosition.getRow() == start && isClear(board, pos2.getRow(), pos2.getColumn())) { // check 2 space ahead, if @start
                moves.add(new ChessMove(myPosition, pos2, null));
            }
        }
        // capture moves
        // row+ col+
        if (inBounds(myPosition.getRow() + adv, myPosition.getColumn() + adv)) {
            ChessPosition pos = new ChessPosition(myPosition.getRow() + adv, myPosition.getColumn() + adv);
            if (board.getPiece(pos) != null
                    && board.getPiece(pos).pieceColor != pieceColor) {
                promotePawn(myPosition, moves, promote, pos);
            }
        }
        // row+ col-
        if (inBounds(myPosition.getRow() + adv, myPosition.getColumn() - adv)) {
            ChessPosition pos = new ChessPosition(myPosition.getRow() + adv, myPosition.getColumn() - adv);
            if (board.getPiece(pos) != null
                    && board.getPiece(pos).pieceColor != pieceColor) {
                promotePawn(myPosition, moves, promote, pos);
            }
        }
    }

    private void getBishopMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        boolean upRightClear = true;
        boolean downLeftClear = true;
        boolean upLeftClear = true;
        boolean downRightClear = true;
        for (int i = 1; i < 8; i++) {
            // up-right
            int newRow = 0;
            int newCol = 0;
            if (upRightClear) {
                newRow = myPosition.getRow() + i;
                newCol = myPosition.getColumn() + i;
                if (inBounds(newRow,newCol)) {
                    upRightClear = isClear(board, newRow, newCol);
                    if (upRightClear) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                    else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                            enemy(board, newRow, newCol)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                }
            }
            // down-left
            if (downLeftClear) {
                newRow = myPosition.getRow() - i;
                newCol = myPosition.getColumn() - i;
                if (inBounds(newRow,newCol)) {
                    downLeftClear = isClear(board, newRow, newCol);
                    if (downLeftClear) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                    else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                            enemy(board, newRow, newCol)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                }
            }
            // up-left
            if (upLeftClear) {
                newRow = myPosition.getRow() + i;
                newCol = myPosition.getColumn() - i;
                if (inBounds(newRow,newCol)) {
                    upLeftClear = isClear(board, newRow, newCol);
                    if (upLeftClear) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                    else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                            enemy(board, newRow, newCol)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                }
            }
            // down-right
            if (downRightClear) {
                newRow = myPosition.getRow() - i;
                newCol = myPosition.getColumn() + i;
                if (inBounds(newRow,newCol)) {
                    downRightClear = isClear(board, newRow, newCol);
                    if (downRightClear) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                    else if (board.getPiece(new ChessPosition(newRow, newCol)) != null &&
                            enemy(board, newRow, newCol)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                }
            }
        }
    }

    private void promotePawn(ChessPosition myPosition, ArrayList<ChessMove> moves, int promote, ChessPosition pos1) {
        if (pos1.getRow() != promote) {
            moves.add(new ChessMove(myPosition, pos1, null));
        }
        else {
            moves.add(new ChessMove(myPosition, pos1, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, pos1, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, pos1, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, pos1, PieceType.KNIGHT));
        }
    }

    private boolean isClear(ChessBoard board, int newRow, int newCol) {
        return board.getPiece(new ChessPosition(newRow, newCol)) == null;
    }

    private boolean inBounds(int newRow, int newCol) {
        return newRow >= 1 && newRow < 9 && newCol >= 1 && newCol < 9;
    }

    private boolean enemy(ChessBoard board, int row, int col) {
        if (board.getPiece(new ChessPosition(row,col)) != null) {
            return board.getPiece(new ChessPosition(row, col)).getTeamColor() != pieceColor;
        }
        else {
            return true;
        }
    }
}
