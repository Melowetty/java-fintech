package ru.melowetty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.melowetty.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
