package ru.nsu.fit.replica.storage.network;

import ru.nsu.fit.replica.Message;

public interface SendingConnection {
    void send(Message data);
    void close();
}
