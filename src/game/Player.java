package game;

import basis.Position;
import basis.Step;
import gui.GuiUpdate.GuiUpdate;

public abstract class Player extends GuiUpdate {
    protected Game game;
    protected boolean isInteractive;
    protected String name;
    protected boolean white;

    public Player(boolean white) {
        this.white = white;
        this.isInteractive = false;
    }

    public Player(Player newPlayer) {
        this(newPlayer.white);
        this.game = newPlayer.game;
        this.isInteractive = newPlayer.isInteractive;
        this.name = newPlayer.name;
    }

    public abstract void dispose();

    public void doMove(Position from, Position to) {
        if (this.game != null) {
            this.game.doMove(from, to);
        }
    }

    public void doMove(Step step) {
        if (this.game != null) {
            this.game.doMove(step.getFrom(), step.getTo());
        }
    }

    public Game getGame() {
        return this.game;
    }

    public boolean isBlack() {
        return !this.white;
    }

    public boolean isInteractive() {
        return this.isInteractive;
    }

    public boolean isWhite() {
        return this.white;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String result = this.isWhite() ? "white" : "black";

        if (this.name != null) {
            result = this.name + " (" + result + ")";
        }
        return result;
    }

    public abstract void yourTurn();
}
