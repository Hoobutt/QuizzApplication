import com.example.quizzapplication.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

public class MailTest {
    @Test
    public void testMail() {
        EmailService emailService = new EmailService();

        // send email to testing message instead of spamming the whole team
        int returnValue = emailService.sendEmail("Test Message", "1234567890", "robbyhansen101@gmail.com", "Test User", true);
        MimeMessage sentMessage = emailService.getMimeMessageHook();

        assert sentMessage != null;
        assert returnValue == 0;
    }
}
