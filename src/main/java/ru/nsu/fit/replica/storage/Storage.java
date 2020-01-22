package ru.nsu.fit.replica.storage;

public interface Storage<K, V> {
    void put(K key, V value);
    V get(K key);
}
