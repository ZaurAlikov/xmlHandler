package ru.alcotester.pricehandler.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ru.alcotester.pricehandler.model.PriceImpl;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.alcotester.pricehandler.service.PriceReaderHelper.checkCellGetBigDec;
import static ru.alcotester.pricehandler.service.PriceReaderHelper.checkCellGetString;
import static ru.alcotester.pricehandler.service.PriceReaderHelper.getColor;

public class ATPriceReader implements PriceReader {

    @Override
    public List<PriceImpl> readPrice(String filePath) throws IOException {
        List<PriceImpl> aTuningPrices = new ArrayList<>();
        String category = "";
        String subCategory = "";
        int firstRow = 15;
        String[] split = filePath.split("\\.");
        if (split.length > 1 && split[1].equals("xls")) {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
            HSSFSheet myExcelSheet = myExcelBook.getSheet("TDSheet");
            List<HSSFRow> rows = new ArrayList<>();
            for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
                rows.add(myExcelSheet.getRow(i));
            }
            if (rows.size() > 0) {
                for (int i = firstRow; i < rows.size(); i++) {
                    if (i == firstRow) {
                        category = checkCellGetString(rows.get(i).getCell(0));
                    }
                    if (getColor(rows, i, 0).equals("FBF9EC") && getColor(rows, i+1, 0).equals("FBF9EC")) {
                        category = checkCellGetString(rows.get(i).getCell(0));
                    }
                    if (getColor(rows, i, 0).equals("FBF9EC") &&
                            getColor(rows, i+1, 0).equals("") &&
                            getColor(rows, i-1, 0).equals("")) {
                        category = checkCellGetString(rows.get(i).getCell(0));
                    }

                    if (getColor(rows, i, 0).equals("")) {
                        PriceImpl aTuningPrice = new PriceImpl();
                        aTuningPrice.setProductCategory(category);
                        aTuningPrice.setProductSubCategory(subCategory);
                        aTuningPrice.setProductName(checkCellGetString(rows.get(i).getCell(0)));
                        aTuningPrice.setSKU(checkCellGetString(rows.get(i).getCell(12)));
                        aTuningPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(14)));
                        aTuningPrice.setTradePrice(checkCellGetBigDec(rows.get(i).getCell(15)));
                        aTuningPrices.add(aTuningPrice);
                    }
                }
            }
            myExcelBook.close();
        } else if (split.length > 1 && split[1].equals("xlsx")) {
            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            XSSFSheet myExcelSheet = myExcelBook.getSheet("TDSheet");
            List<XSSFRow> rows = new ArrayList<>();
            for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
                rows.add(myExcelSheet.getRow(i));
            }
            if (rows.size() > 0) {
                for (int i = firstRow; i < rows.size(); i++) {
                    if (i == firstRow) {
                        category = checkCellGetString(rows.get(i).getCell(0));
                    }
                    if (getColor(rows, i, 0).equals("FBF9EC") && getColor(rows, i+1, 0).equals("FBF9EC")) {
                        category = checkCellGetString(rows.get(i).getCell(0));
                    }
                    if (getColor(rows, i, 0).equals("FBF9EC") &&
                            getColor(rows, i+1, 0).equals("") &&
                            getColor(rows, i-1, 0).equals("")) {
                        category = checkCellGetString(rows.get(i).getCell(0));
                    }

                    if (getColor(rows, i, 0).equals("")) {
                        PriceImpl aTuningPrice = new PriceImpl();
                        aTuningPrice.setProductCategory(category);
                        aTuningPrice.setProductSubCategory(subCategory);
                        aTuningPrice.setProductName(checkCellGetString(rows.get(i).getCell(0)));
                        aTuningPrice.setSKU(checkCellGetString(rows.get(i).getCell(12)));
                        aTuningPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(14)));
                        aTuningPrice.setTradePrice(checkCellGetBigDec(rows.get(i).getCell(15)));
                        aTuningPrices.add(aTuningPrice);
                    }
                }
            }
            myExcelBook.close();
        }
        return aTuningPrices;
    }

    @Override
    public List<PriceImpl> readPrice(List<String> filePaths) {
        return null;
    }
}
