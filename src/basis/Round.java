package basis;

import game.Game;

public class Round {
    public static Round parseRound(String s, Game game) {
        if (s == null) {
            return null;
        }

        String[] parts = s.split(" ");

        if (parts.length == 2) {
            int n;
            try {
                n = Integer.parseInt(parts[0].substring(0, parts[0].length() - 1)) - 1;
            } catch (NumberFormatException ex) {
                return null;
            }

            Step step1 = Step.parseStep(parts[1], game);

            if (step1 == null) {
                return null;
            }

            Round round = new Round(n);
            round.setStep1(step1);
            return round;
        }

        if (parts.length == 3) {
            int n;
            try {
                n = Integer.parseInt(parts[0].substring(0,
                        parts[0].length() - 1)) - 1;
            } catch (NumberFormatException ex) {
                return null;
            }

            Step step1 = Step.parseStep(parts[1], game);
            Step step2 = Step.parseStep(parts[2], game);

            if (step1 == null || step2 == null) {
                return null;
            }

            Round round = new Round(n);
            round.setStep1(step1);
            round.setStep2(step2);
            return round;
        }

        return null;
    }

    protected int n;
    protected Step step1;
    protected Step step2;

    /**
     * Create new round instance
     *
     * @param n index of this round
     */
    public Round(int n) {
        this.n = n;
    }

    /**
     * Create a new round instance based on a round
     *
     * @param newRound copy values from this round
     * @param game     newly created round will be inserted to this game
     */
    public Round(Round newRound, Game game) {
        this(newRound.n);
        if (newRound.step1 != null) {
            this.step1 = new Step(newRound.step1, game);
        }
        if (newRound.step2 != null) {
            this.step2 = new Step(newRound.step2, game);
        }
    }

    public int getN() {
        return this.n;
    }

    public Step getStep1() {
        return this.step1;
    }

    public Step getStep2() {
        return this.step2;
    }

    public int getStepCount() {
        int sum = 0;

        if (step1 != null) {
            sum += 1;
        }

        if (step2 != null) {
            sum += 1;
        }

        return sum;
    }

    public void setStep1(Step s) {
        this.step1 = s;
    }

    public void setStep2(Step s) {
        this.step2 = s;
    }

    @Override
    public String toString() {
        return (n + 1) + ". " + step1.toString() + " "
                + (step2 != null ? step2.toString() : "");
    }
}
