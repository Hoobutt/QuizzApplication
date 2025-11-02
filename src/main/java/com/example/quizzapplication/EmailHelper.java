package com.example.quizzapplication;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailHelper {
    public static int sendEmail(String message, String number, String email, String name) {

        Properties properties = System.getProperties();

        Session session = Session.getDefaultInstance(properties);

        properties.setProperty("");

        try {
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(""));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(""));
            mimeMessage.setSubject("Quizz Application - New Message from " + email);
            mimeMessage.setText("Message from: " + email + "\nPhone number: " + number + "\nName: " + "name" +"\n\n" + message);

            Transport.send(mimeMessage);

            return 0;

        } catch (MessagingException e) {
            System.out.println("Email failed to send.");
            return 1;
        }
    }
}
