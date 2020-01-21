package ru.nsu.fit.replica.storage;

import lombok.Data;

@Data
public class StorageMessage {
    private StorageAction action;
    private long actionId;
    private String key;
    private String value;
}
