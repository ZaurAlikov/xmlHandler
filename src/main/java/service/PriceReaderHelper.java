package service;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.math.BigDecimal;
import java.util.List;

class PriceReaderHelper {

    static String checkCellGetString(XSSFCell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(new Double(cell.getNumericCellValue()).intValue());
        }
        return "";
    }

    static BigDecimal checkCellGetBigDec(XSSFCell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue());
        }
        return BigDecimal.ZERO;
    }

    static String getColor(List<XSSFRow> rows, int rowNum, int cellNum) {
        String color = "";
        XSSFColor fillForegroundXSSFColor = rows.get(rowNum).getCell(cellNum).getCellStyle().getFillForegroundXSSFColor();
        if (fillForegroundXSSFColor != null) {
            color = fillForegroundXSSFColor.getARGBHex().substring(2);
        }
        return color;
    }
}
