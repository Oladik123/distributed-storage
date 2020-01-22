package ru.nsu.fit.replica.storage;

import ru.nsu.fit.replica.Message;

public interface MessageConvertor<K, V> {
    Message toNetwork(StorageMessage<K, V> message);
    StorageMessage<K, V> fromNetwork(Message message);
}
