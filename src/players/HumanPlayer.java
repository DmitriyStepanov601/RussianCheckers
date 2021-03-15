package players;

import game.Player;

public class HumanPlayer extends Player {
    public HumanPlayer(boolean white) {
        super(white);
        this.name = "Human";
        this.isInteractive = true;
    }

    public HumanPlayer(HumanPlayer newPlayer) {
        super(newPlayer);
        this.name = newPlayer.name;
        this.isInteractive = newPlayer.isInteractive;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void yourTurn() {
    }
}
