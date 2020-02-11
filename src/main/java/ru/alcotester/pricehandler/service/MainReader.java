package ru.alcotester.pricehandler.service;

import com.opencsv.CSVWriter;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ru.alcotester.pricehandler.model.BerivdoroguProducts;
import ru.alcotester.pricehandler.model.PriceImpl;
import ru.alcotester.pricehandler.model.VendorEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static ru.alcotester.pricehandler.service.PriceReaderHelper.roundBigDec;

public class MainReader {

    private Stage primaryStage;

    public MainReader(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void read(String bDPricePath, String aTpricePath, String eDPricePath, List<String> esaPricePath) throws IOException {
//        String aTpricePath = "/home/user/Загрузки/Прайс туле 3 с 23.07.2019.xlsx";
//        String eDPricePath = "/home/user/Загрузки/прайс Евродеталь  с 24 07 2019.xls";
//        String bDPricePath = "/home/user/Загрузки/product_export_0-1000_2019-07-30-0947.csv";
//        String ATpricePath = "C:\\Users\\Admin\\Downloads\\Прайс туле 3 с 23.07.2019.xlsx";
//        String BDPricePath = "C:\\Users\\Admin\\Downloads\\product_export_0-1000_2019-07-27-1435.csv";
        List<PriceImpl> atPriceList;
        List<PriceImpl> edPriceList;
        List<PriceImpl> esaPriceList;
        List<String> priceProcessing = new ArrayList<>();
        Map<VendorEnum, List<PriceImpl>> supplierPriceLists = new HashMap<>();
        List<PriceImpl> atMissingProducts;
        List<PriceImpl> edMissingProducts;
        List<PriceImpl> esaMissingProducts;
        Map<VendorEnum, List<PriceImpl>> missProdMap = new HashMap<>();

        if (StringUtils.isEmpty(bDPricePath)) {
            throw new IllegalArgumentException("Неверный путь к файлу с выгрузкой из berivdorogu.ru");
        }

        BerivdoroguPriceReader berivdoroguPriceReader = new BerivdoroguPriceReader();
        List<BerivdoroguProducts> bdPriceList = berivdoroguPriceReader.readPrice(bDPricePath);

        if (StringUtils.isNotEmpty(aTpricePath)) {
            ATPriceReader atPriceReader = new ATPriceReader();
            atPriceList = atPriceReader.readPrice(aTpricePath);
            priceProcessing.add(VendorEnum.ATUNING.getCode());
            supplierPriceLists.put(VendorEnum.ATUNING, atPriceList);
            atMissingProducts = getMissingProducts(VendorEnum.ATUNING, atPriceList, bdPriceList);
            missProdMap.put(VendorEnum.ATUNING, atMissingProducts);
        }

        if (StringUtils.isNotEmpty(eDPricePath)) {
            EDPriceReader edPriceReader = new EDPriceReader();
            edPriceList = edPriceReader.readPrice(eDPricePath);
            priceProcessing.add(VendorEnum.EURODETAL.getCode());
            supplierPriceLists.put(VendorEnum.EURODETAL, edPriceList);
            edMissingProducts = getMissingProducts(VendorEnum.EURODETAL, edPriceList, bdPriceList);
            missProdMap.put(VendorEnum.EURODETAL, edMissingProducts);
        }

        if (CollectionUtils.isNotEmpty(esaPricePath)) {
            ESAPriceReader esaPriceReader = new ESAPriceReader();
            esaPriceList = esaPriceReader.readPrice(esaPricePath);
            priceProcessing.add(VendorEnum.ESAUTO.getCode());
            supplierPriceLists.put(VendorEnum.ESAUTO, esaPriceList);
            esaMissingProducts = getMissingProducts(VendorEnum.ESAUTO, esaPriceList, bdPriceList);
            missProdMap.put(VendorEnum.ESAUTO, esaMissingProducts);
        }

        List<BerivdoroguProducts> updatedBdProducts = updateBdProducts(supplierPriceLists, bdPriceList);
        disablingItems(bdPriceList, priceProcessing);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(primaryStage);
        printBdCsv(updatedBdProducts, priceProcessing, file.getAbsolutePath());
        printMissingProduct(missProdMap, file.getAbsolutePath());
        System.out.println("Read success!");

//        List<BerivdoroguProducts> bdp = new ArrayList<>();
//        for (BerivdoroguProducts updatedBdProduct : updatedBdProducts) {
//            if (updatedBdProduct.getOldRetailPrice() != null) {
//                bdp.add(updatedBdProduct);
//            }
//        }
    }

    private List<PriceImpl> getMissingProducts(VendorEnum vendorCode, List<PriceImpl> supplierPriceList, List<BerivdoroguProducts> bdPriceList) {
        List<PriceImpl> missingProducts = new ArrayList<>();
        supplierPriceList.forEach((atProd) -> {
            boolean flag = false;
            for (BerivdoroguProducts bdProd : bdPriceList) {
                if (bdProd.getModel().substring(0, 2).equals(vendorCode.getCode()) && atProd.getSKU().equals(bdProd.getSKU())) {
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

    private List<BerivdoroguProducts> updateBdProducts(Map<VendorEnum, List<PriceImpl>> supplierPriceList, List<BerivdoroguProducts> bdPriceList) {
        bdPriceList.forEach((myProd) -> {
            for (Map.Entry<VendorEnum, List<PriceImpl>> vendorEnumListEntry : supplierPriceList.entrySet()) {
                for (PriceImpl price : vendorEnumListEntry.getValue()) {
                    if (price.getSKU().equals(myProd.getSKU())) {
                        if (price.getRetailPrice().compareTo(myProd.getRetailPrice()) != 0 && !Objects.equals(price.getRetailPrice(), BigDecimal.ZERO)) {
                            myProd.setOldRetailPrice(myProd.getRetailPrice());
                            myProd.setRetailPrice(price.getRetailPrice());
                            if (!myProd.isStatus()) {
                                myProd.setOldStatus(false);
                            }
                            myProd.setStatus(true);
                            myProd.setQuantity(20);
                        }
                        if (price.getRetailPrice().compareTo(myProd.getRetailPrice()) == 0 && !myProd.isStatus()) {
                            myProd.setOldStatus(false);
                            myProd.setStatus(true);
                            myProd.setQuantity(20);
                        }
                        if (price.getRetailPrice().compareTo(myProd.getRetailPrice()) == 0 && myProd.isStatus() && myProd.getQuantity() == 0) {
                            myProd.setOldQuantity(0);
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

    private void disablingItems(List<BerivdoroguProducts> bdPriceList, List<String> priceProcessing) {
        for (BerivdoroguProducts bdProd : bdPriceList) {
            if (priceProcessing.contains(bdProd.getModel().substring(0, 2)) && !bdProd.isPresentInPrice()) {
                bdProd.setQuantity(0);
            }
        }
    }

    private void printBdCsv(List<BerivdoroguProducts> bdProducts, List<String> priceProcessing,  String saveDir) throws IOException {
        String currentDate = String.valueOf(new Date().getTime());
//        File file = new File("C:\\Users\\Admin\\Downloads\\product_import_" + currentDate + ".csv");
        File file = new File(saveDir + File.separator + "product_import_" + currentDate + ".csv");
//        File file = new File("/berivdorogu/product_import_" + currentDate + ".csv");
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile, ';', '"');
//        String[] header = { "_CATEGORY_", "_NAME_", "_MODEL_", "_SKU_", "_MANUFACTURER_", "_PRICE_", "_QUANTITY_", "_META_TITLE_", "_META_DESCRIPTION_", "_DESCRIPTION_", "_IMAGE_", "_SORT_ORDER_",	"_STATUS_",	"_SEO_KEYWORD_", "_ATTRIBUTES_", "_IMAGES_" };
        String[] header = {"_NAME_", "_MODEL_", "_SKU_", "_PRICE_", "_QUANTITY_", "_STATUS_", "old_price", "old_quantity", "change_status"};
        writer.writeNext(header);
        for (BerivdoroguProducts bdProduct : bdProducts) {
            if (!priceProcessing.contains(bdProduct.getModel().substring(0, 2))) {
                continue;
            }
            String[] data = {bdProduct.getProductName(), bdProduct.getModel(), bdProduct.getSKU(),
                    roundBigDec(bdProduct.getRetailPrice()), String.valueOf(bdProduct.getQuantity()),
                    String.valueOf(bdProduct.isStatus() ? 1 : 0),
                    String.valueOf(bdProduct.getOldRetailPrice() != null ? bdProduct.getOldRetailPrice() : ""),
                    String.valueOf(bdProduct.getOldQuantity() != null ? bdProduct.getOldQuantity() : ""),
                    String.valueOf(bdProduct.getOldStatus() != null ? (bdProduct.getOldStatus() ? 1 : 0) : "")};
            writer.writeNext(data);
        }
        writer.close();
    }

    private void printMissingProduct(Map<VendorEnum, List<PriceImpl>> missingProd,  String saveDir) throws IOException {
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
                cell2.setCellValue(price.getTradePrice() != null ? price.getTradePrice().toString() : "");
                XSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(price.getRetailPrice() != null ? price.getRetailPrice().toString() : "");
                ++rowNum;
            }
        }
        File file = new File(saveDir + File.separator + "miss_products_" + new Date().getTime() + ".xlsx");
        book.write(new FileOutputStream(file));
        book.close();
    }
}
