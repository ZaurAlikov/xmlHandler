import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import service.GMailService;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException, GeneralSecurityException, MessagingException {

        Gmail service = GMailService.getGMailService();
        String user = "me";
        JSONObject ticketDetails = new JSONObject();

        ListMessagesResponse openMessages = service.users().messages().list("me") //.setLabelIds(labelIds)
                .setQ("is:unread label:inbox").setMaxResults(new Long(3)).execute();
        ticketDetails.put("open", "" + openMessages.getResultSizeEstimate());
        ListMessagesResponse closedMessages = service.users().messages().list("me") //.setLabelIds(labelIds)
                .setQ("label:inbox label:closed").setMaxResults(new Long(1)).execute();
        ticketDetails.put("closed", "" + closedMessages.getResultSizeEstimate());

        ListMessagesResponse pendingMessages = service.users().messages().list("me") //.setLabelIds(labelIds)
                .setQ("label:inbox label:pending").setMaxResults(new Long(1)).execute();
        ticketDetails.put("pending", "" + pendingMessages.getResultSizeEstimate());

        ticketDetails.put("unassigned", "0");
        List<Message> messages = openMessages.getMessages();
        //List<Map> openTickets=new ArrayList<Map>();
        JSONArray openTickets = new JSONArray();
        String returnVal = "";
        // Print ID and snippet of each Thread.
        if (messages != null) {
            for (Message message : messages) {
                openTickets.add(new JSONObject(getBareGmailMessageDetails(message.getId(), service)));
            }
            ticketDetails.put("openTicketDetails", openTickets);
        }

        System.out.println(ticketDetails.toJSONString());

//        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
//        List<Label> labels = listResponse.getLabels();
//        service.users().messages().list(user).execute();
//        List<Message> messages = service.users().messages().list(user).execute().getMessages();
//        Message msg;
//        for (Message message : messages) {
//            msg = service.users().messages().get(user, message.getId()).setFormat("raw").execute();
//            Base64 base64Url = new Base64(true);
//            byte[] emailBytes = base64Url.decodeBase64(msg.getRaw());
//            Properties props = new Properties();
//            Session session = Session.getDefaultInstance(props, null);
//            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
//            email.getSubject();
//            String result = new BufferedReader(new InputStreamReader(email.getInputStream()))
//                    .lines().collect(Collectors.joining("\n"));
//            System.out.println(result);
//        }


//        if (labels.isEmpty()) {
//            System.out.println("No labels found.");
//        } else {
//            System.out.println("Labels:");
//            for (Label label : labels) {
//                System.out.printf("- %s\n", label.getName());
//            }
//        }

        MainReader reader = new MainReader();
        reader.read();
    }

    public static Map getBareGmailMessageDetails(String messageId, Gmail service) throws MessagingException {
        Map<String, Object> messageDetails = new HashMap<>();
        try {
            Message message = service.users().messages().get("me", messageId).setFormat("full")
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

            Base64 base64Url = new Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(message.getPayload().getBody().getData());
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
            String result = new BufferedReader(new InputStreamReader(email.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            messageDetails.put("body",result);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return messageDetails;

    }



}
