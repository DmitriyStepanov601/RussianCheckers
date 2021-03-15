package players;

import basis.Desk;
import basis.Figure;
import basis.Position;
import basis.Step;
import game.Game;
import game.Player;

import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer extends Player {
    public ComputerPlayer(boolean white) {
        super(white);
        this.name = "Computer";
    }

    public ComputerPlayer(ComputerPlayer newPlayer) {
        super(newPlayer);
        this.name = newPlayer.name;
    }

    private Step calculateRandomStep(Game game) {
        List<Step> steps = getAllStep(game);
        int size = steps.size();
        Step step = null;

        if (size > 0) {
            int randomNum = (int) (Math.random() * (size - 1));
            step = steps.get(randomNum);
        }

        return step;
    }

    @Override
    public void dispose() {
    }

    public List<Step> getAllStep(Game game) {
        List<Step> s = new ArrayList<>();

        boolean flag = false;

        for (char c = Desk.START_LETTER; c <= game.getEndLetter(); c++) {
            for (int r = 1; r <= game.getDeskDimension(); r++) {
                Position p = game.getPositionAt(c, r);
                Figure f = p.getFigure();

                if (f != null && f.isWhite() == this.white) {
                    List<Step> steps = f.getSteps();

                    if (steps == null) {
                        continue;
                    }

                    for (Step step : steps) {
                        if (!flag && step.getX() != null) {
                            s.clear();
                            flag = true;
                        }

                        if (!flag || step.getX() != null) {
                            s.add(step);

                        }
                    }
                }
            }
        }

        return s;
    }

    @Override
    public void yourTurn() {
        Runnable r = () -> {
            Step step = calculateRandomStep(game);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            doMove(step);
        };

        new Thread(r).start();
    }
}
