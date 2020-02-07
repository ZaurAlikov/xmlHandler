package ru.alcotester.pricehandler.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.alcotester.pricehandler.model.ESAPrice;
import ru.alcotester.pricehandler.model.PriceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ESAPriceReader implements PriceReader {

    private String FICO = "Fico";
    private String MENABO = "Menabo";
    private String WHISPBAR = "Whispbar";
    private String ATLANT = "Атлант";
    private String BAGS = "СПОРТ-ТОВАРЫ THULE";

    @Override
    public List<PriceImpl> readPrice(List<String> filePaths) throws IOException {
        List<ESAPrice> esaPrice = getESAPriceList(filePaths);
        for (ESAPrice price : esaPrice) {
            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(new File(price.getPricePath())));
            




        }

        return null;
    }

    private List<ESAPrice> getESAPriceList(List<String> filePaths) {
        List<ESAPrice> esaPriceList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(filePaths)) {
            for (String filePath : filePaths) {
                if (filePath.contains(FICO)) {
                    esaPriceList.add(fillESAPrice(FICO, filePath));
                }
                if (filePath.contains(MENABO)) {
                    esaPriceList.add(fillESAPrice(MENABO, filePath));
                }
                if (filePath.contains(WHISPBAR)) {
                    esaPriceList.add(fillESAPrice(WHISPBAR, filePath));
                }
                if (filePath.contains(ATLANT)) {
                    esaPriceList.add(fillESAPrice(ATLANT, filePath));
                }
                if (filePath.contains(BAGS)) {
                    esaPriceList.add(fillESAPrice(BAGS, filePath));
                }
            }
        }
        return esaPriceList;
    }

    private ESAPrice fillESAPrice(String name, String path) {
        ESAPrice price = new ESAPrice();
        price.setPriceName(name);
        price.setPricePath(path);
        return price;
    }

    @Override
    public List<PriceImpl> readPrice(String filePath) {
        return null;
    }
}
