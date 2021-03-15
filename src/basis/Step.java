package basis;

import game.Game;

public class Step {
    public static final char POSITION_SEPARATOR = '-';
    public static final char POSITION_X_SEPARATOR = 'x';

    public static Step parseStep(String s, Game game) {
        if (s == null) {
            return null;
        }

        char sep = s.charAt(2);
        if (s.length() == 5 && (sep == POSITION_SEPARATOR || sep == POSITION_X_SEPARATOR)) {
            int fromR;
            int toR;

            try {
                fromR = Integer.parseInt(Character.toString(s.charAt(1)));
                toR = Integer.parseInt(Character.toString(s.charAt(4)));
            } catch (NumberFormatException ex) {
                return null;
            }

            Position from = game.getPositionAt(s.charAt(0), fromR);
            Position to = game.getPositionAt(s.charAt(3), toR);

            if (from != null && to != null) {
                Step step = new Step(from, to);
                step.readSeparator = sep;

                return step;
            }
        }

        return null;
    }

    protected Position from;
    protected char readSeparator = 0;
    protected Position to;
    protected Position x;

    /**
     * Creates new step instance
     *
     * @param from position
     * @param to   position
     */
    public Step(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Creates new position
     *
     * @param from position
     * @param to   position
     * @param x    position
     */
    public Step(Position from, Position to, Position x) {
        this(from, to);
        this.x = x;
    }

    /**
     * Creates new step instance. Copies values from newStep.
     *
     * @param newStep copy values from this instance
     * @param game    step will be addedd in this game.
     */
    public Step(Step newStep, Game game) {
        this(game.getPositionAt(newStep.from), game.getPositionAt(newStep.to),
                game.getPositionAt(newStep.x));
        this.readSeparator = newStep.readSeparator;
    }

    protected boolean checkStep(Position p1, Position p2) {
        if (p1 != null) {
            return !p1.equals(p2);
        } else {
            return p2 != null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Step) {
            Step step = (Step) obj;

            if (checkStep(this.from, step.from)) {
                return false;
            }

            if (checkStep(this.to, step.to)) {
                return false;
            }

            return !checkStep(this.x, step.x);
        }
        return false;
    }

    public Position getFrom() {
        return this.from;
    }

    public char getReadSeparator() {
        return this.readSeparator;
    }

    public Position getTo() {
        return this.to;
    }

    public Position getX() {
        return this.x;
    }

    public void setX(Position x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return from.toString()
                + (x == null ? Character.toString(POSITION_SEPARATOR) :
                Character.toString(POSITION_X_SEPARATOR))
                + to.toString();
    }
}
