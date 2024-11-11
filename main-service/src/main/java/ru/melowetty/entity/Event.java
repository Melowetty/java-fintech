package ru.melowetty.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.melowetty.dto.EventDto;
import ru.melowetty.dto.EventShortDto;
import ru.melowetty.dto.EventWithoutPlaceDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    public Place place;

    public EventShortDto toShortDto() {
        return new EventShortDto(id, name, date, place.getId());
    }

    public EventWithoutPlaceDto toWithoutPlaceDto() {
        return new EventWithoutPlaceDto(id, name, date);
    }

    public EventDto toDto() {
        return new EventDto(id, name, date, place.toShortDto());
    }

}