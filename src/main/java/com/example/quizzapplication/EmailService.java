package com.example.quizzapplication;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {
    public static int sendEmail(String message, String number, String email, String name) {

        // Credentials for the email used to send messages
        final String uname = "team.echo.quiz25@gmail.com";
        final String pass = "szwtvaytkunebjzi";

        final String[] toEmails = {"robertehansen@csus.edu", "huberther@csus.edu", "warnerkanzler@csus.edu "};


        // Pointing to Gmail SMTP server
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", "465");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, pass);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);

            // Add the recipients to the email
            for (String toEmail : toEmails) {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            }

            mimeMessage.setFrom(new InternetAddress("team.echo.quiz25@gmail.com"));
            mimeMessage.setSubject("Quizz Application - New Message from " + email);
            mimeMessage.setText("Message from: " + email + "\nPhone number: " + number + "\nName: " + name +"\n\n" + message);

            Transport.send(mimeMessage);

            return 0;

        } catch (MessagingException e) {
            System.out.println("Email failed to send.");
            return 1;
        }
    }
}
