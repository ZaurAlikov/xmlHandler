package ru.alcotester.pricehandler.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import ru.alcotester.pricehandler.model.BerivdoroguProducts;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class BerivdoroguPriceReader implements PriceReader {

    String CATEGORY = "_CATEGORY_";
    String NAME = "_NAME_";
    String MODEL = "_MODEL_";
    String SKU = "_SKU_";
    String MANUFACTURER = "_MANUFACTURER_";
    String PRICE  = "_PRICE_";
    String QUANTITY = "_QUANTITY_";
    String META_TITLE = "_META_TITLE_";
    String META_DESCRIPTION = "_META_DESCRIPTION_";
    String DESCRIPTION = "_DESCRIPTION_";
    String IMAGE = "_IMAGE_";
    String SORT_ORDER = "_SORT_ORDER_";
    String STATUS = "_STATUS_";
    String SEO_KEYWORD = "_SEO_KEYWORD_";
    String ATTRIBUTES = "_ATTRIBUTES_";
    String IMAGES = "_IMAGES_";

    @Override
    public List<BerivdoroguProducts> readPrice(String filePath) throws IOException {
        List<BerivdoroguProducts> priceList = new ArrayList<>();
        CSVReaderBuilder builder = new CSVReaderBuilder(new FileReader(filePath));
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(';')
                .withQuoteChar('"')
                .build();

        ListIterator<String[]> listIterator = builder.withCSVParser(csvParser).build().readAll().listIterator();
        String[] nextLine;
        HashMap<String, Integer> catToIndex = new HashMap<>();
        while (listIterator.hasNext()) {
            if (listIterator.previousIndex() == -1) {
                nextLine = listIterator.next();
                for (int i = 0; i < nextLine.length; i++) {
                    catToIndex.put(nextLine[i], i);
                }
                continue;
            }
            nextLine = listIterator.next();
            BerivdoroguProducts price = new BerivdoroguProducts();
            price.setBdCategory(getCatIndex(catToIndex, CATEGORY, nextLine));
            price.setProductName(getCatIndex(catToIndex, NAME, nextLine));
            price.setModel(getCatIndex(catToIndex, MODEL, nextLine));
            price.setSKU(getCatIndex(catToIndex, SKU, nextLine));
            price.setManufacturer(getCatIndex(catToIndex, MANUFACTURER, nextLine));
            price.setRetailPrice(getPrice(getCatIndex(catToIndex, PRICE, nextLine)));
            price.setQuantity(Integer.parseInt(getCatIndex(catToIndex, QUANTITY, nextLine)));
            price.setMetaTitle(getCatIndex(catToIndex, META_TITLE, nextLine));
            price.setMetaDescription(getCatIndex(catToIndex, META_DESCRIPTION, nextLine));
            price.setDescription(getCatIndex(catToIndex, DESCRIPTION, nextLine));
            price.setImage(getCatIndex(catToIndex, IMAGE, nextLine));
            price.setSortOrder(Integer.parseInt(getCatIndex(catToIndex, SORT_ORDER, nextLine)));
            price.setStatus(Integer.parseInt(getCatIndex(catToIndex, STATUS, nextLine)) == 1);
            price.setSeoKeyword(getCatIndex(catToIndex, SEO_KEYWORD, nextLine));
            price.setAtributes(getCatIndex(catToIndex, ATTRIBUTES, nextLine));
            price.setImages(getCatIndex(catToIndex, IMAGES, nextLine));
            priceList.add(price);
        }
        return priceList;
    }

    private String getCatIndex(HashMap<String, Integer> catToIndex, String catName, String[] line) {
        Integer catNum = catToIndex.get(catName);
        if (catNum != null) {
            return line[catNum];
        }
        return "";
    }

    private BigDecimal getPrice(String strPrice) {
        BigDecimal result = BigDecimal.ZERO;
        if (StringUtils.isNotEmpty(strPrice)) {
            try {
                result = BigDecimal.valueOf(Double.parseDouble(strPrice));
            } catch (Exception e) {
                System.err.println("Что-то не так с форматом стоимости");
                e.getStackTrace();
            }
            return result;
        }
        System.err.println("Что-то не так со стоимостью, есть нулевые значения");
        return result;
    }

    @Override
    public List<BerivdoroguProducts> readPrice(List<String> filePaths) {
        return null;
    }
}
