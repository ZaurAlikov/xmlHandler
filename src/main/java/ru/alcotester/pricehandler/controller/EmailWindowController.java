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

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class EmailWindowController implements Initializable {

//    private static final String MAIL_QUERY = "has:attachment label:Поставщики filename:(xls xlsx)";
    private static final String MAIL_QUERY = "has:attachment label:Поставщики {filename:xls filename:xlsx} after:2020/01/01 from:Мадатова Светлана";

    public Button loadPricesBtn;
    public Label loadPriceLbl;
    @FXML
    private TableView<EmailTableModel> emailTableView;
    @FXML
    private TableColumn<EmailTableModel, Date> colDate;
    @FXML
    private TableColumn<EmailTableModel, String> colSupplier;
    @FXML
    private TableColumn<EmailTableModel, String> colPriceName;
    private Stage primaryStage;

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
        fillEmailList();
        emailTableView.setItems(emailList);
    }

    private void fillEmailList() {
        List<EmailInfo> emailInfo = new ArrayList<>();
        try {
            emailInfo = GMailService.getEmail(MAIL_QUERY, 100L);
        } catch (GeneralSecurityException | IOException | MessagingException | ParseException e) {
            e.printStackTrace();
        }
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
                String path = PriceReaderHelper.createFolders(true);
                for (EmailTableModel emailTableModel : tableModel) {
                    GMailService.downloadAttachments(GMailService.ME, emailTableModel.getMessageId(), emailTableModel.getAttachmentId(), path + File.separator + emailTableModel.getPriceName());
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
