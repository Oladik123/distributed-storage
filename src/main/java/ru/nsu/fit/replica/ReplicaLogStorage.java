package ru.nsu.fit.replica;

import java.util.Map;

public interface ReplicaLogStorage {

    long getCurrentActionId();

    long logAction(ReplicaLog replicaLog);

    void setActionId(long actionId);

    ReplicaLog getReplicaLog(long beginActionId);
}
