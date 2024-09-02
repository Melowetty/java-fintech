package ru.melowetty.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor
@Slf4j
public class CityWriterService {
    public void saveFile(String fileName, String data) {
        try {
            log.debug("Начало сохранения города в файл");
            Files.writeString(Path.of(fileName), data);
            log.debug("Конец сохранения города в файл");
        } catch (IOException e) {
            log.error("Произошла ошибка во время сохранения файла!");
            log.error(e.getMessage());
        }
    }
}
