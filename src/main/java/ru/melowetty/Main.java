package ru.melowetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import ru.melowetty.service.CityReaderService;
import ru.melowetty.service.CityWriterService;
import ru.melowetty.service.CityXmlConverterService;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class Main {
    private static final CityReaderService cityReaderService = new CityReaderService(new ObjectMapper());
    private static final CityWriterService cityWriterService = new CityWriterService();
    private static final CityXmlConverterService cityXmlConverterService = new CityXmlConverterService(new XmlMapper());

    public static void main(String[] args) throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите название json-файла с городом:");
            String fileName = scanner.nextLine();

            var city = cityReaderService.readCityFromFile(fileName);

            if (city == null) {
                log.warn("City is null, shutdown program");
                throw new RuntimeException("City is null");
            }

            log.info("Считан город: {}, по пути: {}", city, fileName);

            var cityAsXml = cityXmlConverterService.toXML(city);

            log.info("Город в формате XML: {}", cityAsXml);

            System.out.println("Введите название файла для вывода города в формате XML:");
            String xmlFileName = scanner.nextLine();

            cityWriterService.saveFile(xmlFileName, cityAsXml);

            log.info("Файл сохранен по пути: {}", xmlFileName);
        } catch (RuntimeException e) {
            log.warn("Завершение программы");
            log.error(e.toString());
        }
    }
}