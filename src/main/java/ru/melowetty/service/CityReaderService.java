package ru.melowetty.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.melowetty.model.City;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@AllArgsConstructor
public class CityReaderService {

    private ObjectMapper mapper;

    public City readCityFromFile(String fileName) {
        try {
            log.debug("Начало считывания города из файла");

            var city = mapper.readValue(Path.of(fileName).toFile(), City.class);

            log.debug("Конец считывания города из файла");

            return city;
        } catch (DatabindException | StreamReadException exception) {
            log.error("Произошла ошибка конвертации файла в объект", exception);
        } catch (IOException ioException) {
            log.error("Произошла ошибка во время чтения файла!", ioException);
        }
        return null;
    }
}
