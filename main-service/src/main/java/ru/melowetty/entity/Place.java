package ru.melowetty.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.melowetty.dto.PlaceDto;
import ru.melowetty.dto.PlaceShortDto;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "places")
public class Place {
    @Column(nullable = false)
    public String slug;
    @Column(nullable = false)
    public String name;
    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    public List<Event> events;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    public PlaceShortDto toShortDto() {
        return new PlaceShortDto(id, slug, name);
    }

    public PlaceDto toDto() {
        return new PlaceDto(id, slug, name, events.stream().map(Event::toWithoutPlaceDto).toList());
    }
}