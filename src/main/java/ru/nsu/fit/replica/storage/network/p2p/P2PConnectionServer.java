package ru.nsu.fit.replica.storage.network.p2p;

import lombok.SneakyThrows;
import ru.nsu.fit.replica.storage.network.NetworkChannel;
import ru.nsu.fit.replica.storage.network.NetworkConnectionServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PConnectionServer implements NetworkConnectionServer {

    private ServerSocket socket;

    /**
     * Создает новый сокет, способный принимать соединения на указанном адресе и порту
     * @param bindAddress
     * @param port
     */
    @SneakyThrows
    @Override
    public void start(String bindAddress, int port) {
        socket = new ServerSocket();
        socket.bind(new InetSocketAddress(bindAddress, port));
    }

    /**
     * Блокирующее принятие входящего соединения
     * @return
     */
    @SneakyThrows
    @Override
    public NetworkChannel accept() {
        Socket s = socket.accept();
        return new P2PConnectionClient(s);
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
