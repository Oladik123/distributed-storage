package ru.nsu.fit.replica.storage.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.ReplicaLog;
import ru.nsu.fit.replica.ReplicaLogStorage;
import ru.nsu.fit.replica.storage.InitMessage;
import ru.nsu.fit.replica.storage.MessageConvertor;
import ru.nsu.fit.replica.storage.MessageConvertorImpl;
import ru.nsu.fit.replica.storage.Storage;
import ru.nsu.fit.replica.storage.StorageAction;
import ru.nsu.fit.replica.storage.StorageMessage;
import ru.nsu.fit.replica.storage.network.controller.NetworkController;

public class StorageWriterServerImpl implements StorageWriterServer {
    private final ReplicaLogStorage replicaLogStorage;

    private final Storage storage;
    private final NetworkController networkController;

    private final MessageConvertor messageConvertor = new MessageConvertorImpl();
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    public StorageWriterServerImpl(final ReplicaLogStorage replicaLogStorage,
                                   final Storage storage,
                                   final NetworkController networkController) {
        this.replicaLogStorage = replicaLogStorage;
        this.storage = storage;
        this.networkController = networkController;
    }


    @Override
    synchronized public void put(String key, String value) {
        ReplicaLog replicaLog = new ReplicaLog();
        replicaLog.setStorageAction(StorageAction.PUT);
        replicaLog.setKey(key);
        replicaLog.setValue(value);
        long actionId = replicaLogStorage.logAction(replicaLog);

        StorageMessage message = new StorageMessage();
        message.setKey(key);
        message.setValue(value);
        message.setAction(StorageAction.PUT);
        message.setActionId(actionId);
        networkController.writeMessage(messageConvertor.toNetwork(message));
    }

    @Override
    public String get(String key) {
        return storage.get(key);
    }

    @Override
    public Map<String, String> getCurrentSnapshot() {
        return storage.getCurrentSnapshot();
    }

    @Override
    public void start() {
        /* не уверен надо ли так, вынес в отдельный поток чтобы в основном можно было
         писать из консоли команды типа put key value например. Т.е. локальная запись
         */
        Thread thread = new Thread(() -> {
            while (isActive.get()) {
                Message message = networkController.getMessage();
                switch (StorageAction.valueOf(message.getHeaders().get(Message.ACTION_HEADER_KEY))) {
                    case PUT:
                        StorageMessage storageMessage = messageConvertor.fromNetwork(message);
                        put(storageMessage.getKey(), storageMessage.getValue());
                        break;
                    case INIT:
                        InitMessage initMessage = new InitMessage();
                        initMessage.setStorage(storage.getCurrentSnapshot());
                        initMessage.setLastId(replicaLogStorage.getCurrentActionId());

                        UUID uuid = UUID.fromString(message.getHeaders().get(Message.CLIENT_ID_HEADER_KEY));

                        networkController.writeMessage(uuid, messageConvertor.initToNetwork(initMessage));
                        break;
                    case INIT_REQ:
                        storageMessage = new StorageMessage();
                        ReplicaLog replicaLog = replicaLogStorage.getReplicaLog(
                                Long.valueOf(message.getHeaders().get(Message.ACTION_ID_HEADER_KEY))
                        );
                        storageMessage.setAction(replicaLog.getStorageAction());
                        storageMessage.setKey(replicaLog.getKey());
                        storageMessage.setValue(replicaLog.getValue());

                        uuid = UUID.fromString(message.getHeaders().get(Message.CLIENT_ID_HEADER_KEY));

                        networkController.writeMessage(uuid, messageConvertor.toNetwork(storageMessage));
                        break;
                }
            }
        });
        thread.start();
    }
}
