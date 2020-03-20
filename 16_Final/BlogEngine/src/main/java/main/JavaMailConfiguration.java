package main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Properties;

@Configuration
public class JavaMailConfiguration {

    private static final String EMAIL_ADDRESS_FROM = "SOME_EMAIL@GMAIL.COM";
    private static final String GMAIL_SECRET_MAIL_APPLICATION_KEY = "HERE_YOUR_SECRET_KEY_FOR_GMAIL";

//    @Bean
//    public SimpleMailMessage templateSimpleMessage() {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setText("This is the test email template for your email:\n%s\n");
//        return message;
//    }

    @Bean //TODO настройки задаются здесь или все же в application.yml?
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(EMAIL_ADDRESS_FROM);
        mailSender.setPassword(GMAIL_SECRET_MAIL_APPLICATION_KEY);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }
}
