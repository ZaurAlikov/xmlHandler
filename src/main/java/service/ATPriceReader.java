package service;

import model.PriceImpl;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static service.PriceReaderHelper.checkCellGetBigDec;
import static service.PriceReaderHelper.checkCellGetString;
import static service.PriceReaderHelper.getColor;

public class ATPriceReader implements PriceReader {

    @Override
    public List<PriceImpl> readPrice(String filePath) throws IOException {
        XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
        XSSFSheet myExcelSheet = myExcelBook.getSheet("TDSheet");
        List<XSSFRow> rows = new ArrayList<>();
        List<PriceImpl> aTuningPrices = new ArrayList<>();
        String category = "";
        String subCategory = "";
        int firstRow = 15;
        for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
            rows.add(myExcelSheet.getRow(i));
        }
        if (rows.size() > 0) {

            for (int i = firstRow; i < rows.size(); i++) {
//                if (getColor(rows, i, 0).equals("FBF9EC") && getColor(rows, i+1, 0).equals("FBF9EC")) {
//                    category = checkCellGetString(rows.get(i).getCell(0));
//                    subCategory = checkCellGetString(rows.get(i+1).getCell(0));
//                } else if (getColor(rows, i, 0).equals("FBF9EC") && getColor(rows, i-1, 0).equals("") && getColor(rows, i+1, 0).equals("")) {
//                    category = checkCellGetString(rows.get(i).getCell(0));
//                    subCategory = "";
//                }
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
        return aTuningPrices;
    }
}
