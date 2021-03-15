package players.NetworkPlayer;

public interface NetworkPlayerInitListener {
    void accept(String status);

    void connected(String status);

    void errorHandler(String message);
}
