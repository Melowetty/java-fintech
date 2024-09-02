package ru.melowetty.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City {
    public String slug;
    public Coords coords;

    @Override
    public String toString() {
        return "City{" +
                "slug='" + slug + '\'' +
                ", coords=" + coords +
                '}';
    }
}
