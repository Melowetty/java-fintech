package ru.melowetty.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class EventManager<E> {
    HashMap<EventType, List<EventListener<E>>> listeners = new HashMap<>();

    public EventManager() {
        for(var eventType : EventType.values()) {
            listeners.put(eventType, new ArrayList<>());
        }
    }

    public void notify(EventType eventType, E event) {
        for(var listener : listeners.get(eventType)) {
            listener.handleUpdate(eventType, event);
        }
    }

    public void subscribe(EventType eventType, EventListener<E> listener) {
        listeners.get(eventType).add(listener);
    }

    public void unSubscribe(EventType eventType, EventListener<E> listener) {
        listeners.get(eventType).remove(listener);
    }

    public void removeAllSubscribers() {
        listeners.replaceAll((t, v) -> new ArrayList<>());
    }
}
