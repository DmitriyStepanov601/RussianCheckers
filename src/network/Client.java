package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client extends NetworkMan {
    protected String host;

    public Client(String host, int port, String name) {
        super(port, name);
        this.host = host;
    }

    @Override
    public void connect() {
        if (this.connectThread != null && this.receiveThread.isAlive()) {
            return;
        }

        Runnable r = () -> {
            try {
                s = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(
                        s.getInputStream()));
                out = new PrintStream(s.getOutputStream());
                sendRequest();
                waitForAccept();
            } catch (IOException ex) {
                handleExceptions(ex);
            }
        };

        this.connectThread = new Thread(r);
        this.connectThread.start();
    }

    protected boolean parseAccepted(String message) {
        String par = parseCommand(ACCEPTED, message);

        if (par != null) {
            this.opponentName = par;
            sendConnected("Game from " + par + "accepted.");
            return true;
        }

        return false;
    }

    protected void sendRequest() {
        send(REQUEST + this.name);
    }

    protected void waitForAccept() {
        receive();
    }
}
