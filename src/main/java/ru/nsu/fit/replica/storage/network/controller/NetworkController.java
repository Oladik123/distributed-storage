package ru.nsu.fit.replica.storage.network.controller;

import java.util.UUID;

import ru.nsu.fit.replica.Message;

public interface NetworkController {
    /**
     * Получение сообщения из очереди сообщений
     * @return возвращает сообщение с хедером, по которому
     * можно определить идентификатор отправителя.
     */
    Message getMessage();

    /**
     * Отправить сообщение по заданному айди.
     * В этом методе проставляется хедер отправителя
     * @param nodeId айди получателя
     */
    void writeMessage(UUID nodeId, Message message);

    /**
     * Отправить сообщение. Врайтер отправляет сообщение в бродкаст, ридер - врайтеру
     */
    void writeMessage(Message message);
}
