package ru.nsu.fit.replica.storage.network;

public interface NetworkConnectionServer {
    void start(String bindAddress, int port);
    NetworkChannel accept();
    void stop();
}
