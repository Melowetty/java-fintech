package ru.melowetty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.melowetty.entity.Place;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("SELECT p " +
            "FROM Place p " +
            "JOIN FETCH p.events " +
            "WHERE p.id = ?1")
    Optional<Place> getPlaceById(Long id);
}
