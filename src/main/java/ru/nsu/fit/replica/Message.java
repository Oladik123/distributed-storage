package ru.nsu.fit.replica;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class Message implements Serializable {
    public static final String ACTION_ID_HEADER_KEY = "action_id";
    public static final String KEY_HEADER_KEY = "key";
    public static final String ACTION_HEADER_KEY = "action";
    public static final String CLIENT_ID_HEADER_KEY = "client_id";

    private String data;

    private HashMap<String, String> headers = new HashMap<>(); // иначе ломбок делает immutable map в которой не работает put

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public Message() {}
    public Message(String data, HashMap<String, String> headers) {
        this.data = data;
        this.headers = headers;
    }
}
