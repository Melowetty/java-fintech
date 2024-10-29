package ru.melowetty.repository;

import java.util.List;

public interface BaseRepository<E, I> extends Backupable {
    E create(E entity);

    void removeById(I id);

    E update(E entity);

    E findById(I id);

    List<E> findAll();

    boolean existsById(I id);
}
