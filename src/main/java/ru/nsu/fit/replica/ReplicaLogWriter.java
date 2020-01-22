package ru.nsu.fit.replica;

public interface ReplicaLogWriter {
    void send(Message message) throws ReplicaException;
}
