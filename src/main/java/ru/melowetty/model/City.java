package ru.melowetty.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class City {
    public String slug;
    public Coords coords;
}
