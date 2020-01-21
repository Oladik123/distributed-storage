package ru.nsu.fit.replica;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ReplicaLogStorageImpl implements ReplicaLogStorage {
    private final AtomicLong actionId = new AtomicLong();
    private final Map<Long, ReplicaLog> log = new ConcurrentHashMap<>();

    @Override
    public long getCurrentActionId() {
        return actionId.get();
    }

    @Override
    public long logAction(ReplicaLog replicaLog) {
        long newActionId = actionId.incrementAndGet();
        log.put(newActionId, replicaLog);
        return newActionId;
    }

    @Override
    public void setActionId(long newActionId) {
        actionId.set(newActionId);
    }

    @Override
    public ReplicaLog getReplicaLog(long beginActionId) {
        return log.get(beginActionId);
    }
}
