package network;

import basis.Position;
import basis.Step;
import players.NetworkPlayer.NetworkPlayerInitListener;
import players.NetworkPlayer.NetworkPlayerReceiveListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.BindException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public abstract class NetworkMan {
    protected static final String ACCEPTED = "accepted";
    protected static final String CLOSED = "closed";
    protected static final String REQUEST = "request";
    protected static final String SEPARATOR = "-";
    protected Thread connectThread;
    protected boolean created;
    protected BufferedReader in;
    protected List<NetworkPlayerInitListener> initListeners = new ArrayList<>();
    protected String name;
    protected String opponentName;
    protected PrintStream out;
    protected int port;
    protected List<NetworkPlayerReceiveListener> receiveListeners = new ArrayList<>();
    protected Thread receiveThread;
    protected Socket s;

    public NetworkMan(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public void addInitListener(NetworkPlayerInitListener toAdd) {
        initListeners.add(toAdd);
    }

    public void addReceiveListener(NetworkPlayerReceiveListener toAdd) {
        receiveListeners.add(toAdd);
    }

    public abstract void connect();

    public void dispose() {
        try {
            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (s != null) {
                s.close();
            }
        } catch (IOException ex) {
            handleExceptions(ex);
        }

        if (this.connectThread != null) {
            this.connectThread.interrupt();
        }

        if (this.receiveThread != null) {
            this.receiveThread.interrupt();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getOponentName() {
        return this.opponentName;
    }

    protected void handleExceptions(Exception ex) {
        String message = ex.getMessage();

        if (ex instanceof UnknownHostException) {
            message = "Unknown host: " + message;
        }

        if (ex instanceof BindException) {
            message = "Port (" + this.port + ") already in use";
        }

        for (NetworkPlayerInitListener npl : initListeners) {
            npl.errorHandler(message);
        }

        for (NetworkPlayerReceiveListener npl : receiveListeners) {
            npl.errorHandler("Fatal error: opponent not responding. Game closed.");
        }

        ex.printStackTrace();
    }

    public boolean isCreated() {
        return this.created;
    }

    protected String parseCommand(String command, String message) {
        String cmd = message.substring(0, command.length());
        String par = message.substring(command.length());
        if (cmd.equals(command)) {
            return par;
        }

        return null;
    }

    protected void parseInput(String message) {
        if (!this.created) {
            if (this instanceof Server) {
                Server server = (Server) this;
                server.parseRequest(message);
            } else {
                Client client = (Client) this;
                client.parseAccepted(message);
            }
        } else {
            Step step = parseStep(message);

            if (step != null) {
                sendReceived(step);
            }
        }
    }

    protected String parseStep(Step step) {
        if (step == null) {
            return null;
        }

        Position from = step.getFrom();
        Position to = step.getTo();

        return from.getImageCol() + SEPARATOR + from.getImageRow() + SEPARATOR
                + to.getImageCol() + SEPARATOR + to.getImageRow();
    }

    protected Step parseStep(String message) {
        if (message == null) {
            return null;
        }

        String[] parts = message.split(SEPARATOR);

        if (parts.length == 4) {
            int fromR;
            int toR;

            try {
                fromR = Integer.parseInt(parts[1]);
                toR = Integer.parseInt(parts[3]);
            } catch (NumberFormatException ex) {
                return null;
            }

            Position from = new Position(null, parts[0].charAt(0), fromR, true);
            Position to = new Position(null, parts[2].charAt(0), toR, true);

            return new Step(from, to);
        }

        return null;
    }

    public void receive() {
        if (this.receiveThread != null && this.receiveThread.isAlive()) {
            return;
        }

        Runnable r = () -> {
            String message;

            try {
                message = in.readLine();
            } catch (IOException ex) {
                handleExceptions(ex);
                return;
            }

            parseInput(message);
        };

        this.receiveThread = new Thread(r);
        this.receiveThread.start();
    }

    public void send(Step step) {
        send(parseStep(step));
    }

    public void send(String message) {
        out.print(message + "\n");
        out.flush();
    }

    protected void sendAccept(String status) {
        for (NetworkPlayerInitListener npl : initListeners) {
            npl.accept(status);
        }
    }

    protected void sendConnected(String status) {
        this.created = true;

        for (NetworkPlayerInitListener npl : initListeners) {
            npl.connected(status);
        }
    }

    protected void sendReceived(Step step) {
        for (NetworkPlayerReceiveListener npl : receiveListeners) {
            npl.received(step);
        }
    }
}
