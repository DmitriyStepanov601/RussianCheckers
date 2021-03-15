package players.NetworkPlayer;

import basis.Step;

public interface NetworkPlayerReceiveListener {
    void errorHandler(String message);

    void received(Step step);
}