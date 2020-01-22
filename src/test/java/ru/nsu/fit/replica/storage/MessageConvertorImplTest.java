package ru.nsu.fit.replica.storage;

import org.junit.Test;
import ru.nsu.fit.replica.Message;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.locks.Lock;

import static org.junit.Assert.*;

public class MessageConvertorImplTest {

    @Test
    public void simpleTest() {
        var converter = new MessageConvertorImpl<String, String>();
        var storeMessage = new StorageMessage<String, String>();
        storeMessage.setAction(StorageAction.PUT);
        storeMessage.setActionId(123456);
        storeMessage.setKey("Key");
        storeMessage.setValue("Value");

        var msg = converter.toNetwork(storeMessage);
        var sameStoreMessage = converter.fromNetwork(msg);
        assertEquals(storeMessage, sameStoreMessage);

        var sameMsg = converter.toNetwork(sameStoreMessage);
        assertEquals(msg, sameMsg);
    }
}