package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int column;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.column = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", row, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof ChessPosition))
            return false;

        ChessPosition other = (ChessPosition) obj;

        // They are equal if they have the same row and column.
        if (row == other.row && column == other.column)
            return true;

        return false;
    }
}
