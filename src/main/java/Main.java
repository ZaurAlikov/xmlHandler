import model.ATuningPrice;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        readFromExcel("/home/user/Загрузки/Прайс туле 3 с 23.07.2019.xlsx");
    }

    public static void readFromExcel(String filePath) throws IOException {
        XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
        XSSFSheet myExcelSheet = myExcelBook.getSheet("TDSheet");
        List<XSSFRow> rows = new ArrayList<>();
        List<ATuningPrice> aTuningPrices = new ArrayList<>();
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
                    ATuningPrice aTuningPrice = new ATuningPrice();
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
        System.out.println(aTuningPrices);





        myExcelBook.close();
    }

    private static String checkCellGetString(XSSFCell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(new Double(cell.getNumericCellValue()).intValue());
        }
        return "";
    }

    private static BigDecimal checkCellGetBigDec(XSSFCell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue());
        }
        return BigDecimal.ZERO;
    }

    private static String getColor(List<XSSFRow> rows, int rowNum, int cellNum) {
        String color = "";
        XSSFColor fillForegroundXSSFColor = rows.get(rowNum).getCell(cellNum).getCellStyle().getFillForegroundXSSFColor();
        if (fillForegroundXSSFColor != null) {
            color = fillForegroundXSSFColor.getARGBHex().substring(2);
        }
        return color;
    }

}
