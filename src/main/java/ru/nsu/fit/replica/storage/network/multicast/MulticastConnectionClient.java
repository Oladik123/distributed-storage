package ru.nsu.fit.replica.storage.network.multicast;

import com.google.gson.Gson;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.storage.network.NetworkConnector;
import ru.nsu.fit.replica.storage.network.ReceivingConnection;

public final class MulticastConnectionClient implements NetworkConnector, ReceivingConnection {
    private Socket socket;
    private boolean closed;
    private final Gson gson = new Gson();

    public void connect(String address, int port) {
        socket = (new ZContext()).createSocket(SocketType.SUB);
        Socket socket = this.socket;
        socket.connect("tcp://" + address + ':' + port);
        socket.subscribe("");
        closed = false;
    }

    public Message receive() {
        if (closed) {
            throw new RuntimeException("Client connection is closed");
        }

        return gson.fromJson(new String(socket.recv(), Charsets.UTF_8), Message.class);
    }

    public void close() {
        closed = true;
        socket.close();
    }
}
