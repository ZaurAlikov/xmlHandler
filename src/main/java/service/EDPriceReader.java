package service;

import model.PriceImpl;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static service.PriceReaderHelper.checkCellGetBigDec;
import static service.PriceReaderHelper.checkCellGetString;

public class EDPriceReader implements PriceReader {
    @Override
    public List<PriceImpl> readPrice(String filePath) throws IOException {
        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet myExcelSheet = myExcelBook.getSheet("Боксы,лыжные крепления");
        List<HSSFRow> rows = new ArrayList<>();
        List<PriceImpl> eurodetalPrices = new ArrayList<>();
        String category = "";
        String subCategory = "";
        int firstRow = 7;
        for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
            rows.add(myExcelSheet.getRow(i));
        }
        if (rows.size() > 0) {
            for (int i = firstRow; i < rows.size(); i++) {
                if (rows.get(i).getCell(1) != null) {
                    String SKUCell = checkCellGetString(rows.get(i).getCell(1));
                    if (!SKUCell.contains("ED")) {
                        category = SKUCell;
                    } else {
                        PriceImpl eurodetalPrice = new PriceImpl();
                        eurodetalPrice.setProductCategory(category);
                        eurodetalPrice.setProductSubCategory(subCategory);
                        eurodetalPrice.setProductName(checkCellGetString(rows.get(i).getCell(2)));
                        eurodetalPrice.setSKU(checkCellGetString(rows.get(i).getCell(1)));
                        eurodetalPrice.setRetailPrice(checkCellGetBigDec(rows.get(i).getCell(6)));
                        eurodetalPrice.setTradePrice(checkCellGetBigDec(rows.get(i).getCell(5)));
                        eurodetalPrices.add(eurodetalPrice);
                    }
                }
                }

        }
        return eurodetalPrices;
    }
}
