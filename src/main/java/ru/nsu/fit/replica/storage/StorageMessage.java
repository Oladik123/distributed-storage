package ru.nsu.fit.replica.storage;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageMessage<K, V> implements Serializable {
    private StorageAction action;
    private long actionId;
    private K key;
    private V value;

    public StorageAction getAction() {
        return action;
    }

    public void setAction(StorageAction action) {
        this.action = action;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
