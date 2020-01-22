package ru.nsu.fit.replica.storage;

import ru.nsu.fit.replica.Message;

import java.io.*;

public class MessageConvertorImpl<K, V> implements MessageConvertor<K, V> {
    //    private static String ACTION = "Action";
    @Override
    public Message toNetwork(StorageMessage<K, V> message) {
        var msg = new Message();
        var data = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(data);
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        msg.setData(data.toByteArray());
        return msg;
    }

    @Override
    public StorageMessage<K, V> fromNetwork(Message message) {
        var data = new ByteArrayInputStream(message.getData());
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            //noinspection unchecked
            return (StorageMessage<K, V>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
