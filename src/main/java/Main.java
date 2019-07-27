import model.BerivdoroguProducts;
import model.PriceImpl;
import service.ATPriceReader;
import service.BerivdoroguPriceReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        List<PriceImpl> atMissingProducts = getMissingProducts(atPriceList, bdPriceList);
        disablingItems(bdPriceList);

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
                    if (spl.getRetailPrice().compareTo(myProd.getRetailPrice()) != 0) {
                        myProd.setOldRetailPrice(myProd.getRetailPrice());
                        myProd.setRetailPrice(spl.getRetailPrice());
                        myProd.setStatus(true);
                        myProd.setQuantity("20");
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
                bdProd.setQuantity("0");
            }
        }
    }
}
