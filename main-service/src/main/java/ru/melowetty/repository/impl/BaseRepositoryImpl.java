package ru.melowetty.repository.impl;

import lombok.extern.slf4j.Slf4j;
import ru.melowetty.repository.BaseRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class BaseRepositoryImpl<E extends Serializable, I extends Serializable> implements BaseRepository<E, I> {
    private ConcurrentHashMap<I, E> storage = new ConcurrentHashMap<>();
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

    public String backup() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(storage);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    public void restore(String state) {
        try {
            byte[] data = Base64.getDecoder().decode(state);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            this.storage = (ConcurrentHashMap<I, E>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException e) {
            log.error("Класс для восстановления бэкапа не найден", e);
        } catch (IOException e) {
            log.error("Произошла ошибка при восстановлении состояния репозитория", e);
        }
    }
}
