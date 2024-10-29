package ru.melowetty.event;

public interface EventListener<E> {
    void handleUpdate(EventType eventType, E event);
}
