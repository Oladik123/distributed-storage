package ru.nsu.fit.replica;

import ru.nsu.fit.replica.storage.server.StorageReaderServer;
import ru.nsu.fit.replica.storage.server.StorageReaderServerImpl;
import ru.nsu.fit.replica.storage.server.StorageWriterServer;
import ru.nsu.fit.replica.storage.server.StorageWriterServerImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final StorageWriterServer<String, String> writer = new StorageWriterServerImpl<>();
        final ArrayList<StorageReaderServer<String, String>> readers = new ArrayList<>();
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            readers.add(new StorageReaderServerImpl<>());
        }
        final List<Thread> threads = readers.stream().map(r -> new Thread(r::initalSync)).collect(Collectors.toList());
        threads.forEach(Thread::start);

        final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ready");
        System.out.print("> ");
        input.lines().forEach(s -> {
            if (s.startsWith("put")) {
                String[] words = s.split(" ");
                if (words.length != 3) {
                    System.err.println("error");
                    return;
                }
                writer.put(words[1], words[2]);
            } else if (s.startsWith("get")) {
                String[] words = s.split(" ");
                if (words.length != 2) {
                    System.err.println("error");
                } else {
                    for (int i = 0; i < readers.size(); i++) {
                        System.out.print("Reader " + i + ": ");
                        System.out.println(readers.get(i).get(words[1]));
                    }
                }
            }
            System.out.print("> ");
        });
    }

}
