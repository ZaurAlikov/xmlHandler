import model.GMailLabels;
import service.GMailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, GeneralSecurityException, MessagingException {
        Map<String, String> email = GMailService.getEmail("is:unread label:inbox", Arrays.asList(GMailLabels.DELIVERY, GMailLabels.INBOX), 10L);
        MainReader reader = new MainReader();
        reader.read();
    }


}
