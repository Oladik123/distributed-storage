package ru.nsu.fit.replica.storage.client;

import ru.nsu.fit.replica.storage.Storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageClient implements Storage {

    private Map<String, String> map = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public Map<String, String> getCurrentSnapshot() {
        return new ConcurrentHashMap<>(map);
    }
}
