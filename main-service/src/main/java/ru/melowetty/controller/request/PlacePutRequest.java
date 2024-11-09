package ru.melowetty.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class PlacePutRequest {
    public String slug;
    public String name;
}
