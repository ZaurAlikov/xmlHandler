package ru.alcotester.pricehandler.controller;

import com.google.api.services.gmail.model.MessagePart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ru.alcotester.pricehandler.model.EmailInfo;
import ru.alcotester.pricehandler.model.EmailTableModel;
import ru.alcotester.pricehandler.service.GMailService;
import ru.alcotester.pricehandler.service.PriceReaderHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class EmailWindowController implements Initializable {


    private String mail_query = "";
    private String datePartQuery = "";
    private String labelPartQuery = "";
    private String fromPartQuery = "";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String A_TUNING = "eurotuning-spb";
    private static final String ES_AUTO = "autobud";
    private static final String EVRODETAL = "evrodetal";

    public Button applyBtn;
    public Button loadPricesBtn;
    public Label loadPriceLbl;
    public DatePicker filterDatePicker;
    public TextField filterFromTxtFld;
    public ComboBox<String> filterLabelCmbBox;
    @FXML
    private TableView<EmailTableModel> emailTableView;
    @FXML
    private TableColumn<EmailTableModel, Date> colDate;
    @FXML
    private TableColumn<EmailTableModel, String> colSupplier;
    @FXML
    private TableColumn<EmailTableModel, String> colPriceName;

    private ObservableList<EmailTableModel> emailList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDate.setCellFactory(column -> new TableCell<EmailTableModel, Date>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setText(null);
                }
                else {
                    this.setText(format.format(item));
                }
            }
        });
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colPriceName.setCellValueFactory(new PropertyValueFactory<>("priceName"));
        colDate.setSortType(TableColumn.SortType.ASCENDING);
        emailTableView.setItems(emailList);
        filterLabelCmbBox.setItems(FXCollections.observableArrayList(GMailService.getLabelNames()));
        filterLabelCmbBox.getSelectionModel().select("Поставщики");
        labelPartQuery = filterLabelCmbBox.getValue();
        filterLabelCmbBox.valueProperty().addListener((ov, oldValue, newValue) -> labelPartQuery = newValue);
        Calendar defaultDate = Calendar.getInstance();
        defaultDate.add(Calendar.DAY_OF_MONTH, -7);
        filterDatePicker.setValue(LocalDate.of(defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH)+1, defaultDate.get(Calendar.DAY_OF_MONTH)));
        datePartQuery = filterDatePicker.getValue().format(FORMATTER);
        filterDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> datePartQuery = newValue.format(FORMATTER));
        filterFromTxtFld.focusedProperty().addListener((observable, oldValue, newValue) -> { if (!newValue) fromPartQuery = filterFromTxtFld.getText(); });
        constructQuery();
        fillEmailList();
    }

    private void fillEmailList() {
        emailList.clear();
        List<EmailInfo> emailInfo = GMailService.getEmail(mail_query, 100L);
        for (EmailInfo info : emailInfo) {
            if (CollectionUtils.isNotEmpty(info.getMessageParts())) {
                for (MessagePart messagePart : info.getMessageParts()) {
                    if (StringUtils.isNotEmpty(messagePart.getFilename())) {
                        EmailTableModel emailTableModel = new EmailTableModel();
                        emailTableModel.setMessageId(info.getId());
                        emailTableModel.setAttachmentId(messagePart.getBody().getAttachmentId());
                        emailTableModel.setDate(info.getDate());
                        emailTableModel.setSupplier(info.getFromEmail());
                        emailTableModel.setPriceName(messagePart.getFilename());
                        emailList.add(emailTableModel);
                    }
                }
            }
        }
    }

    private void constructQuery() {
        StringBuilder result = new StringBuilder("has:attachment {filename:xls filename:xlsx}")
                .append(" label:")
                .append(labelPartQuery)
                .append(" after:")
                .append(datePartQuery);
        if(StringUtils.isNotBlank(fromPartQuery)) {
            result.append(" from:").append(fromPartQuery);
        }
        mail_query = result.toString();
    }

    public void emailWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("EmailModalWindow.fxml"));
        Parent emailModal = loader.load();
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene secondScene = new Scene(emailModal);
        dialog.setScene(secondScene);
        dialog.setTitle("Выбор и загрузка прайсов из писем");
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    public void loadPrices(ActionEvent actionEvent) {
        String status = "";
        loadPriceLbl.setText(status);
        if (actionEvent.getSource() instanceof Button) {
            Button btn = (Button) actionEvent.getSource();
            if (btn.getId().equals("loadPricesBtn")) {
                List<EmailTableModel> tableModel = emailTableView.getSelectionModel().getSelectedItems();
                if (CollectionUtils.isNotEmpty(tableModel)) {
                    String path = PriceReaderHelper.createPriceFolders();
                    for (EmailTableModel emailTableModel : tableModel) {
                        if (emailTableModel.getSupplier().contains(A_TUNING)) {
                            String aTuningPath = PriceReaderHelper.checkAndCreateDir(path + File.separator + "ATuning");
                            GMailService.downloadAttachments(GMailService.ME, emailTableModel.getMessageId(), emailTableModel.getAttachmentId(), aTuningPath + File.separator + emailTableModel.getPriceName());
                        }
                        if (emailTableModel.getSupplier().contains(ES_AUTO)) {
                            String eSAutoPath = PriceReaderHelper.checkAndCreateDir(path + File.separator + "ESAuto");
                            GMailService.downloadAttachments(GMailService.ME, emailTableModel.getMessageId(), emailTableModel.getAttachmentId(), eSAutoPath + File.separator + emailTableModel.getPriceName());
                        }
                        if (emailTableModel.getSupplier().contains(EVRODETAL)) {
                            String evrodetalPath = PriceReaderHelper.checkAndCreateDir(path + File.separator + "Evrodetal");
                            GMailService.downloadAttachments(GMailService.ME, emailTableModel.getMessageId(), emailTableModel.getAttachmentId(), evrodetalPath + File.separator + emailTableModel.getPriceName());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(emailTableView.getSelectionModel().getSelectedItems())) {
                    status = "Loading is complete";
                    loadPriceLbl.setText(status);
                    emailTableView.getSelectionModel().clearSelection();
                } else {
                    status = "Nothing selected";
                    loadPriceLbl.setText(status);
                }
            }
        }
    }

    public void onFilter(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            Button btn = (Button) actionEvent.getSource();
            if (btn.getId().equals("applyBtn")) {
                constructQuery();
                fillEmailList();
            }
        }
    }
}
