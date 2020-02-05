package service;

import model.PriceImpl;

import java.io.IOException;
import java.util.List;

public class ESAPriceReader implements PriceReader {

    @Override
    public List<PriceImpl> readPrice(List<String> filePaths) throws IOException {
        return null;
    }

    @Override
    public List<PriceImpl> readPrice(String filePath) {
        return null;
    }
}
