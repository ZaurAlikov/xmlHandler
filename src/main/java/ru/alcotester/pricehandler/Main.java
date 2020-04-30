package ru.alcotester.pricehandler;

import ru.alcotester.pricehandler.controller.PriceUpdaterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PriceUpdater.fxml"));
        Parent root = loader.load();
        PriceUpdaterController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Price updater");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
//        XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(new File("c:\\out\\MasterData1.xlsx")));
//        XSSFSheet myExcelSheet = myExcelBook.getSheet("Document_TB47");
//        List<XSSFRow> rows = new ArrayList<>();
//        for (int i = 0; i <= myExcelSheet.getLastRowNum(); i++) {
//            rows.add(myExcelSheet.getRow(i));
//        }
//        List<Thule> thuleList = new ArrayList<>();
//        List<Thule> berivdoroguList = new ArrayList<>();
//        if (rows.size() > 0) {
//            for (int i = 1; i < 7319; i++) {
//                Thule thule = new Thule();
//                thule.setSku(checkCellGetString(rows.get(i).getCell(0)));
//                thule.setName(checkCellGetString(rows.get(i).getCell(1)));
//                thule.setNetto(checkCellGetString(rows.get(i).getCell(5)));
//                thule.setBrutto(checkCellGetString(rows.get(i).getCell(7)));
//                thule.setLength(checkCellGetString(rows.get(i).getCell(8)));
//                thule.setWidth(checkCellGetString(rows.get(i).getCell(9)));
//                thule.setHeight(checkCellGetString(rows.get(i).getCell(10)));
//                thuleList.add(thule);
//            }
//            for (int i = 1; i < 605; i++) {
//                Thule berivdorogu = new Thule();
//                berivdorogu.setSku(checkCellGetString(rows.get(i).getCell(20)));
//                berivdorogu.setName(checkCellGetString(rows.get(i).getCell(13)));
//                berivdoroguList.add(berivdorogu);
//            }
//        }
//        for (Thule thule : berivdoroguList) {
//            Thule thule1 = IterableUtils.find(thuleList, findThule -> thule.getSku().equals(findThule.getSku()));
//            if (thule1 != null) {
//                thule.setNetto(thule1.getNetto());
//                thule.setBrutto(thule1.getBrutto());
//                thule.setLength(thule1.getLength());
//                thule.setWidth(thule1.getWidth());
//                thule.setHeight(thule1.getHeight());
//            }
//
//        }
//        for (int i = 1; i < 605; i++) {
//            for (Thule thule : berivdoroguList) {
//                if(checkCellGetString(rows.get(i).getCell(20)).equals(thule.getSku())) {
//                    XSSFRow row = rows.get(i);
//                    XSSFCell cell1 = row.createCell(21);
//                    cell1.setCellValue(inCm(thule.getLength()));
//                    XSSFCell cell2 = row.createCell(22);
//                    cell2.setCellValue(inCm(thule.getWidth()));
//                    XSSFCell cell3 = row.createCell(23);
//                    cell3.setCellValue(inCm(thule.getHeight()));
//                    XSSFCell cell4 = row.createCell(24);
//                    cell4.setCellValue(thule.getNetto());
//                    XSSFCell cell5 = row.createCell(25);
//                    cell5.setCellValue(thule.getBrutto());
//                    break;
//                }
//            }
//            System.out.println(i);
//        }
//        File file = new File("c:\\out\\MasterData1.xlsx");
//        myExcelBook.write(new FileOutputStream(file));
//        myExcelBook.close();



        launch(args);
    }

    private static String inCm(String inM) {
        BigDecimal multiply = new BigDecimal("999999");
        if (inM != null) {
            BigDecimal bdInM = new BigDecimal(inM.replace(",", "."));
            multiply = bdInM.multiply(new BigDecimal("100"));
        }
        return multiply.toString().equals("999999") ? "" : multiply.toString();
    }
}

class Thule {
    String sku;
    String name;
    String netto;
    String brutto;
    String length;
    String width;
    String height;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetto() {
        return netto;
    }

    public void setNetto(String netto) {
        this.netto = netto;
    }

    public String getBrutto() {
        return brutto;
    }

    public void setBrutto(String brutto) {
        this.brutto = brutto;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
