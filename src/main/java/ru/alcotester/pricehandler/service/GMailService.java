package ru.alcotester.pricehandler.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ru.alcotester.pricehandler.model.EmailInfo;
import ru.alcotester.pricehandler.model.GMailLabels;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class GMailService {

    public static final String ME = "me";

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String MIME_TYPE = "multipart";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Gmail service = null;

    static {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GMailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static List<EmailInfo> getEmail(String q, long maxResults) {
        List<EmailInfo> emailInfos = new ArrayList<>();
        try {
            ListMessagesResponse openMessages = service
                    .users()
                    .messages()
                    .list(ME)
                    .setQ(q)                          // Фильтр - "is:unread label:inbox"   "label:inbox label:closed"  "label:inbox label:pending"
                    .setMaxResults(maxResults)
                    .execute();
            List<Message> messages = openMessages.getMessages();
            if (CollectionUtils.isNotEmpty(messages)) {
                for (Message message : messages) {
                    emailInfos.add(getEmailInfo(message.getId()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emailInfos;
    }

    public static void downloadAttachments(String userId, String messageId, String attId, String filename) {
        try {
            MessagePartBody attachPart = service
                    .users()
                    .messages()
                    .attachments()
                    .get(userId, messageId, attId)
                    .execute();
            byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
            FileOutputStream fileOutFile = new FileOutputStream(filename);
            fileOutFile.write(fileByteArray);
            fileOutFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static EmailInfo getEmailInfo(String messageId) throws IOException {
        EmailInfo emailInfo = new EmailInfo();
        List<MessagePartHeader> headers = new ArrayList<>();
        Message message = service.users().messages().get(ME, messageId).setFormat("full").setFields("id,payload,sizeEstimate,snippet,threadId").execute();
        if (message != null) {
            emailInfo.setId(message.getId());
            emailInfo.setThreadId(message.getThreadId());
            emailInfo.setSnippet(message.getSnippet());
            emailInfo.setMimeType(message.getPayload().getMimeType());
            emailInfo.setMessageParts(message.getPayload().getParts());
            headers = message.getPayload().getHeaders();
        }
        if (CollectionUtils.isNotEmpty(headers)) {
            for (MessagePartHeader header : headers) {
                if (header.getName().equals("From")) {
                    fillFromNameAndEmail(header.getValue(), emailInfo);
                }
                if (header.getName().equals("To")) {
                    fillToNameAndEmail(header.getValue(), emailInfo);
                }
                if (header.getName().equals("Date")) {
                    emailInfo.setDate(new Date(Date.parse(header.getValue())));
                }
                if (header.getName().equals("Subject")) {
                    emailInfo.setSubject(header.getValue());
                }
            }
        }
        return emailInfo;
    }

    private static void fillFromNameAndEmail(String value, EmailInfo emailInfo) {
        if (StringUtils.isNotEmpty(value)) {
            String[] split = value.split("\"");
            if (split.length == 3) {
                emailInfo.setFromName(split[1]);
                emailInfo.setFromEmail(split[2].replace("<", "").replace(">", "").trim());
            } else if (split.length == 1) {
                String[] split1 = value.split("<");
                if (split1.length == 2) {
                    emailInfo.setFromName(split1[0].trim());
                    emailInfo.setFromEmail(split1[1].replace("<", "").replace(">", "").trim());
                }
            }
        }
    }

    private static void fillToNameAndEmail(String value, EmailInfo emailInfo) {
        if (StringUtils.isNotEmpty(value)) {
            String[] split = value.split("\"");
            if (split.length == 3) {
                emailInfo.setToName(split[1]);
                emailInfo.setToEmail(split[2].replace("<", "").replace(">", "").trim());
            }
        }
    }

    private static String getMailBody(Message message) throws MessagingException, IOException {
        byte[] decodedData;
        StringBuilder undecodeData = new StringBuilder();
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        if (message.getPayload().getMimeType().contains("multipart")) {
            List<MessagePart> parts = message.getPayload().getParts();
            for (MessagePart part : parts) {
                String data = part.getBody().getData();
                if (data != null) {
                    undecodeData.append(data);
                }
            }
            decodedData = Base64.decodeBase64(undecodeData.toString());
        } else {
            decodedData = message.getPayload().getBody().decodeData();
        }
        if (decodedData != null) {
            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(decodedData));
            return new BufferedReader(new InputStreamReader(email.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        }
        return "";
    }

    public static List<String> getLabelNames() {
        List<String> labelsResult = new ArrayList<>();
        ListLabelsResponse response = null;
        try {
            response = service.users().labels().list(ME).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Label> labels = (List<Label>) response.get("labels");
        for (Label gLabel : labels) {
            if (gLabel.getType().equals("user")) {
                labelsResult.add(gLabel.getName());
            }
        }
        return labelsResult;
    }

    private static List<String> getLabelIds(List<GMailLabels> gLabels) throws IOException {
        List<String> labelsResult = new ArrayList<>();
        ListLabelsResponse response = service.users().labels().list(ME).execute();
        List<Label> labels = (List<Label>) response.get("labels");
        for (GMailLabels gLabel : gLabels) {
            for (Label lbl : labels) {
                if (lbl.getName().equals(gLabel.getName())) {
                    labelsResult.add(lbl.getId());
                    break;
                }
            }
        }
        return labelsResult;
    }
}
