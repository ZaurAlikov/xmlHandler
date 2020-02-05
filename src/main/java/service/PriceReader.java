package service;

import model.Price;

import java.io.IOException;
import java.util.List;

public interface PriceReader {

    List<? extends Price> readPrice(String filePath) throws IOException;

    List<? extends Price> readPrice(List<String> filePaths) throws IOException;
}
