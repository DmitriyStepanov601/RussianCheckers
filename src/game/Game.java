package game;

import basis.*;
import figures.Pawn;
import figures.Rook;
import gui.GuiUpdate.GuiUpdate;
import log.IFileLog;
import players.ComputerPlayer;
import players.HumanPlayer;
import players.NetworkPlayer.NetworkPlayer;

import java.io.File;
import java.util.List;

public class Game extends GuiUpdate {
    public enum StepStatus {
        CANT_MOVE,
        MUST_MOVE,
        NO_FIGURE,
        NOT_CURRENT_PLAYER,
        NOT_DEFINED,
        OK,
        POS_FIG_NULL
    }

    protected static final int DIMENSION = 8;
    protected Desk desk;
    protected boolean dirtyFlag;
    protected IFileLog file;
    protected Player currentPlayer;
    protected boolean finished;
    protected StepStatus lastStepStatus;
    protected Player player1;
    protected Player player2;
    protected Log log;

    public static Game open(File f, IFileLog file) {
        return file.open(f);
    }

    public Game(Game newGame) {
        this(null, null);
        this.player1 = createNewPlayer(newGame.player1);
        this.player2 = createNewPlayer(newGame.player2);

        this.finished = newGame.finished;
        this.currentPlayer = newGame.currentPlayer;
        this.dirtyFlag = newGame.dirtyFlag;
        this.file = newGame.file;
        this.lastStepStatus = newGame.lastStepStatus;

        this.desk = new Desk(newGame.desk);
        this.log = new Log(newGame.log, this);
    }

    public Game(Player player1, Player player2) {
        this.dirtyFlag = true;
        this.finished = false;

        this.desk = createDesk();
        this.log = new Log();

        this.player1 = player1;
        if (this.player1 != null) {
            this.player1.setGame(this);
        }

        this.player2 = player2;
        if (this.player2 != null) {
            this.player2.setGame(this);
        }

        setCurrentPlayer();
    }

    public StepStatus addRound(Round round) {
        if (round == null) {
            return StepStatus.POS_FIG_NULL;
        }

        Round lastRound = getLastRound();

        if (lastRound == null && round.getN() != 0) {
            return StepStatus.POS_FIG_NULL;
        }

        if (lastRound != null && lastRound.getN() + 1 != round.getN()) {
            return StepStatus.POS_FIG_NULL;
        }

        Step step1 = round.getStep1();
        Step step2 = round.getStep2();

        if (step1 == null) {
            return StepStatus.POS_FIG_NULL;
        }

        StepStatus s = addStep(step1);

        if (s != StepStatus.OK) {
            return s;
        }

        if (step2 == null) {
            return s;
        }

        s = addStep(step2);

        return s;
    }

    public StepStatus addStep(Step step) {
        if (this.currentPlayer == null) {
            return StepStatus.NOT_CURRENT_PLAYER;
        }

        StepStatus status = canDoStep(step, this.currentPlayer);

        if (status == StepStatus.OK) {
            doStep(step);

            this.log.add(step);

            switchPlayer();
        }

        this.lastStepStatus = status;
        return status;
    }

    public StepStatus canDoStep(Step step, Player player) {
        if (player == null || step == null) {
            return StepStatus.POS_FIG_NULL;
        }

        Position from = step.getFrom();
        Position to = step.getTo();

        if (from == null || to == null) {
            return StepStatus.POS_FIG_NULL;
        }

        Figure f1 = from.getFigure();

        if (f1 == null) {
            return StepStatus.POS_FIG_NULL;
        }

        if (f1.isWhite() != player.isWhite()) {
            return StepStatus.NOT_CURRENT_PLAYER;
        }

        Step newStep = f1.canMove(to);

        if (newStep != null) {
            step.setX(newStep.getX());
        }

        Position x = step.getX();

        if (x == null && isDuty(player)) {
            return StepStatus.MUST_MOVE;
        }

        char sep = step.getReadSeparator();

        if (x == null && sep != 0 && sep != Step.POSITION_SEPARATOR) {
            return StepStatus.MUST_MOVE;
        }

        if (x != null && sep != 0 && sep != Step.POSITION_X_SEPARATOR) {
            return StepStatus.MUST_MOVE;
        }

        if (newStep != null) {
            return StepStatus.OK;
        }

        return StepStatus.CANT_MOVE;
    }

    protected void changePawnToRook(Position pos) {
        Rook rook;

        if (pos == null) {
            return;
        }

        Figure f1 = pos.getFigure();

        if (f1 == null) {
            return;
        }

        rook = new Rook(pos, f1.isWhite());
        pos.putFigure(rook);
    }

    public void checkRook() {
        Position pos = isNewRook(true);

        if (pos != null) {
            changePawnToRook(pos);
        }

        pos = isNewRook(false);

        if (pos != null) {
            changePawnToRook(pos);
        }
    }

    protected Desk createDesk() {
        Desk desk = new Desk(Game.DIMENSION);
        createFigures(desk);
        return desk;
    }

    protected void createFigures(Desk desk) {
        int dim = desk.getDimension();
        for (char c = Desk.START_LETTER; c <= desk.getEndLetter(); c++) {
            for (int r = 1; r <= dim; r++) {
                Position p = desk.getPositionAt(c, r);

                if (p.isBlack()) {
                    if (r <= 3) {
                        Pawn pawn = new Pawn(p, true);
                        p.putFigure(pawn);
                    } else if (r > (dim - 3)) {
                        Pawn pawn = new Pawn(p, false);
                        p.putFigure(pawn);
                    } else {
                        p.removeFigure();
                    }
                }
            }
        }
    }

    protected Player createNewPlayer(Player player) {
        if (player instanceof HumanPlayer) {
            return new HumanPlayer((HumanPlayer) player);
        } else if (player instanceof ComputerPlayer) {
            return new ComputerPlayer((ComputerPlayer) player);
        } else if (player instanceof NetworkPlayer) {
            return new NetworkPlayer((NetworkPlayer) player);
        }
        return player;
    }

    public void dispose() {
        if (this.player1 != null) {
            this.player1.dispose();
        }

        if (this.player2 != null) {
            this.player2.dispose();
        }
    }

    public void doMove(Position from, Position to) {
        if (from == null || to == null) {
            return;
        }

        StepStatus status = addStep(new Step(from, to));

        if (this.currentPlayer != null) {
            this.currentPlayer.yourTurn();
        }

        this.finished = isFinished();


        refreshGui();
    }

    protected void doStep(Step step) {
        if (step == null) {
            return;
        }

        Position from = step.getFrom();
        Position to = step.getTo();

        if (from == null || to == null) {
            return;
        }

        Figure f1 = from.getFigure();

        if (f1 == null) {
            return;
        }

        f1.move(to);

        Position remove = step.getX();

        if (remove != null) {
            remove.getFigure().removePosition();
            remove.removeFigure();
        }

        checkRook();

    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getDeskDimension() {
        return desk.getDimension();
    }

    public char getEndLetter() {
        return this.desk.getEndLetter();
    }

    public boolean getFinished() {
        return this.finished;
    }

    public Round getLastRound() {
        if (this.log != null) {
            return this.log.getLastRound();
        }
        return null;
    }

    public Step getLastStep() {
        if (this.log != null) {
            return this.log.getLastStep();
        }

        return null;
    }

    public StepStatus getLastStepStatus() {
        return this.lastStepStatus;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Position getPositionAt(char c, int r) {
        return this.desk.getPositionAt(c, r);
    }

    public Position getPositionAt(Position p) {
        if (p == null) {
            return null;
        }

        char c = p.getImageCol();
        int r = p.getImageRow();
        return getPositionAt(c, r);
    }

    public List<Round> getRounds() {
        if (this.log != null) {
            return this.log.getLog();
        }
        return null;
    }

    public int getStepCount() {
        int sum = 0;

        if (this.log != null) {
            List<Round> rounds = this.log.getLog();
            for (Round round : rounds) {
                sum += round.getStepCount();
            }
        }

        return sum;
    }

    public List<Step> getSteps() {
        if (this.log != null) {
            return this.log.getSteps();
        }
        return null;
    }

    public Player getWinner() {
        if (this.finished) {
            return this.currentPlayer == this.player1 ? this.player2
                    : this.player1;
        }

        return null;
    }

    public boolean isDirty() {
        return this.dirtyFlag;
    }

    private boolean isDuty(Player player) {
        for (char c = Desk.START_LETTER; c <= this.getEndLetter(); c++) {
            for (int r = 1; r <= this.desk.getDimension(); r++) {
                Position p = this.getPositionAt(c, r);
                Figure f = p.getFigure();

                if (f != null && f.isWhite() == player.isWhite()) {
                    List<Step> steps = f.getSteps();

                    if (steps == null) {
                        continue;
                    }

                    for (Step step : steps) {
                        if (step.getX() != null) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isFinished() {
        int count = 0;
        int steps = 0;

        for (char c = Desk.START_LETTER; c <= this.getEndLetter(); c++) {
            for (int r = 1; r <= this.desk.getDimension(); r++) {
                Position p = this.getPositionAt(c, r);
                Figure f = p.getFigure();

                if (f != null && f.isWhite() == this.currentPlayer.isWhite()) {
                    count += 1;
                    steps += f.getSteps().size();
                }
            }
        }

        return count == 0 || steps == 0;
    }

    public Position isNewRook(boolean white) {
        for (char c = Desk.START_LETTER; c <= this.getEndLetter(); c++) {
            int row = white ? this.desk.getDimension() : 1;
            Position p = this.getPositionAt(c, row);

            if (p != null && p.isBlack()) {
                Figure f = p.getFigure();

                if (f != null && f.isWhite() == white
                        && f.getClass() != Rook.class) {
                    return p;
                }
            }
        }

        return null;
    }

    public void reset() {
        this.log.clear();
        this.desk = createDesk();
        setCurrentPlayer();
    }

    public boolean save(File f) {
        boolean result = file.save(f, this);
        this.dirtyFlag = !result;
        return result;
    }

    public void setCurrentPlayer() {
        if (this.player1 != null || this.player2 != null) {
            assert this.player1 != null;
            this.currentPlayer = this.player1.isWhite() ? this.player1 : this.player2;
        }
    }

    public void setDirty(boolean dirty) {
        this.dirtyFlag = dirty;
    }

    public void setFileType(IFileLog file) {
        this.file = file;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void start() {
        if (this.currentPlayer != null) {
            this.currentPlayer.yourTurn();
        }

        refreshGui();
    }

    public void switchPlayer() {
        this.currentPlayer = (this.currentPlayer == this.player1) ? this.player2
                : this.player1;
    }

    @Override
    public String toString() {
        return "Russian checkers";
    }
}
