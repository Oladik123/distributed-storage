package ru.nsu.fit.replica.storage.network.controller;

import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.storage.StorageAction;
import ru.nsu.fit.replica.storage.network.multicast.MulticastConnectionClient;
import ru.nsu.fit.replica.storage.network.p2p.P2PConnectionClient;

import java.util.*;

public class ClientNetworkController implements NetworkController {

    private final Queue<Message> queue = new LinkedList<>();
    private P2PConnectionClient connectionClient = new P2PConnectionClient();
    private MulticastConnectionClient broadcastClient = new MulticastConnectionClient();
    private UUID uuid;
    //private UUID serverUUID;

    @Override
    public Message getMessage() {
        try {
            synchronized (queue) {
                while (queue.size() == 0){
                    queue.wait();
                }
                return queue.remove();
            }
        } catch (NoSuchElementException | InterruptedException e) {
            return null;
        }
    }

    @Override
    public void writeMessage(UUID nodeId, Message message) {

    }

    @Override
    public void writeMessage(Message message) {
        message.getHeaders().put(Message.CLIENT_ID_HEADER_KEY, uuid.toString());
        connectionClient.send(message);
    }

    public ClientNetworkController(String address, int p2pPort, int broadcastPort){
        connectionClient.connect(address, p2pPort);

        uuid = UUID.randomUUID();
        Message message = new Message();
        message.setData(uuid.toString());
        message.setHeaders(Map.of(Message.ACTION_HEADER_KEY, StorageAction.INIT_REQ.name()));
        message.getHeaders().put(Message.CLIENT_ID_HEADER_KEY, uuid.toString());
        connectionClient.send(message);


        Thread thread1 = new Thread(new P2PMessageListener());
        thread1.start();

        broadcastClient.connect(address, broadcastPort);

        Thread thread2 = new Thread(new MulticastMessageListener());
        thread2.start();

    }

    private class P2PMessageListener implements Runnable{

        @Override
        public void run() {
//            Message m = connectionClient.receive();
//            uuid = UUID.fromString(m.getData());
//            serverUUID = UUID.fromString(m.getHeaders().get(Message.CLIENT_ID_HEADER_KEY));
            while (true){
                Message m = connectionClient.receive();
                synchronized (queue){
                    queue.add(m);
                    queue.notify();
                }
            }
        }
    }

    private class MulticastMessageListener implements Runnable{

        @Override
        public void run() {
            while (true){
                Message m = broadcastClient.receive();
                synchronized (queue){
                    queue.add(m);
                }
                ClientNetworkController.this.notify();
            }
        }
    }
}
