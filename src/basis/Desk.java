package basis;

public class Desk {
    public static final char START_LETTER = 'a';
    protected int dim;
    protected char endLetter;
    protected Position[][] pos;

    public Desk(Desk newDesk) {
        this(newDesk.dim);
        boolean flag = false;

        // Copy positions
        for (char c = Desk.START_LETTER; c <= this.endLetter; c++) {
            for (int r = 1; r <= dim; r++) {
                pos[c - Desk.START_LETTER][r - 1] = new Position(newDesk.getPositionAt(c, r), this);
                flag = !flag;
            }
            flag = !flag;
        }
    }

    public Desk(int dim) {
        this.dim = dim;
        this.endLetter = (char) (START_LETTER + this.dim - 1);
        boolean flag = false;

        this.pos = new Position[dim][];

        for (char c = Desk.START_LETTER; c <= this.endLetter; c++) {
            pos[c - Desk.START_LETTER] = new Position[dim];
            for (int r = 1; r <= dim; r++) {
                pos[c - Desk.START_LETTER][r - 1] = new Position(this, c, r,
                        flag);
                flag = !flag;
            }
            flag = !flag;
        }
    }


    public int getDimension() {
        return this.dim;
    }

    public char getEndLetter() {
        return this.endLetter;
    }

    /**
     * Returns the figure on the given position.
     * If address is not valid, returns null.
     *
     * @param c column
     * @param r row
     * @return figure
     */
    public Figure getFigureAt(char c, int r) {
        Position pos = getPositionAt(c, r);
        if (pos == null) {
            return null;
        }
        return pos.getFigure();
    }

    /**
     * Returns a position on the given position.
     * If address is not valid, returns null.
     *
     * @param c column
     * @param r row
     * @return position
     */
    public Position getPositionAt(char c, int r) {
        if (outOfBoundaries(c, r)) {
            return null;
        }
        return pos[c - Desk.START_LETTER][r - 1];
    }

    /**
     * Determines if the address is a valid address or not.
     *
     * @param c column
     * @param r row
     * @return true if valid, false if not valid.
     */
    protected boolean outOfBoundaries(char c, int r) {
        return c < Desk.START_LETTER || c > this.endLetter || r < 1
                || r > this.dim;
    }
}
