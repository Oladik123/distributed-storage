package ru.nsu.fit.replica.storage.network.multicast;

import com.google.gson.Gson;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import ru.nsu.fit.replica.Message;
import ru.nsu.fit.replica.storage.network.SendingConnection;

public final class MulticastConnectionHost implements SendingConnection {
    private Socket socket;
    private boolean closed;
    private final Gson gson = new Gson();

    public final void host(String address, int port) {
        socket = new ZContext().createSocket(SocketType.PUB);
        socket.bind("tcp://" + address + ':' + port);

        this.closed = false;
    }

    public void send(Message data) {
        if (this.closed) {
            throw new RuntimeException("Client connection is closed");
        }

        socket.send(this.gson.toJson(data));
    }

    public void close() {
        closed = true;
        socket.close();
    }
}