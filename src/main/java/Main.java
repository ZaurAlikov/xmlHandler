import com.opencsv.CSVWriter;
import model.BerivdoroguProducts;
import model.PriceImpl;
import service.ATPriceReader;
import service.BerivdoroguPriceReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
//        String filePath = "/home/user/Загрузки/Прайс туле 3 с 23.07.2019.xlsx";
        String ATpricePath = "C:\\Users\\Admin\\Downloads\\Прайс туле 3 с 23.07.2019.xlsx";
        String BDPricePath = "C:\\Users\\Admin\\Downloads\\product_export_0-1000_2019-07-27-1435.csv";
        ATPriceReader atPriceReader = new ATPriceReader();
        List<PriceImpl> atPriceList = atPriceReader.readPrice(ATpricePath);
        BerivdoroguPriceReader berivdoroguPriceReader = new BerivdoroguPriceReader();
        List<BerivdoroguProducts> bdPriceList = berivdoroguPriceReader.readPrice(BDPricePath);

        List<BerivdoroguProducts> updatedBdProducts = updateBdProducts(atPriceList, bdPriceList);
        disablingItems(bdPriceList);
        printBdCsv(updatedBdProducts);

        List<PriceImpl> atMissingProducts = getMissingProducts(atPriceList, bdPriceList);

        List<BerivdoroguProducts> bdp = new ArrayList<>();
        for (BerivdoroguProducts updatedBdProduct : updatedBdProducts) {
            if (updatedBdProduct.getOldRetailPrice() != null) {
                bdp.add(updatedBdProduct);
            }
        }

        System.out.println("2341234");
    }

    private static List<PriceImpl> getMissingProducts(List<PriceImpl> supplierPriceList, List<BerivdoroguProducts> bdPriceList) {
        List<PriceImpl> missingProducts = new ArrayList<>();
        supplierPriceList.forEach((atProd) -> {
            boolean flag = false;
            for (BerivdoroguProducts bdProd : bdPriceList) {
                if (bdProd.getModel().substring(0,2).equals("04") && atProd.getSKU().equals(bdProd.getSKU())) {
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

    private static List<BerivdoroguProducts> updateBdProducts(List<PriceImpl> supplierPriceList, List<BerivdoroguProducts> bdPriceList) {
        bdPriceList.forEach((myProd) -> {
            for (PriceImpl spl : supplierPriceList) {
                if (spl.getSKU().equals(myProd.getSKU())) {
                    if (spl.getRetailPrice().compareTo(myProd.getRetailPrice()) != 0 && !Objects.equals(spl.getRetailPrice(), BigDecimal.ZERO)) {
                        myProd.setOldRetailPrice(myProd.getRetailPrice());
                        myProd.setRetailPrice(spl.getRetailPrice());
                        myProd.setStatus(true);
                        myProd.setQuantity(20);
                    }
                    myProd.setPresentInPrice(true);
                    break;
                }
            }
        });
        return bdPriceList;
    }

    private static void disablingItems(List<BerivdoroguProducts> bdPriceList) {
        for (BerivdoroguProducts bdProd : bdPriceList) {
            if (!bdProd.isPresentInPrice()) {
                bdProd.setQuantity(0);
            }
        }
    }

    private static void printBdCsv(List<BerivdoroguProducts> bdProducts) throws IOException {
        String currentDate = String.valueOf(new Date().getTime());
        File file = new File("C:\\Users\\Admin\\Downloads\\product_import_" + currentDate + ".csv");
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile, ';', '"');
//        String[] header = { "_CATEGORY_", "_NAME_", "_MODEL_", "_SKU_", "_MANUFACTURER_", "_PRICE_", "_QUANTITY_", "_META_TITLE_", "_META_DESCRIPTION_", "_DESCRIPTION_", "_IMAGE_", "_SORT_ORDER_",	"_STATUS_",	"_SEO_KEYWORD_", "_ATTRIBUTES_", "_IMAGES_" };
        String[] header = { "_NAME_", "_MODEL_", "_SKU_", "_PRICE_", "_QUANTITY_", "_STATUS_" };
        writer.writeNext(header);
        for (BerivdoroguProducts bdProduct : bdProducts) {
            String[] data = { bdProduct.getProductName(),  bdProduct.getModel(), bdProduct.getSKU(), bdProduct.getRetailPrice().toString(), String.valueOf(bdProduct.getQuantity()), String.valueOf(bdProduct.isStatus() ? 1 : 0) };
            writer.writeNext(data);
        }
        writer.close();
    }
}
