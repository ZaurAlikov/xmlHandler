package ru.alcotester.pricehandler.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import ru.alcotester.pricehandler.model.ColumnMapping;
import ru.alcotester.pricehandler.model.Price;
import ru.alcotester.pricehandler.model.PriceImpl;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ru.alcotester.pricehandler.service.PriceReaderHelper.*;

public class ATPriceReader implements PriceReader {

    @Override
    public List<PriceImpl> readPrice(String filePath, ColumnMapping columnMapping) throws IOException {
        List<PriceImpl> aTuningPrices = new ArrayList<>();
        String[] split = filePath.split("\\.");
        if (split.length > 1 && split[1].equals("xls")) {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
            aTuningPrices = fillPrice(myExcelBook, columnMapping);
            myExcelBook.close();
        } else if (split.length > 1 && split[1].equals("xlsx")) {
            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            aTuningPrices = fillPrice(myExcelBook, columnMapping);
            myExcelBook.close();
        } else {
            System.err.println("Что-то не так с путем к прайсу ATuning");
        }
        return aTuningPrices;
    }

    private List<PriceImpl> fillPrice(Workbook myExcelBook, ColumnMapping columnMapping) {
        List<PriceImpl> aTuningPrices = new ArrayList<>();
        String category = "";
        String subCategory = "";
        Sheet myExcelSheet = null;
        int numberOfSheets = myExcelBook.getNumberOfSheets();
        int notHiddenSheets = 0;
        int notHiddenSheetIdx = 0;
        for (int i = 0; i < numberOfSheets; i++) {
            if (!myExcelBook.isSheetHidden(i)) {
                ++notHiddenSheets;
                notHiddenSheetIdx = i;
            }
        }
        if (notHiddenSheets == 1) {
            myExcelSheet = myExcelBook.getSheetAt(notHiddenSheetIdx);
        }
        if (myExcelSheet == null) {
            myExcelSheet = myExcelBook.getSheet("TDSheet");
        }
        if (myExcelSheet == null) {
            throw new IllegalArgumentException("Не удалось определить на каком листе прайса ATuning находятся данные");
        }
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i <= myExcelSheet.getLastRowNum(); i++) {
            if (myExcelSheet.getRow(i).getPhysicalNumberOfCells() > 0) {
                rows.add(myExcelSheet.getRow(i));
            }
        }
        int firstRow = getFirstRow(rows, columnMapping);
        if (rows.size() > 0) {
            for (int i = firstRow; i < rows.size(); i++) {
                if (i == firstRow) {
                    category = checkCellGetString(rows.get(i-1).getCell(columnMapping.getProductName())).trim();
                }
                if (isCat(rows.get(i), columnMapping) && !getCellColor(rows.get(i+1).getCell(0)).equals("")) {
                    category = checkCellGetString(rows.get(i).getCell(columnMapping.getProductName())).trim();
                }
                if (isCat(rows.get(i), columnMapping) && getCellColor(rows.get(i+1).getCell(0)).equals("") && getCellColor(rows.get(i-1).getCell(0)).equals("")) {
                    category = checkCellGetString(rows.get(i).getCell(columnMapping.getProductName())).trim();
                }
                if (!isCat(rows.get(i), columnMapping)) {
                    PriceImpl aTuningPrice = new PriceImpl();
                    aTuningPrice.setProductCategory(category);
                    aTuningPrice.setProductSubCategory(subCategory);
                    aTuningPrice.setProductName(checkCellGetString(rows.get(i).getCell(columnMapping.getProductName())));
                    aTuningPrice.setSKU(checkCellGetString(rows.get(i).getCell(columnMapping.getSku())));
                    if (columnMapping.getRetailPrice() != null) {
                        aTuningPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(columnMapping.getRetailPrice())));
                    } else {
                        aTuningPrice.setRetailPrice(BigDecimal.ZERO);
                    }
                    if (columnMapping.getTradePrice() != null) {
                        aTuningPrice.setTradePrice(checkCellGetBigDec(rows.get(i).getCell(columnMapping.getTradePrice())));
                    }
                    aTuningPrices.add(aTuningPrice);
                }
            }
        }
        cleanPrice(aTuningPrices);
        return aTuningPrices;
    }

    private int getFirstRow(List<Row> rows, ColumnMapping columnMapping) {
        for (int i = 0; i < rows.size(); i++) {
            Integer priceOrAvailabilityColumn = columnMapping.getRetailPrice() != null ? columnMapping.getRetailPrice() : columnMapping.getAvailability();
            if ((StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(columnMapping.getSku()))) && getCellColor(rows.get(i).getCell(columnMapping.getSku())).equals("")) &&
                    (StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(columnMapping.getProductName()))) && getCellColor(rows.get(i).getCell(columnMapping.getProductName())).equals("")) &&
                    (columnMapping.getUnit() == null || StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(columnMapping.getUnit()))) && getCellColor(rows.get(i).getCell(columnMapping.getUnit())).equals("")) &&
                    (StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(priceOrAvailabilityColumn))) && getCellColor(rows.get(i).getCell(priceOrAvailabilityColumn)).equals(""))) {
                return i;
            }
        }
        return 0;
    }

    private boolean isCat(Row row, ColumnMapping columnMapping) {
        if ((StringUtils.isBlank(checkCellGetString(row.getCell(columnMapping.getSku()))) && !getCellColor(row.getCell(columnMapping.getSku())).equals("")) &&
                (StringUtils.isNotBlank(checkCellGetString(row.getCell(columnMapping.getProductName()))) && !getCellColor(row.getCell(columnMapping.getProductName())).equals(""))) {
            return true;
        }
        return false;
    }

    @Override
    public List<? extends Price> readPrice(String filePath) {
        return null;
    }

    @Override
    public List<PriceImpl> readPrice(List<String> filePaths) {
        return null;
    }
}
