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

        bdPriceList.forEach((myProd) -> {
            for (PriceImpl pp : atPriceList) {
                if (pp.getSKU().equals(myProd.getSKU())) {
                    if (pp.getRetailPrice().compareTo(myProd.getRetailPrice()) != 0) {
                        myProd.setOldRetailPrice(myProd.getRetailPrice());
                        myProd.setRetailPrice(pp.getRetailPrice());
                    }
                    myProd.setPresentInPrice(true);
                    break;
                }
            }
        });

        List<PriceImpl> missingProducts = new ArrayList<>();
        atPriceList.forEach((atProd) -> {
            boolean flag = false;
            for (BerivdoroguProducts bdProd : bdPriceList) {
                if (bdProd.getModel().substring(0,2).equals("04") && atProd.getSKU().equals(bdProd.getSKU())) {
                    flag = true;
                }
            }
            if (!flag) {
                missingProducts.add(atProd);
            }
        });

        System.out.println("23423");
    }
}
