package ru.nsu.fit.replica.storage.client;

import ru.nsu.fit.replica.storage.Storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageClient<K, V> implements Storage<K, V> {

    private final Map<K, V> storage = new ConcurrentHashMap<>();

    @Override
    public void put(K key, V value) {
        storage.put(key, value);
    }

    @Override
    public V get(K key) {
        return storage.get(key);
    }
}
