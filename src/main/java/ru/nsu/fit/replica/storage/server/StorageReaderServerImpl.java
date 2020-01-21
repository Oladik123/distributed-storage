package ru.nsu.fit.replica.storage.server;

import java.util.Map;
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

public class StorageReaderServerImpl implements StorageReaderServer {
    private static final int BUFFER_CAPACITY = 1000;
    private long maxPos = 0;
    private final StorageMessage[] syncBuffer = new StorageMessage[BUFFER_CAPACITY];

    private final ReplicaLogStorage replicaLogStorage;

    private final Storage storage;
    private final NetworkController networkController;

    private final MessageConvertor messageConvertor = new MessageConvertorImpl();
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    private boolean needSync = true;


    public StorageReaderServerImpl(final ReplicaLogStorage replicaLogStorage,
                                   final Storage storage,
                                   final NetworkController networkController) {
        this.replicaLogStorage = replicaLogStorage;
        this.storage = storage;
        this.networkController = networkController;
    }

    @Override
    public StorageWriterServer promoteToWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        Message message = new Message();
        message.setHeaders(Map.of(Message.ACTION_HEADER_KEY, StorageAction.INIT_REQ.name()));
        networkController.writeMessage(message);

        while (isActive.get()) {
            message = networkController.getMessage();
            if (needSync) {
                if (StorageAction.valueOf(message.getHeaders().get(Message.ACTION_HEADER_KEY)) == StorageAction.INIT) {
                    InitMessage initMessage = messageConvertor.initFromNetwork(message);
                    replicaLogStorage.setActionId(initMessage.getLastId());
                    initMessage.getStorage().forEach(storage::put);
                } else {
                    StorageMessage storageMessage = messageConvertor.fromNetwork(message);
                    synchronize(storageMessage);
                }
            } else {
                StorageMessage storageMessage = messageConvertor.fromNetwork(message);

                if (checkNeedSync(storageMessage.getActionId())) {
                    needSync = true;
                    synchronize(storageMessage);
                } else {
                    storeMessage(storageMessage);
                }
            }
        }
    }

    @Override
    public void put(String key, String value) {
        throw new UnsupportedOperationException(); //что мы тут делаем? у нас же ридер // возможно отправка запроса в врайтера
    }

    @Override
    public String get(String key) {
        return storage.get(key);
    }

    @Override
    public Map<String, String> getCurrentSnapshot() {
        return storage.getCurrentSnapshot();
    }

    private boolean checkNeedSync(long actionId) {
        return !(replicaLogStorage.getCurrentActionId() + 1 == actionId);
    }

    private void synchronize(StorageMessage storageMessage) {
        int pos = calculateSyncBufferPosition(storageMessage.getActionId());
        if (pos > maxPos) {
            maxPos = storageMessage.getActionId();
        }
        syncBuffer[pos] = storageMessage;
        if (!bufferHaveSkips()) {
            for (int i = 0; i <= maxPos; ++i) {
                storeMessage(syncBuffer[i]);
            }
            needSync = false;
        }
    }

    private void storeMessage(StorageMessage storageMessage) {
        logMessage(storageMessage);
        //TODO добавить логику для других операций кроме PUT если будут
        storage.put(storageMessage.getKey(), storageMessage.getValue());
    }

    private void logMessage(StorageMessage storageMessage) {
        ReplicaLog replicaLog = new ReplicaLog();
        replicaLog.setKey(storageMessage.getKey());
        replicaLog.setStorageAction(storageMessage.getAction());
        replicaLog.setValue(storageMessage.getValue());

        replicaLogStorage.logAction(replicaLog);
    }

    private boolean bufferHaveSkips() {
        boolean skips = false;
        for (int i = 0;
             i < maxPos;
             ++i) {
            if (syncBuffer[i] == null) {
                skips = true;
                Message message = new Message();
                message.setHeaders(
                        Map.of(Message.ACTION_HEADER_KEY, StorageAction.INIT_REQ.name(),
                                Message.ACTION_ID_HEADER_KEY, String.valueOf(replicaLogStorage.getCurrentActionId() + i + 1))
                );
                networkController.writeMessage(message);
            }
        }
        return skips;
    }

    private int calculateSyncBufferPosition(long actionId) {
        return (int) (actionId - replicaLogStorage.getCurrentActionId() - 1);
    }
}
