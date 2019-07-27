package service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import model.BerivdoroguProducts;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BerivdoroguPriceReader implements PriceReader {
    @Override
    public List<BerivdoroguProducts> readPrice(String filePath) throws IOException {
        List<BerivdoroguProducts> priceList = new ArrayList<>();
        CSVReaderBuilder builder = new CSVReaderBuilder(new FileReader(filePath));
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(';')
                .withQuoteChar('"')
                .build();
        try (CSVReader reader = builder.withSkipLines(1).withCSVParser(csvParser).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                BerivdoroguProducts price = new BerivdoroguProducts();
                price.setProductName(nextLine[1]);
                price.setSKU(nextLine[3]);
                price.setRetailPrice(new BigDecimal(Double.parseDouble(nextLine[5])));
                price.setStatus(Integer.parseInt(nextLine[12]) == 1);
                price.setModel(nextLine[2]);
                price.setQuantity(nextLine[6]);
                priceList.add(price);
            }
        }
        return priceList;
    }
}
