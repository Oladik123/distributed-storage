package ru.nsu.fit.replica.storage;

import lombok.Data;

import java.util.Map;

@Data
public class InitMessage<K,V> {
    private Map<K, V> storage;
    private long lastId;
}
