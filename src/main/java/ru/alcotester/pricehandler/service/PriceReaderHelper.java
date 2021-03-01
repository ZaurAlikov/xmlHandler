package ru.alcotester.pricehandler.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import ru.alcotester.pricehandler.model.ColumnMapping;
import ru.alcotester.pricehandler.model.PriceImpl;
import ru.alcotester.pricehandler.model.VendorEnum;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class PriceReaderHelper {

    public static String checkCellGetString(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(new Double(cell.getNumericCellValue()).intValue());
        }
        return "";
    }

//    static String checkCellGetString(HSSFCell cell) {
//        if (cell.getCellType() == CellType.STRING) {
//            return cell.getStringCellValue();
//        }
//        if (cell.getCellType() == CellType.NUMERIC) {
//            return String.valueOf(new Double(cell.getNumericCellValue()).intValue());
//        }
//        return "";
//    }

//    static BigDecimal checkCellGetBigDec(XSSFCell cell) {
//        if (cell.getCellType() == CellType.NUMERIC) {
//            return new BigDecimal(cell.getNumericCellValue());
//        }
//        return BigDecimal.ZERO;
//    }

    public static BigDecimal checkCellGetBigDec(Cell cell) {
        BigDecimal result = BigDecimal.ZERO;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.STRING) {
            String stringCellValue = cell.getStringCellValue();
            String replace = stringCellValue
                    .replace("р.", "")
                    .replace(" ", "")
                    .replace(",", ".");

            try {
                result = BigDecimal.valueOf(Double.parseDouble(replace));
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

    static <T> String getCellColor(T cell) {
        String color = "";
        if (cell.getClass().equals(HSSFCell.class)) {
            HSSFColor fillForegroundXSSFColor = ((HSSFCell)cell).getCellStyle().getFillForegroundColorColor();
            color = getARGBHex(fillForegroundXSSFColor.getHexString());
        } else if (cell.getClass().equals(XSSFCell.class)) {
            XSSFColor fillForegroundXSSFColor = ((XSSFCell)cell).getCellStyle().getFillForegroundXSSFColor();
            if (fillForegroundXSSFColor != null) {
                color = fillForegroundXSSFColor.getARGBHex().substring(2);
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

    public static String roundBigDec(BigDecimal val) {
        return String.valueOf(val.intValue()/10*10);
    }

    public static String createPriceFolders() {
        return createDateNameFolder(createMainPath());
    }

    public static String createResultFolders() {
        String mainPath = createMainPath();
        return createDateNameFolder(checkAndCreateDir(mainPath + File.separator + "result"));
    }

    public static String createMainPath() {
        String mainPath = System.getProperty("user.dir");
        return checkAndCreateDir(mainPath + File.separator + "csvPrices");
    }

    public static String bdPricePathExist() {
        File file = new File(System.getProperty("user.dir") + File.separator + "csvPrices" + File.separator + "csv_price_export.csv");
        return file.exists() ? file.getPath() : "";
    }

    public static String checkAndCreateDir(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.getPath();
    }

    public static String getLastDateFolderName(List<String> pathList) {
        List<Calendar> dates = new ArrayList<>();
        for (String path : pathList) {
            if (path.contains("result")) {
                continue;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(path.substring(0,2)));
            calendar.set(Calendar.MONTH, Integer.parseInt(path.substring(3,5)) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(path.substring(6,10)));
//            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(path.substring(9,11)));
//            calendar.set(Calendar.MINUTE, Integer.parseInt(path.substring(11,13)));
//            calendar.set(Calendar.SECOND, Integer.parseInt(path.substring(13,15)));
            dates.add(calendar);
        }
        dates.sort(Calendar::compareTo);
        Calendar calendar = dates.get(dates.size() - 1);
        return getDirNameByDate(calendar);
    }

    public static ColumnMapping getColumnMapping(VendorEnum vendor, List<ColumnMapping> columnMappingList) {
        return columnMappingList.stream().filter(cm -> vendor == cm.getVendor()).findFirst().orElse(null);
    }

    private static String createDateNameFolder(String mainPath) {
        Calendar now = Calendar.getInstance();
        return checkAndCreateDir(mainPath + File.separator + getDirNameByDate(now));
    }

    private static String getDirNameByDate(Calendar calendar) {
        StringBuilder nowDirName = new StringBuilder();
        DecimalFormat mFormat= new DecimalFormat("00");
        nowDirName.append(mFormat.format(Double.valueOf(calendar.get(Calendar.DAY_OF_MONTH))))
                .append("_")
                .append(mFormat.format(Double.valueOf(calendar.get(Calendar.MONTH)+1)))
                .append("_")
                .append(calendar.get(Calendar.YEAR));
//                .append("_")
//                .append(mFormat.format(Double.valueOf(calendar.get(Calendar.HOUR_OF_DAY))))
//                .append(mFormat.format(Double.valueOf(calendar.get(Calendar.MINUTE))))
//                .append(mFormat.format(Double.valueOf(calendar.get(Calendar.SECOND))));
        return nowDirName.toString();
    }

    public static List<PriceImpl> cleanPrice(List<PriceImpl> price) {
        price.removeIf(p -> StringUtils.isBlank(p.getSKU()) || StringUtils.isBlank(p.getProductName()));
        return price;
    };
}
