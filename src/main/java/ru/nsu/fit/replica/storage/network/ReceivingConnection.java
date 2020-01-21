package ru.nsu.fit.replica.storage.network;

import ru.nsu.fit.replica.Message;

public interface ReceivingConnection {
    Message receive();
    void close();
}
