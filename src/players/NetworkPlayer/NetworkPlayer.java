package players.NetworkPlayer;

import basis.Position;
import basis.Step;
import game.Player;
import network.NetworkMan;
import network.Server;

public class NetworkPlayer extends Player implements
        NetworkPlayerReceiveListener, NetworkPlayerInitListener {
    protected String lastErrorMessage;
    protected NetworkMan nm;

    public NetworkPlayer(boolean white) {
        super(white);
        this.lastErrorMessage = "";
    }

    public NetworkPlayer(boolean white, NetworkMan nm) {
        super(white);
        this.nm = nm;
        if (this.nm != null) {
            this.nm.addReceiveListener(this);
            this.nm.addInitListener(this);
        }
    }

    public NetworkPlayer(NetworkPlayer newPlayer) {
        super(newPlayer);
        this.nm = newPlayer.nm;
        this.lastErrorMessage = newPlayer.lastErrorMessage;
    }

    @Override
    public void accept(String status) {
    }

    public void cancel() {
        dispose();
    }

    public void connect() {
        if (this.nm != null) {
            this.nm.connect();
        }
    }

    @Override
    public void connected(String status) {
        setName(getOponentName());
    }

    @Override
    public void dispose() {
        if (this.nm != null) {
            this.nm.dispose();
        }
    }

    @Override
    public void errorHandler(String message) {
        this.lastErrorMessage = message;
        refreshGui();
    }

    public String getLastError() {
        return this.lastErrorMessage;
    }

    public String getNameFromNetwork() {
        if (this.nm != null) {
            return this.nm.getName();
        }
        return null;
    }

    public String getOponentName() {
        if (this.nm != null) {
            return this.nm.getOponentName();
        }
        return null;
    }

    @Override
    public void received(Step step) {
        if (step != null) {
            Position from = this.game.getPositionAt(step.getFrom());
            Position to = this.game.getPositionAt(step.getTo());

            doMove(from, to);
        }
    }

    public void setAccepted() {
        if (this.nm != null && this.nm instanceof Server) {
            Server server = (Server) this.nm;
            server.setAccepted();
        }
    }

    @Override
    public void yourTurn() {
        Step lastStep = game.getLastStep();

        if (lastStep != null && this.nm != null) {
            this.nm.send(lastStep);
        }

        if (this.nm != null) {
            this.nm.receive();
        }
    }
}
