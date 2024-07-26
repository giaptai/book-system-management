//package com.hrm.books.utilities.send_mail;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//@Getter
//@Setter
//public class EmailInit {
//    @Value("${spring.mail.host}")
//    private String MAIL_HOST;
//    @Value("${spring.mail.port}")
//    private int MAIL_PORT;
//    @Value("${spring.mail.username}")
//    private String MAIL_USERNAME;
//    @Value("${spring.mail.password}")
//    private String MAIL_PASSWORD;
//    @Value("${spring.mail.properties.mail.smtp.auth}")
//    private String MAIL_AUTH;
//    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
//    private String MAIL_STARTTLS;
//    private SimpleMailMessage message;
//    private JavaMailSenderImpl sender;
//
//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        message = new SimpleMailMessage();
//        sender = new JavaMailSenderImpl();
//        System.out.println(MAIL_HOST);
//        sender.setHost(MAIL_HOST);
//        sender.setPort(MAIL_PORT);
//        sender.setUsername(MAIL_USERNAME);
//        sender.setPassword(MAIL_PASSWORD);
//        Properties props = sender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", MAIL_AUTH);
//        props.put("mail.smtp.starttls.enable", MAIL_STARTTLS);
//        props.put("mail.debug", "true");
//        return sender;
//    }
//
//    public void sendSimpleMail(String to, String subject, String text) {
//        message.setFrom("noreply@gmail.com");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        sender.send(message);
//    }
//}
