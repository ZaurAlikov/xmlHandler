package ru.alcotester.pricehandler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ru.alcotester.pricehandler.model.ColumnMapping;
import ru.alcotester.pricehandler.model.VendorEnum;
import ru.alcotester.pricehandler.service.MainReader;
import ru.alcotester.pricehandler.service.PriceReaderHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static ru.alcotester.pricehandler.service.PriceReaderHelper.getColumnMapping;

public class PriceUpdaterController implements Initializable {

    private static String A_TUNING = "ATuning";
    private static String ES_AUTO = "ESAuto";
    private static String EVRODETAL = "Evrodetal";

    public Button berivdoroguBtn;
    public TextField berivdoroguTxt;
    public Label statusLbl;
    public Button atuningBtn;
    public TextField atuningTxt;
    public Button eurodetalBtn;
    public TextField eurodetalTxt;
    public Button esautoBtn;
    public TextArea esAutoTxtArea;
    public Button goBtn;
    public Button emailBtn;
    public Button downloadBDPriceBtn;
    public Button bdAutoBtn;
    public Button aTunAutoBtn;
    public Button edvlAutoBtn;
    public Button esaAutoBtn;
    public Button bdClearBtn;
    public Button atunClearBtn;
    public Button edlClearBtn;
    public Button esaClearBtn;

    private Stage primaryStage;
    private String bdPricePath;
    private String atPricePath;
    private String edPricePath;
    private List<String> esaPricePath = new ArrayList<>();
    private List<ColumnMapping> columnMappingList = new ArrayList<>();

    private MainReader reader = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reader = new MainReader();
        initColumnMapping();
    }

    private void initColumnMapping() {
        ColumnMapping columnMapping = new ColumnMapping();
        columnMapping.setVendor(VendorEnum.ATUNING);
        columnMapping.setProductName(5);
        columnMapping.setSku(0);
        columnMapping.setRetailPrice(null);
        columnMapping.setTradePrice(null);
        columnMapping.setUnit(13);
        columnMapping.setAvailability(14);
        columnMappingList.add(columnMapping);
    }

    public void fileChooser(ActionEvent event) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(PriceReaderHelper.createMainPath()));
        String status = "";
        statusLbl.setText(status);
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            if (btn.getId().equals("berivdoroguBtn")) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    bdPricePath = file.getPath();
                    berivdoroguTxt.setText(bdPricePath);
                }
            }
            if (btn.getId().equals("atuningBtn")) {
                String lastFolderPath = getLastFolderPath();
                File path = new File(lastFolderPath + File.separator + A_TUNING);
                if (StringUtils.isNotEmpty(lastFolderPath) && path.exists()) {
                    fileChooser.setInitialDirectory(path);
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        atPricePath = file.getPath();
                        atuningTxt.setText(atPricePath);
                    }
                } else {
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        atPricePath = file.getPath();
                        atuningTxt.setText(atPricePath);
                    }
                }
            }
            if (btn.getId().equals("eurodetalBtn")) {
                String lastFolderPath = getLastFolderPath();
                File path = new File(lastFolderPath + File.separator + EVRODETAL);
                if (StringUtils.isNotEmpty(lastFolderPath) && path.exists()) {
                    fileChooser.setInitialDirectory(path);
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        edPricePath = file.getPath();
                        eurodetalTxt.setText(edPricePath);
                    }
                } else {
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        edPricePath = file.getPath();
                        eurodetalTxt.setText(edPricePath);
                    }
                }
            }
            if (btn.getId().equals("esautoBtn")) {
                String lastFolderPath = getLastFolderPath();
                File path = new File(lastFolderPath + File.separator + ES_AUTO);
                if (StringUtils.isNotEmpty(lastFolderPath) && path.exists()) {
                    fileChooser.setInitialDirectory(path);
                    List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
                    if (CollectionUtils.isNotEmpty(files)) {
                        for (File file : files) {
                            esaPricePath.add(file.getPath());
                            esAutoTxtArea.appendText(file.getPath() + "\n");
                        }
                    }
                } else {
                    List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
                    if (CollectionUtils.isNotEmpty(files)) {
                        for (File file : files) {
                            esaPricePath.add(file.getPath());
                            esAutoTxtArea.appendText(file.getPath() + "\n");
                        }
                    }
                }
            }
            if (btn.getId().equals("goBtn")) {
                try {
                    reader.read(bdPricePath, atPricePath, edPricePath, esaPricePath, columnMappingList);
                    status = "Read success!";
                } catch (IOException e) {
                    status = "Возникли проблемы при чтении/записи файлов";
                    System.err.println(status);
                    e.printStackTrace();
                } catch (Exception e) {
                    status = e.toString();
                    e.printStackTrace();
                } finally {
                    statusLbl.setText(status);
                }
            }
            if (btn.getId().equals("emailBtn")) {
                EmailWindowController emailWindowController = new EmailWindowController();
                emailWindowController.emailWindow();
            }
            if (btn.getId().equals("downloadBDPriceBtn")) {
                try {
                    reader.downloadBDPrice();
                    status = "Download success!";
                } catch (Exception e) {
                    status = "Download failed! " + e.toString();
                    e.printStackTrace();
                } finally {
                    statusLbl.setText(status);
                }
            }
            if (btn.getId().equals("bdAutoBtn")) {
                if (StringUtils.isNotEmpty(PriceReaderHelper.bdPricePathExist())) {
                    bdPricePath = PriceReaderHelper.bdPricePathExist();
                    berivdoroguTxt.setText(bdPricePath);
                } else {
                    berivdoroguBtn.fire();
                }
            }
            if (btn.getId().equals("aTunAutoBtn")) {
                String lastFolderPath = getLastFolderPath();
                List<String> fileList = getFileList(lastFolderPath + File.separator + A_TUNING);
                if (StringUtils.isNotEmpty(lastFolderPath) && fileList.size() == 1) {
                    atPricePath = fileList.get(0);
                    atuningTxt.setText(atPricePath);
                } else {
                    atuningBtn.fire();
                }
            }
            if (btn.getId().equals("edvlAutoBtn")) {
                String lastFolderPath = getLastFolderPath();
                List<String> fileList = getFileList(lastFolderPath + File.separator + EVRODETAL);
                if (StringUtils.isNotEmpty(lastFolderPath) && fileList.size() == 1) {
                    edPricePath = fileList.get(0);
                    eurodetalTxt.setText(edPricePath);
                } else {
                    eurodetalBtn.fire();
                }
            }
            if (btn.getId().equals("esaAutoBtn")) {
                String lastFolderPath = getLastFolderPath();
                List<String> fileList = getFileList(lastFolderPath + File.separator + ES_AUTO);
                if (StringUtils.isNotEmpty(lastFolderPath) && CollectionUtils.isNotEmpty(fileList)) {
                    for (String file : fileList) {
                        esaPricePath.add(file);
                        esAutoTxtArea.appendText(file + "\n");
                    }
                } else {
                    esautoBtn.fire();
                }
            }
            if (btn.getId().equals("bdClearBtn")) {
                bdPricePath = "";
                berivdoroguTxt.setText(bdPricePath);
            }
            if (btn.getId().equals("atunClearBtn")) {
                atPricePath = "";
                atuningTxt.setText(atPricePath);
            }
            if (btn.getId().equals("edlClearBtn")) {
                edPricePath = "";
                eurodetalTxt.setText(edPricePath);
            }
            if (btn.getId().equals("esaClearBtn")) {
                esaPricePath.clear();
                esAutoTxtArea.clear();
            }
            if (btn.getId().equals("openBtn")) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    Desktop.getDesktop().open(file);
                }
            }
            if (btn.getId().equals("aTuningColMapBtn")) {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ColumnMapping.fxml"));
                Parent columnMappingModal = loader.load();
                ColumnMappingController columnMappingController =  loader.getController();
                ColumnMapping columnMapping = getColumnMapping(VendorEnum.ATUNING, columnMappingList);
                setColumnMapWindow(columnMappingController, columnMapping);
                columnMappingController.columnMappingWindow(columnMappingModal);
                fillColumnMapping(columnMappingController, columnMapping);
            }
        }
    }

    private void fillColumnMapping(ColumnMappingController columnMappingController, ColumnMapping columnMapping) {
        columnMapping.setSku(getFieldValue(columnMappingController.skuFld));
        columnMapping.setProductName(getFieldValue(columnMappingController.nameFld));
        columnMapping.setRetailPrice(getFieldValue(columnMappingController.retailPrcFld));
        columnMapping.setTradePrice(getFieldValue(columnMappingController.tradePrcFld));
        columnMapping.setUnit(getFieldValue(columnMappingController.unitFld));
        columnMapping.setAvailability(getFieldValue(columnMappingController.availabilityFld));
    }

    private Integer getFieldValue(TextField field) {
        if (StringUtils.isNotBlank(field.getText())) {
            return Integer.valueOf(field.getText());
        } else {
            return null;
        }
    }

    private void setColumnMapWindow(ColumnMappingController columnMappingController, ColumnMapping columnMapping) {
        columnMappingController.skuFld.setText(columnMapping.getSku() != null ? columnMapping.getSku().toString() : null);
        columnMappingController.nameFld.setText(columnMapping.getProductName() != null ? columnMapping.getProductName().toString() : null);
        columnMappingController.retailPrcFld.setText(columnMapping.getRetailPrice() != null ? columnMapping.getRetailPrice().toString() : null);
        columnMappingController.tradePrcFld.setText(columnMapping.getTradePrice() != null ? columnMapping.getTradePrice().toString() : null);
        columnMappingController.unitFld.setText(columnMapping.getUnit() != null ? columnMapping.getUnit().toString() : null);
        columnMappingController.availabilityFld.setText(columnMapping.getAvailability() != null ? columnMapping.getAvailability().toString() : null);
    }

    private String getLastFolderPath() throws IOException {
        String mainPath = PriceReaderHelper.createMainPath();
        List<String> pathList = new ArrayList<>();
        try (Stream<Path> paths = Files.list(Paths.get(mainPath))) {
            paths.filter(Files::isDirectory).forEach(path -> pathList.add(path.toString()));
        }
        if (CollectionUtils.isNotEmpty(pathList)) {
            List<String> dirNameList = new ArrayList<>();
            pathList.forEach(path -> dirNameList.add(path.substring(path.lastIndexOf(File.separator)+1)));
            return mainPath + File.separator + PriceReaderHelper.getLastDateFolderName(dirNameList);
        }
        return "";
    }

    private List<String> getFileList(String path) throws IOException {
        List<String> fileList = new ArrayList<>();
        File file1 = new File(path);
        if (file1.exists()){
            try (Stream<Path> files = Files.list(Paths.get(path))) {
                files.filter(Files::isRegularFile).forEach(file -> fileList.add(file.toString()));
            }
        }
        return fileList;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
