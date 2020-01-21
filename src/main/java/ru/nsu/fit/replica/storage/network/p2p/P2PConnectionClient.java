package ru.nsu.fit.replica.storage.network.p2p;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.storage.network.NetworkChannel;
import ru.nsu.fit.replica.storage.network.NetworkConnector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class P2PConnectionClient implements NetworkConnector, NetworkChannel {

    private Socket socket;

    /**
     * Создает пустое соединение. Перед вызовами receive и send требуется вызов connect
     */
    public P2PConnectionClient() {

    }

    /**
     * Создает соединение с существующим сокетом. Высоз accept не требуется
     * @param socket сокет
     */
    P2PConnectionClient(Socket socket) {
        this.socket = socket;
    }

    /**
     * Создание нового сокета и подключение к указангому адресу и порту
     * @param address
     * @param port
     */
    @Override
    public void connect(@NotNull String address, int port) {
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(address, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Блокирующее получение Message
     * @return
     */
    @Override
    public Message receive() {
        byte[] byteArray = new byte[0];
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            return (Message)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void send(Message data) {
        try {
            ObjectOutputStream oos = new
                    ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
