package ru.melowetty.repository;

import java.util.List;

public interface BaseRepository<E, I>{
    E create(E entity);
    void removeById(E id);
    E update(E entity);
    E findById(I id);
    List<E> findAll();
}
