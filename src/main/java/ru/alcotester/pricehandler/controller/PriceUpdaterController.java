package ru.alcotester.pricehandler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ru.alcotester.pricehandler.service.MainReader;
import ru.alcotester.pricehandler.service.PriceReaderHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

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
    public Label dlBdStatusLbl;
    public Button bdAutoBtn;
    public Button aTunAutoBtn;
    public Button edvlAutoBtn;
    public Button esaAutoBtn;

    private Stage primaryStage;
    private String bdPricePath;
    private String atPricePath;
    private String edPricePath;
    private List<String> esaPricePath = new ArrayList<>();

    private MainReader reader = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reader = new MainReader();
    }

    public void fileChooser(ActionEvent event) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        String status = "";
        String dlBdStatus = "";
        statusLbl.setText(status);
        dlBdStatusLbl.setText(dlBdStatus);
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
                    reader.read(bdPricePath, atPricePath, edPricePath, esaPricePath);
                    status = "Read success!";
                } catch (IOException e) {
                    status = "Возникли проблемы при чтении/записи файлов";
                    System.err.println(status);
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    status = e.getMessage();
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
                    dlBdStatus = "Download success!";
                } catch (IOException e) {
                    dlBdStatus = "Download failed!";
                    e.printStackTrace();
                } finally {
                    dlBdStatusLbl.setText(dlBdStatus);
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
        }
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
