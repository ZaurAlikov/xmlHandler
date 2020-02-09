package ru.alcotester.pricehandler.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class PriceReaderHelper {

    static String checkCellGetString(XSSFCell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(new Double(cell.getNumericCellValue()).intValue());
        }
        return "";
    }

    static String checkCellGetString(HSSFCell cell) {
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

    static BigDecimal checkCellGetBigDec(HSSFCell cell) {
        BigDecimal result = BigDecimal.ZERO;
        if (cell.getCellType() == CellType.NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.STRING) {
            String stringCellValue = cell.getStringCellValue();
            String replace = stringCellValue
                    .replace("р.", "")
                    .replace(" ", "")
                    .replace(",", ".");

            try {
                result = new BigDecimal(Double.parseDouble(replace));
            } catch (NumberFormatException e) {
                System.err.println("Значение " + replace + " не может быть преобразованно в BigDecimal");
            }
        }
        return result;
    }

    static String getColor(List<?> rows, int rowNum, int cellNum) {
        String color = "";
        if (CollectionUtils.isNotEmpty(rows)) {
            if (rows.get(0) instanceof XSSFRow) {
                XSSFColor fillForegroundXSSFColor = ((XSSFRow)rows.get(rowNum)).getCell(cellNum).getCellStyle().getFillForegroundXSSFColor();
                if (fillForegroundXSSFColor != null) {
                    color = fillForegroundXSSFColor.getARGBHex().substring(2);
                }
            } else if (rows.get(0) instanceof HSSFRow) {
                HSSFColor fillForegroundXSSFColor = ((HSSFRow)rows.get(rowNum)).getCell(cellNum).getCellStyle().getFillForegroundColorColor();
                color = getARGBHex(fillForegroundXSSFColor.getHexString());
            }
        }
        return Objects.equals(color, "000000") ? "" : color;
    }

    private static String getARGBHex(String hexString) {
        StringBuilder result = new StringBuilder();
        String[] split = hexString.split(":");
        for (String s : split) {
            if (s.length() == 4) {
                result.append(s.substring(0,2));
            } else if (s.length() == 1) {
                result.append(s).append(s);
            }
        }
        return result.toString();
    }

    static public String roundBigDec(BigDecimal val) {
        return String.valueOf(val.intValue()/10*10);
    }



}
