package basis;

import javax.swing.*;
import java.awt.Graphics;
import java.util.List;

public abstract class Figure {
    protected Position p;
    protected boolean white;

    /**
     * Creates new figure based on existing one.
     * Puts the new figure on position p.
     *  @param newF existing figure
     * @param p    put new figure here
     */
    public Figure(Figure newF, Position p) {
        this(p, newF.white);
    }

    /**
     * Create new figure.
     *
     * @param p     store figure on position p
     * @param white new figue's color
     */
    public Figure(Position p, boolean white) {
        this.p = p;
        this.white = white;
    }

    /**
     * Returns true if this figure can move to position p, false otherwise.
     *
     * @param p test moving to this position
     * @return true or false
     */
    public abstract Step canMove(Position p);

    /**
     * Draws figure on g.
     *
     * @param g  where to draw
     * @param x0 right upper corner's x (position)
     * @param y0 right upper corner's y (position)
     * @param w  width of position
     * @param h  height of position
     */
    public abstract void draw(Graphics g, int x0, int y0, int w, int h);

    public Position getPosition() {
        return this.p;
    }

    public abstract List<Step> getSteps();

    public boolean isBlack() {
        return !this.white;
    }

    public boolean isWhite() {
        return this.white;
    }

    public void move(Position p) {
        this.p.removeFigure();
        this.p = p;
        this.p.putFigure(this);
    }

    public void removePosition() {
        Position previous = this.p;
        this.p = null;
    }
}
