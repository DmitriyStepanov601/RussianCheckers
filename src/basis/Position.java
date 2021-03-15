package basis;

import figures.Pawn;
import figures.Rook;

import java.awt.Color;
import java.awt.Graphics;

public class Position {
    public static char getImageCol(int col) {
        return (char) (col + Desk.START_LETTER);
    }
    public static int getImageRow(int row) {
        return row + 1;
    }

    public static int getRealCol(char c) {
        char ch = Character.toLowerCase(c);
        return ch - Desk.START_LETTER;
    }

    public static int getRealRow(int row) {
        return row - 1;
    }

    public static final Color BROWN_COLOR = new Color(82, 43, 43);
    protected char c;
    protected Desk desk;
    protected Figure f;
    protected int r;
    protected boolean selected;
    protected boolean white;

    /**
     * Create new position based on col and row value
     * desk will be null and color: white
     *
     * @param c column
     * @param r row
     */
    public Position(char c, int r) {
        this(null, c, r, true);
    }

    /**
     * Creates new position object
     *
     * @param d     desk
     * @param c     column
     * @param r     row
     * @param white color
     */
    public Position(Desk d, char c, int r, boolean white) {
        this.c = c;
        this.r = r;
        this.desk = d;
        this.white = white;
        this.selected = false;
    }

    /**
     * Creates new position
     *
     * @param newPos copy values from this position object
     * @param d      desk where the position is stored
     */
    public Position(Position newPos, Desk d) {
        this(d, newPos.c, newPos.r, newPos.white);
        this.selected = newPos.selected;
        if (newPos.f != null) {
            if (newPos.f instanceof Pawn) {
                this.f = new Pawn((Pawn) (newPos.f), this);
            } else if (newPos.f instanceof Rook) {
                this.f = new Rook((Rook) (newPos.f), this);
            }
        }
    }

    public void draw(Graphics g, int x0, int y0, int w, int h) {
        g.setColor(this.white ? (this.selected ? Color.white : Color.white)
                : (this.selected ? BROWN_COLOR : BROWN_COLOR));
        int x = x0 + getRealCol() * w;
        int y = y0 + getRealRow() * h;
        g.fillRect(x, y, w, h);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Position && o.hashCode() == hashCode();
    }

    public Desk getDesk() {
        return this.desk;
    }

    public Figure getFigure() {
        return f;
    }

    public char getImageCol() {
        return c;
    }

    public int getImageRow() {
        return r;
    }

    public int getRealCol() {
        char ch = Character.toLowerCase(c);
        return ch - Desk.START_LETTER;
    }

    public int getRealRow() {
        return r - 1;
    }

    @Override
    public int hashCode() {
        String s = Integer.toString(this.c) + this.r;
        return Integer.parseInt(s);
    }

    public boolean isBlack() {
        return !this.white;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isWhite() {
        return this.white;
    }

    public Position nextPosition(int dC, int dR) {
        int c = getRealCol(this.c) + dC;
        int r = getRealRow(this.r) + dR;

        return this.desk.getPositionAt(getImageCol(c), getImageRow(r));
    }

    public void putFigure(Figure f) {
        Figure previous = this.f;
        if (previous != null) {
            previous.removePosition();
        }
        this.f = f;
    }

    public void removeFigure() {
        Figure previous = this.f;
        if (previous != null) {
            previous.removePosition();
        }
        this.f = null;
    }

    public boolean sameColumn(Position p) {
        return p.c == this.c;
    }

    public boolean sameRow(Position p) {
        return p.r == this.r;
    }

    public void select() {
        this.selected = true;
    }

    @Override
    public String toString() {
        return this.c + String.valueOf(this.r);
    }

    public void unSelect() {
        this.selected = false;
    }
}