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
                price.setBdCategory(nextLine[0]);
                price.setProductName(nextLine[1]);
                price.setModel(nextLine[2]);
                price.setSKU(nextLine[3]);
                price.setManufacturer(nextLine[4]);
                price.setRetailPrice(new BigDecimal(Double.parseDouble(nextLine[5])));
                price.setQuantity(Integer.parseInt(nextLine[6]));
                price.setMetaTitle(nextLine[7]);
                price.setMetaDescription(nextLine[8]);
                price.setDescription(nextLine[9]);
                price.setImage(nextLine[10]);
                price.setSortOrder(Integer.parseInt(nextLine[11]));
                price.setStatus(Integer.parseInt(nextLine[12]) == 1);
                price.setSeoKeyword(nextLine[13]);
                price.setAtributes(nextLine[14]);
                price.setImages(nextLine[15]);
                priceList.add(price);
            }
        }
        return priceList;
    }

    @Override
    public List<BerivdoroguProducts> readPrice(List<String> filePaths) {
        return null;
    }
}
