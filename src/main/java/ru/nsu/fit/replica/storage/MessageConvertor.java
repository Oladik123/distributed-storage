package ru.nsu.fit.replica.storage;

import ru.nsu.fit.replica.Message;

public interface MessageConvertor {
    Message toNetwork(StorageMessage message);
    StorageMessage fromNetwork(Message message);
    Message initToNetwork(InitMessage message);
    InitMessage initFromNetwork(Message message);
}
