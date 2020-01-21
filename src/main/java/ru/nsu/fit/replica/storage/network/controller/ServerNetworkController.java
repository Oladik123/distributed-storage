package ru.nsu.fit.replica.storage.network.controller;

import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.storage.network.NetworkChannel;
import ru.nsu.fit.replica.storage.network.multicast.MulticastConnectionHost;
import ru.nsu.fit.replica.storage.network.p2p.P2PConnectionServer;

import java.util.*;

public class ServerNetworkController implements NetworkController {

    private final Queue<Message> queue = new LinkedList<>();
    private final Map<UUID, NetworkChannel> clients = new HashMap<>();
    private MulticastConnectionHost host = new MulticastConnectionHost();
    private P2PConnectionServer server;
    private UUID uuid;

    @Override
    public Message getMessage() {
        try {
            synchronized (queue){
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
        message.getHeaders().put(Message.CLIENT_ID_HEADER_KEY, uuid.toString());
        synchronized (clients){
            clients.get(nodeId).send(message);
        }
    }

    @Override
    public void writeMessage(Message message) {
        host.send(message);
    }

    public ServerNetworkController(String address, int p2pPort, int broadcastPort) {
        uuid = UUID.randomUUID();
        server = new P2PConnectionServer();
        server.start(address, p2pPort);

        Thread thread = new Thread(new ConnectionListener());
        thread.start();

        host.host(address, broadcastPort);
    }

    class ConnectionListener implements Runnable {

//        private P2PConnectionServer server;
//
//        ConnectionListener(P2PConnectionServer server) {
//            this.server = server;
//        }

        @Override
        public void run() {
            while (true) {
                NetworkChannel connectionClient = server.accept();

                Message m = connectionClient.receive();
                UUID newUUID = UUID.fromString(m.getData());
                synchronized (clients){
                    clients.put(newUUID, connectionClient);
                }

                Thread thread = new Thread(new MessageListener(connectionClient));
                thread.start();
            }
        }
    }

    private class MessageListener implements Runnable {

        private NetworkChannel connectionClient;

        MessageListener(NetworkChannel connectionClient) {
            this.connectionClient = connectionClient;
        }

        @Override
        public void run() {
            while (true) {
                Message m = connectionClient.receive();
                synchronized (queue){
                    queue.add(m);
                    queue.notify();
                }
            }
        }
    }

    //TODO: close
}
