package ru.alcotester.pricehandler.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import ru.alcotester.pricehandler.model.ColumnMapping;
import ru.alcotester.pricehandler.model.Price;
import ru.alcotester.pricehandler.model.PriceImpl;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ru.alcotester.pricehandler.service.PriceReaderHelper.checkCellGetBigDec;
import static ru.alcotester.pricehandler.service.PriceReaderHelper.checkCellGetString;

public class EDPriceReader implements PriceReader {
    @Override
    public List<PriceImpl> readPrice(String filePath) throws IOException {
        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
        List<HSSFRow> rows = new ArrayList<>();
        List<PriceImpl> eurodetalPrices = new ArrayList<>();
        String category = "";
        String subCategory = "";
        for (int i = 0; i <= myExcelSheet.getLastRowNum(); i++) {
            rows.add(myExcelSheet.getRow(i));
        }
        int firstRow = getFirstRow(rows);
        if (rows.size() > 0) {
            for (int i = firstRow; i < rows.size(); i++) {
                if (i == firstRow && i != 0) {
                    category = checkCellGetString(rows.get(i-1).getCell(2)).trim();
                }
                if (isCat(rows.get(i))) {
                    category = checkCellGetString(rows.get(i).getCell(2)).trim();
                } else {
                    PriceImpl eurodetalPrice = new PriceImpl();
                    eurodetalPrice.setProductCategory(category);
                    eurodetalPrice.setProductSubCategory(subCategory);
                    eurodetalPrice.setProductName(checkCellGetString(rows.get(i).getCell(2)).trim());
                    eurodetalPrice.setSKU(checkCellGetString(rows.get(i).getCell(1)));
                    eurodetalPrice.setTradePrice(checkCellGetBigDec(rows.get(i).getCell(3)));
                    if (StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(6)))) {
                        eurodetalPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(6)));
                    } else {
                        BigDecimal retailPrice = eurodetalPrice.getTradePrice().multiply(new BigDecimal("1.5"));
                        eurodetalPrice.setRetailPrice(retailPrice);
                    }
                    eurodetalPrices.add(eurodetalPrice);
                }
//                if (rows.get(i).getCell(1) != null) {
//                    String SKUCell = checkCellGetString(rows.get(i).getCell(1));
//                    if (!SKUCell.contains("ED")) {
//                        category = SKUCell;
//                    } else {
//                        PriceImpl eurodetalPrice = new PriceImpl();
//                        eurodetalPrice.setProductCategory(category);
//                        eurodetalPrice.setProductSubCategory(subCategory);
//                        eurodetalPrice.setProductName(checkCellGetString(rows.get(i).getCell(2)));
//                        eurodetalPrice.setSKU(checkCellGetString(rows.get(i).getCell(1)));
//                        eurodetalPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(6)));
//                        eurodetalPrice.setTradePrice(checkCellGetBigDec(rows.get(i).getCell(5)));
//                        eurodetalPrices.add(eurodetalPrice);
//                    }
//                }
            }
        }
        return eurodetalPrices;
    }

    private int getFirstRow(List<HSSFRow> rows) {
        for (int i = 0; i < rows.size(); i++) {
            if (StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(1))) &&
                    StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(2))) &&
                            StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(3))) &&
                                    StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(4))) &&
                                            StringUtils.isNotBlank(checkCellGetString(rows.get(i).getCell(5))) &&
                                                rows.get(i).getCell(5).getCellType() == CellType.NUMERIC) {
                return i;
            }
        }
        return 0;
    }

    private boolean isCat(HSSFRow row) {
        if (StringUtils.isBlank(checkCellGetString(row.getCell(1))) &&
                StringUtils.isNotBlank(checkCellGetString(row.getCell(2))) &&
                    StringUtils.isBlank(checkCellGetString(row.getCell(3))) &&
                        StringUtils.isBlank(checkCellGetString(row.getCell(4))) &&
                                StringUtils.isBlank(checkCellGetString(row.getCell(5)))) {
            return true;
        }
        return false;
    }

    @Override
    public List<PriceImpl> readPrice(List<String> filePaths) {
        return null;
    }

    @Override
    public List<? extends Price> readPrice(String filePath, ColumnMapping columnMapping) {
        return null;
    }
}
