import com.opencsv.CSVWriter;
import model.BerivdoroguProducts;
import model.PriceImpl;
import model.VendorEnum;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import service.ATPriceReader;
import service.BerivdoroguPriceReader;
import service.EDPriceReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static service.PriceReaderHelper.roundBigDec;

public class Main {

    public static void main(String[] args) throws IOException {
        String aTpricePath = "/home/user/Загрузки/Прайс туле 3 с 23.07.2019.xlsx";
        String eDPricePath = "/home/user/Загрузки/прайс Евродеталь  с 24 07 2019.xls";
        String bDPricePath = "/home/user/Загрузки/product_export_0-1000_2019-07-30-0947.csv";
//        String ATpricePath = "C:\\Users\\Admin\\Downloads\\Прайс туле 3 с 23.07.2019.xlsx";
//        String BDPricePath = "C:\\Users\\Admin\\Downloads\\product_export_0-1000_2019-07-27-1435.csv";

        List<String> priceProcessing = new ArrayList<>();
        priceProcessing.add(VendorEnum.ATUNING.getCode());
        priceProcessing.add(VendorEnum.EURODETAL.getCode());

        ATPriceReader atPriceReader = new ATPriceReader();
        List<PriceImpl> atPriceList = atPriceReader.readPrice(aTpricePath);

        EDPriceReader edPriceReader = new EDPriceReader();
        List<PriceImpl> edPriceList = edPriceReader.readPrice(eDPricePath);

        BerivdoroguPriceReader berivdoroguPriceReader = new BerivdoroguPriceReader();
        List<BerivdoroguProducts> bdPriceList = berivdoroguPriceReader.readPrice(bDPricePath);

        Map<VendorEnum, List<PriceImpl>> suplearPriceLists = new HashMap<>();
        suplearPriceLists.put(VendorEnum.ATUNING, atPriceList);
        suplearPriceLists.put(VendorEnum.EURODETAL, edPriceList);

        List<BerivdoroguProducts> updatedBdProducts = updateBdProducts(suplearPriceLists, bdPriceList);
        disablingItems(bdPriceList, priceProcessing);
        printBdCsv(updatedBdProducts);

        List<PriceImpl> atMissingProducts = getMissingProducts(VendorEnum.ATUNING, atPriceList, bdPriceList);
        List<PriceImpl> edMissingProducts = getMissingProducts(VendorEnum.EURODETAL, edPriceList, bdPriceList);
        Map<VendorEnum, List<PriceImpl>> missProdMap = new HashMap<>();
        missProdMap.put(VendorEnum.ATUNING, atMissingProducts);
        missProdMap.put(VendorEnum.EURODETAL, edMissingProducts);
        printMissingProduct(missProdMap);

        List<BerivdoroguProducts> bdp = new ArrayList<>();
        for (BerivdoroguProducts updatedBdProduct : updatedBdProducts) {
            if (updatedBdProduct.getOldRetailPrice() != null) {
                bdp.add(updatedBdProduct);
            }
        }

        System.out.println("2341234");
    }

    private static List<PriceImpl> getMissingProducts(VendorEnum vendorCode, List<PriceImpl> supplierPriceList, List<BerivdoroguProducts> bdPriceList) {
        List<PriceImpl> missingProducts = new ArrayList<>();
        supplierPriceList.forEach((atProd) -> {
            boolean flag = false;
            for (BerivdoroguProducts bdProd : bdPriceList) {
                if (bdProd.getModel().substring(0,2).equals(vendorCode.getCode()) && atProd.getSKU().equals(bdProd.getSKU())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                missingProducts.add(atProd);
            }
        });
        return missingProducts;
    }

    private static List<BerivdoroguProducts> updateBdProducts(Map<VendorEnum, List<PriceImpl>> supplierPriceList, List<BerivdoroguProducts> bdPriceList) {
        bdPriceList.forEach((myProd) -> {
            for (Map.Entry<VendorEnum, List<PriceImpl>> vendorEnumListEntry : supplierPriceList.entrySet()) {
                for (PriceImpl price : vendorEnumListEntry.getValue()) {
                    if (price.getSKU().equals(myProd.getSKU())) {
                        if (price.getRetailPrice().compareTo(myProd.getRetailPrice()) != 0 && !Objects.equals(price.getRetailPrice(), BigDecimal.ZERO)) {
                            myProd.setOldRetailPrice(myProd.getRetailPrice());
                            myProd.setRetailPrice(price.getRetailPrice());
                            myProd.setStatus(true);
                            myProd.setQuantity(20);
                        }
                        myProd.setPresentInPrice(true);
                        break;
                    }
                }
            }
        });
        return bdPriceList;
    }

    private static void disablingItems(List<BerivdoroguProducts> bdPriceList, List<String> priceProcessing) {
        for (BerivdoroguProducts bdProd : bdPriceList) {
            if (priceProcessing.contains(bdProd.getModel().substring(0,2)) && !bdProd.isPresentInPrice()) {
                bdProd.setQuantity(0);
            }
        }
    }

    private static void printBdCsv(List<BerivdoroguProducts> bdProducts) throws IOException {
        String currentDate = String.valueOf(new Date().getTime());
//        File file = new File("C:\\Users\\Admin\\Downloads\\product_import_" + currentDate + ".csv");
        File file = new File("/home/user/Загрузки/berivdorogu/product_import_" + currentDate + ".csv");
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile, ';', '"');
//        String[] header = { "_CATEGORY_", "_NAME_", "_MODEL_", "_SKU_", "_MANUFACTURER_", "_PRICE_", "_QUANTITY_", "_META_TITLE_", "_META_DESCRIPTION_", "_DESCRIPTION_", "_IMAGE_", "_SORT_ORDER_",	"_STATUS_",	"_SEO_KEYWORD_", "_ATTRIBUTES_", "_IMAGES_" };
        String[] header = { "_NAME_", "_MODEL_", "_SKU_", "_PRICE_", "_QUANTITY_", "_STATUS_" };
        writer.writeNext(header);
        for (BerivdoroguProducts bdProduct : bdProducts) {
            String[] data = { bdProduct.getProductName(),  bdProduct.getModel(), bdProduct.getSKU(), roundBigDec(bdProduct.getRetailPrice()), String.valueOf(bdProduct.getQuantity()), String.valueOf(bdProduct.isStatus() ? 1 : 0) };
            writer.writeNext(data);
        }
        writer.close();
    }

    private static void printMissingProduct(Map<VendorEnum, List<PriceImpl>> missingProd) throws IOException {
        XSSFWorkbook book = new XSSFWorkbook();
        for (Map.Entry<VendorEnum, List<PriceImpl>> vendorEnumListEntry : missingProd.entrySet()) {
            XSSFSheet sheet = book.createSheet(vendorEnumListEntry.getKey().getName());
            int rowNum = 0;
            for (PriceImpl price : vendorEnumListEntry.getValue()) {
                XSSFRow row = sheet.createRow(rowNum);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue(price.getProductName());
                XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(price.getSKU());
                XSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(price.getTradePrice().toString());
                XSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(price.getRetailPrice().toString());
                ++rowNum;
            }
        }
        book.write(new FileOutputStream("/home/user/Загрузки/berivdorogu/missProducts" + new Date().getTime() + ".xlsx"));
        book.close();
    }
}
