package ru.alcotester.pricehandler.service;

import ru.alcotester.pricehandler.model.ColumnMapping;
import ru.alcotester.pricehandler.model.Price;

import java.io.IOException;
import java.util.List;

public interface PriceReader {

    List<? extends Price> readPrice(String filePath) throws IOException;

    List<? extends Price> readPrice(List<String> filePaths) throws IOException;

    List<? extends Price> readPrice(String filePath, ColumnMapping columnMapping) throws IOException;
}
