package ru.nsu.fit.replica.storage;

import java.util.Map;

public interface Storage {
    void put(String key, String value);
    String get(String key);
    Map<String, String> getCurrentSnapshot();
}
