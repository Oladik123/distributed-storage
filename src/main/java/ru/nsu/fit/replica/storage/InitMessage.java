package ru.nsu.fit.replica.storage;

import lombok.Data;

import java.util.Map;

@Data
public class InitMessage {
    private Map<String, String> storage;
    private long lastId;
}
