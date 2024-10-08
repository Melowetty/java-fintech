package ru.melowetty.repository.impl;

import ru.melowetty.repository.BaseRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseRepositoryImpl<E, I> implements BaseRepository<E, I> {
    private final ConcurrentHashMap<I, E> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    @Override
    public E create(E entity) {
        storage.put(getIndexFromEntity(entity), entity);
        idCounter.incrementAndGet();
        return entity;
    }

    @Override
    public void removeById(I id) {
        storage.remove(id);
    }

    @Override
    public E update(E entity) {
        storage.put(getIndexFromEntity(entity), entity);
        return entity;
    }

    @Override
    public E findById(I id) {
        return storage.get(id);
    }

    @Override
    public List<E> findAll() {
        return storage.values().stream().toList();
    }

    @Override
    public boolean existsById(I id) {
        return storage.containsKey(id);
    }

    public int count() {
        return idCounter.intValue();
    }

    protected abstract I getIndexFromEntity(E entity);
}
