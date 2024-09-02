package ru.melowetty.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.melowetty.model.City;

@AllArgsConstructor
@Slf4j
public class CityXmlConverterService {
    private XmlMapper mapper;

    public String toXML(City city) {
        try {
            log.debug("Начало конвертации города в XML");
            var xml = mapper.writeValueAsString(city);
            log.debug("Конец конвертации города в XML");
            return xml;
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка во время сериализации объекта City в XML");
            log.error(e.toString());
        }

        return null;
    }
}
