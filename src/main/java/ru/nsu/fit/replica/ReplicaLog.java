package ru.nsu.fit.replica;

import lombok.Data;
import ru.nsu.fit.replica.storage.StorageAction;

@Data
public class ReplicaLog {
    private StorageAction storageAction;
    private String key;
    private String value;
}
