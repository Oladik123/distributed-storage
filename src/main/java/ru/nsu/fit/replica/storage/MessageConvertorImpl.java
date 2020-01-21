package ru.nsu.fit.replica.storage;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.nsu.fit.replica.Message;

public class MessageConvertorImpl implements MessageConvertor {
    private final Gson gson = new Gson();

    @Override
    public Message toNetwork(StorageMessage message) {
        Message networkMsg = new Message();
        networkMsg.setData(message.getValue());

        Map<String, String> headers = new HashMap<>();
        headers.put(Message.KEY_HEADER_KEY, message.getKey());
        headers.put(Message.ACTION_HEADER_KEY, message.getAction().name());
        headers.put(Message.ACTION_ID_HEADER_KEY, String.valueOf(message.getActionId()));
        networkMsg.setHeaders(headers);

        return networkMsg;
    }

    @Override
    public StorageMessage fromNetwork(Message message) {
        StorageMessage storageMessage = new StorageMessage();
        storageMessage.setKey(message.getHeaders().get(Message.KEY_HEADER_KEY));
        storageMessage.setActionId(Long.valueOf(message.getHeaders().get(Message.ACTION_ID_HEADER_KEY)));
        storageMessage.setAction(StorageAction.valueOf(message.getHeaders().get(Message.ACTION_HEADER_KEY)));
        storageMessage.setValue(message.getData());
        return storageMessage;
    }

    @Override
    public Message initToNetwork(InitMessage message) {
        Message networkMsg = new Message();
        networkMsg.setData(gson.toJson(message.getStorage()));

        Map<String, String> headers = new HashMap<>();
        headers.put(Message.ACTION_HEADER_KEY, StorageAction.INIT.name());
        headers.put(Message.ACTION_ID_HEADER_KEY, String.valueOf(message.getLastId()));
        networkMsg.setHeaders(headers);

        return networkMsg;
    }

    @Override
    public InitMessage initFromNetwork(Message message) {
        InitMessage initMessage = new InitMessage();
        Type type = new TypeToken<Map<String, String>>() {}.getType();

        initMessage.setStorage(gson.fromJson(message.getData(), type));
        initMessage.setLastId(Long.valueOf(message.getHeaders().get(Message.ACTION_ID_HEADER_KEY)));

        return initMessage;
    }
}
