package ru.nsu.fit.replica.storage.server;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.storage.MessageConvertor;
import ru.nsu.fit.replica.storage.MessageConvertorImpl;
import ru.nsu.fit.replica.storage.StorageAction;
import ru.nsu.fit.replica.storage.StorageMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageReaderServerImpl<K, V> implements StorageReaderServer<K, V> {

    private final ZContext context = new ZContext();
    private final Socket socket = context.createSocket(SocketType.SUB);
    private final MessageConvertor<K, V> convertor = new MessageConvertorImpl<>();
    private final Map<K, V> localStorage = new ConcurrentHashMap<>();

    public StorageReaderServerImpl() {
        socket.connect("tcp://localhost:5555");
        socket.subscribe(ZMQ.SUBSCRIPTION_ALL);
    }

    @Override
    public StorageWriterServer<K, V> promoteToWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initialSync() {
        while (!Thread.currentThread().isInterrupted()) {
            StorageMessage<K, V> message = convertor.fromNetwork(new Message(socket.recv(0)));
            System.out.println(this + " received msg");
            if (message.getAction() == StorageAction.PUT) {
                localStorage.put(message.getKey(), message.getValue());
            }
        }
    }

    @Override
    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(K key) {
        return localStorage.get(key);
    }
}
