package service;

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
import model.GMailLabels;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class GMailService {

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String ME = "me";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

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

    private static Gmail getGMailService() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Map<String, String> getEmail(String q, List<GMailLabels> labels, long maxResults) throws GeneralSecurityException, IOException, MessagingException {
        List<String> labelIds = getLabelIds(labels);
        JSONObject ticketDetails = new JSONObject();
        Gmail service = getGMailService();
        ListMessagesResponse openMessages = service.
                users()
                .messages()
                .list(ME)
//                .setLabelIds(labelIds)
//                .setQ(q)                          // Фильтр - "is:unread label:inbox"   "label:inbox label:closed"  "label:inbox label:pending"
                .setMaxResults(maxResults)
                .execute();
        List<Message> messages = openMessages.getMessages();
        JSONArray openTickets = new JSONArray();
        if (messages != null) {
            for (Message message : messages) {
                openTickets.add(new JSONObject(getBareGmailMessageDetails(message.getId(), service)));
            }
            ticketDetails.put("openTicketDetails", openTickets);
        }
        return ticketDetails;
    }

    private static Map getBareGmailMessageDetails(String messageId, Gmail service) throws MessagingException {
        Map<String, Object> messageDetails = new HashMap<>();
        try {
            Message message = service.users().messages().get(ME, messageId).setFormat("full")
                    .setFields("id,payload,sizeEstimate,snippet,threadId").execute();
            List<MessagePartHeader> headers = message.getPayload().getHeaders();
            for (MessagePartHeader header : headers) {
                if (header.getName().equals("From") || header.getName().equals("Date")
                        || header.getName().equals("Subject") || header.getName().equals("To")
                        || header.getName().equals("CC")) {
                    messageDetails.put(header.getName().toLowerCase(), header.getValue());
                }
            }
            messageDetails.put("snippet", message.getSnippet());
            messageDetails.put("threadId", message.getThreadId());
            messageDetails.put("id", message.getId());
            messageDetails.put("body", getMailBody(message));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return messageDetails;
    }

    private static String getMailBody(Message message) throws MessagingException, IOException {
        byte[] decodedData;
        StringBuilder undecodeData = new StringBuilder();
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        if (message.getPayload().getMimeType().contains("multipart")) {
            List<MessagePart> parts = message.getPayload().getParts();
            for (MessagePart part : parts) {
                undecodeData.append(part.getBody().getData());
            }
            decodedData = decodeData(undecodeData.toString());
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

    private static byte[] decodeData(String data) {
        org.apache.commons.codec.binary.Base64 base64Url = new Base64(true);
        return base64Url.decodeBase64(data);
    }

    private static List<String> getLabelIds(List<GMailLabels> gLabels) throws GeneralSecurityException, IOException {
        List<String> labelsResult = new ArrayList<>();
        Gmail service = getGMailService();
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
