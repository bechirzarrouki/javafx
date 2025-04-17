package Utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.UnsupportedEncodingException;

public class EmailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    // Gmail credentials
    private static final String USERNAME = "mahmoudlengliz000@gmail.com"; // Your Gmail address
    // Replace this with the 16-character App Password you generated
    private static final String PASSWORD = "eigm hvca uzhc emkz";

    /**
     * Sends a plain text email
     */
    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        sendEmail(to, subject, content, false);
    }
    
    /**
     * Sends an email with optional HTML formatting
     */
    public static void sendEmail(String to, String subject, String content, boolean isHtml) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, "InnovMatch Support"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            
            if (isHtml) {
                message.setContent(content, "text/html; charset=utf-8");
            } else {
                message.setText(content);
            }

            Transport.send(message);
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Error setting sender name", e);
        }
    }
    
    /**
     * Creates a professional HTML email for password reset
     */
    public static void sendPasswordResetEmail(String to, String resetCode) throws MessagingException {
        String subject = "InnovMatch - Password Reset Code";
        
        String htmlContent = ""
            + "<html><head>"
            + "<style>"
            + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
            + ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }"
            + ".header { background-color: #6a3de8; color: white; padding: 15px; text-align: center; border-radius: 5px 5px 0 0; }"
            + ".content { padding: 20px; }"
            + ".code { font-size: 24px; font-weight: bold; background-color: #f5f5f5; padding: 10px; text-align: center; letter-spacing: 5px; margin: 20px 0; }"
            + ".footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }"
            + "</style>"
            + "</head><body>"
            + "<div class='container'>"
            + "<div class='header'><h2>InnovMatch Password Reset</h2></div>"
            + "<div class='content'>"
            + "<p>Hello,</p>"
            + "<p>We received a request to reset your password for your InnovMatch account. Please use the following code to complete the password reset process:</p>"
            + "<div class='code'>" + resetCode + "</div>"
            + "<p>This code is valid for 15 minutes. If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>"
            + "<p>Thank you,<br>The InnovMatch Team</p>"
            + "</div>"
            + "<div class='footer'>"
            + "<p>This is an automated message, please do not reply to this email.</p>"
            + "<p>&copy; " + java.time.Year.now().getValue() + " InnovMatch. All rights reserved.</p>"
            + "</div>"
            + "</div>"
            + "</body></html>";
        
        sendEmail(to, subject, htmlContent, true);
    }
}
