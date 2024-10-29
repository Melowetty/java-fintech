package ru.melowetty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class Category implements Serializable {
    public int id;
    public String slug;
    public String name;
}
