package ru.nsu.fit.replica;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Message implements Serializable {
    private byte[] data;

    public Message(byte[] data) {
        this.data = data;
    }

    public Message() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private Map<String, String> headers;

}
