package replay;

import basis.Step;
import game.Game;
import game.Player;
import java.util.List;

public class GameReplay extends Game {
    protected int actualStep;

    public GameReplay(Game newG) {
        super(newG);
        setActualStepToLast();
    }

    public GameReplay(Player player1, Player player2) {
        super(player1, player2);
    }

    public Step getActualStep() {
        List<Step> steps = getSteps();
        int index = getActualStepIndex();
        if (index >= 0 && index < steps.size()) {
            return steps.get(index);
        }
        return null;
    }

    public int getActualStepIndex() {
        return this.actualStep;
    }

    public void goToStep(int stepIndex) {
        if (stepIndex < -1 || stepIndex >= this.getStepCount()) {
            return;
        }

        this.actualStep = stepIndex;
        createFigures(this.desk);
        int currentStep = -1;

        for (Step step : this.log.getSteps()) {
            if (currentStep >= stepIndex) {
                break;
            }

            doStep(step);

            currentStep += 1;
        }
    }

    public void nextStep() {
        goToStep(this.actualStep + 1);
    }

    public void setActualStepToLast() {
        this.actualStep = getStepCount() - 1;
    }

    public void stepBack() {
        goToStep(this.actualStep - 1);
    }
}
