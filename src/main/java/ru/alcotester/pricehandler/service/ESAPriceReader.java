package ru.alcotester.pricehandler.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import ru.alcotester.pricehandler.model.ColumnMapping;
import ru.alcotester.pricehandler.model.ESAPrice;
import ru.alcotester.pricehandler.model.Price;
import ru.alcotester.pricehandler.model.PriceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.alcotester.pricehandler.service.PriceReaderHelper.*;
import static ru.alcotester.pricehandler.service.PriceReaderHelper.checkCellGetBigDec;

public class ESAPriceReader implements PriceReader {

    private String FICO = "Fico";
    private String MENABO = "Menabo";
    private String WHISPBAR = "Whispbar";
    private String ATLANT = "Атлант";
    private String BAGS = "СПОРТ-ТОВАРЫ THULE";
    private String BAGS_TOTAL = "Stock Thule AWK+Luggage";
    private String ROOF_RACKS_TOTAL = "Stock Roof-Rack";

    @Override
    public List<PriceImpl> readPrice(List<String> filePaths) throws IOException {
        List<ESAPrice> esaPricePathList = getESAPriceList(filePaths);
        List<PriceImpl> esaPriceList = new ArrayList<>();
        for (ESAPrice price : esaPricePathList) {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(new File(price.getPricePath())));
            HSSFSheet myExcelSheet = myExcelBook.getSheet("TDSheet");
            List<HSSFRow> rows = new ArrayList<>();
            for (int i = 0; i <= myExcelSheet.getLastRowNum(); i++) {
                rows.add(myExcelSheet.getRow(i));
            }
            if (price.getPriceName().equals(ROOF_RACKS_TOTAL)) {
                if (rows.size() > 0) {
                    for (int i = 0; i < rows.size(); i++) {
                        if (checkCell(rows.get(i).getCell(0)) &&
                                checkCell(rows.get(i).getCell(1)) &&
                                checkCell(rows.get(i).getCell(2)) &&
                                checkCell(rows.get(i).getCell(3)) &&
                                checkCell(rows.get(i).getCell(4)) &&
                                checkCell(rows.get(i).getCell(5))) {
                            PriceImpl esaPrice = new PriceImpl();
                            esaPrice.setProductName(checkCellGetString(rows.get(i).getCell(3)));
                            if (checkCellGetString(rows.get(i).getCell(2)).equals("Атлант") ||
                                    checkCellGetString(rows.get(i).getCell(2)).equals("Вездеход")) {
                                esaPrice.setSKU(trimSKU(checkCellGetString(rows.get(i).getCell(1))));
                            } else {
                                esaPrice.setSKU(checkCellGetString(rows.get(i).getCell(1)));
                            }
                            esaPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(5)));
                            esaPriceList.add(esaPrice);
                        }
                    }
                }
            }
            if (price.getPriceName().equals(BAGS_TOTAL)) {
                if (rows.size() > 0) {
                    for (int i = 0; i < rows.size(); i++) {
                        if (checkCell(rows.get(i).getCell(0)) &&
                                checkCell(rows.get(i).getCell(5)) &&
                                checkCell(rows.get(i).getCell(10)) &&
                                checkCell(rows.get(i).getCell(14)) &&
                                checkCell(rows.get(i).getCell(15))) {
                            PriceImpl esaPrice = new PriceImpl();
                            esaPrice.setProductName(checkCellGetString(rows.get(i).getCell(10)));
                            esaPrice.setSKU(trimSKU(checkCellGetString(rows.get(i).getCell(5))));
                            esaPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(15)));
                            esaPriceList.add(esaPrice);
                        }
                    }
                }
            }
            myExcelBook.close();
        }
        return esaPriceList;
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
                if (filePath.contains(BAGS_TOTAL)) {
                    esaPriceList.add(fillESAPrice(BAGS_TOTAL, filePath));
                }
                if (filePath.contains(ROOF_RACKS_TOTAL)) {
                    esaPriceList.add(fillESAPrice(ROOF_RACKS_TOTAL, filePath));
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

    private boolean checkCell(Cell cell) {
        if (cell == null) {
            return false;
        }
        CellType cellType = cell.getCellType();
        if (cellType.equals(CellType.STRING)) {
            return StringUtils.isNotEmpty(cell.getStringCellValue()) && getCellColor(cell).equals("");
        } else {
            return cellType.equals(CellType.NUMERIC) && getCellColor(cell).equals("");
        }
    }

    private String trimSKU(String sku) {
        return sku.substring(3);
    }

    @Override
    public List<PriceImpl> readPrice(String filePath) {
        return null;
    }

    @Override
    public List<? extends Price> readPrice(String filePath, ColumnMapping columnMapping) {
        return null;
    }
}
