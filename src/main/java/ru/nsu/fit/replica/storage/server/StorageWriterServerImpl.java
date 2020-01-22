package ru.nsu.fit.replica.storage.server;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import ru.nsu.fit.replica.storage.MessageConvertor;
import ru.nsu.fit.replica.storage.MessageConvertorImpl;
import ru.nsu.fit.replica.storage.StorageAction;
import ru.nsu.fit.replica.storage.StorageMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StorageWriterServerImpl<K, V> implements StorageWriterServer<K, V> {
    private final ConcurrentMap<K, V> storage = new ConcurrentHashMap<>();
    private final ZContext context = new ZContext();
    private final MessageConvertor<K, V> converter = new MessageConvertorImpl<>();
    private final Socket pubSocket = context.createSocket(SocketType.PUB);
    private long actionId = 0;

    public StorageWriterServerImpl() {
        pubSocket.bind("tcp://*:5555");
    }

    @Override
    public void put(K key, V value) {
        checkNotNull(key);
        storage.put(key, value);
        var msg = new StorageMessage<K, V>();
        msg.setKey(key);
        msg.setValue(value);
        msg.setActionId(actionId++);
        msg.setAction(StorageAction.PUT);
        pubSocket.send(converter.toNetwork(msg).getData());
    }

    @Override
    public V get(K key) {
        checkNotNull(key);
        return storage.get(key);
    }

    private void checkNotNull(Object arg) {
        if (null == arg) {
            throw new NullPointerException("Passed null into argument");
        }
    }
}
