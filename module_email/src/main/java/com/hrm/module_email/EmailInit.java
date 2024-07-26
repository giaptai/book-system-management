package com.hrm.module_email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@Getter
@Setter
public class EmailInit {
    @Value("${spring.mail.host}")
    private String MAIL_HOST;
    @Value("${spring.mail.port}")
    private int MAIL_PORT;
    @Value("${spring.mail.username}")
    private String MAIL_USERNAME;
    @Value("${spring.mail.password}")
    private String MAIL_PASSWORD;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String MAIL_AUTH;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String MAIL_STARTTLS;
    private SimpleMailMessage message;
    private JavaMailSenderImpl sender;

    @Bean
    public JavaMailSender getJavaMailSender() {
        message = new SimpleMailMessage();
        sender = new JavaMailSenderImpl();
        System.out.println(MAIL_HOST);
        sender.setHost(MAIL_HOST);
        sender.setPort(MAIL_PORT);
        sender.setUsername(MAIL_USERNAME);
        sender.setPassword(MAIL_PASSWORD);
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", MAIL_AUTH);
        props.put("mail.smtp.starttls.enable", MAIL_STARTTLS);
        props.put("mail.debug", "true");
        return sender;
    }

    /**
     * Send mail with template
     * **/
    public void sendEMail(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_16.name());
            helper.setFrom("no-reply@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            sender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
