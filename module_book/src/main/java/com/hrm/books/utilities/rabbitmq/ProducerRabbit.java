// package com.hrm.books.utilities.rabbitmq;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.hrm.books.utilities.constants.Constants;
// import com.hrm.books.utilities.exceptions.MyException;
// import com.rabbitmq.client.AMQP;
// import com.rabbitmq.client.Channel;
// import com.rabbitmq.client.Connection;
// import com.rabbitmq.client.ConnectionFactory;
// import lombok.Getter;
// import lombok.Setter;
// import org.springframework.context.annotation.Configuration;

// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.io.ObjectOutputStream;
// import java.nio.charset.StandardCharsets;
// import java.util.Date;
// import java.util.concurrent.TimeoutException;

// @Configuration
// @Getter
// @Setter
// public class ProducerRabbit {
//     private ConnectionFactory factory;
//     private Connection connection;
//     private Channel channel;
//     private MyException myEx;
//     private ObjectMapper objectMapper = new ObjectMapper();

//     public ProducerRabbit() {
//         factory = new ConnectionFactory();
//         factory.setHost(Constants.host); //localhost
//         try {
//             this.connection = factory.newConnection();
//             this.channel = connection.createChannel();
//         } catch (IOException | TimeoutException e) {
//             throw new RuntimeException(e);
// //            throw myEx.new myGlobalError(e.getMessage());
//         }
//     }

//     public void sendSimpleMessage(String message) {
//         try {
//             channel.queueDeclare("CRUSH", false, false, false, null);
//             channel.basicPublish("", "CRUSH", new AMQP.BasicProperties.Builder().timestamp(new Date()).build(), message.getBytes(StandardCharsets.UTF_8));
//             System.err.printf("%1$-15s", message);
//         } catch (IOException e) {
//             System.err.println(e.getMessage());
//             throw new RuntimeException(e);
//         }
//     }

//     public <T> void sendMessage(String queueName, T message) {
//         try {
// //            ByteArrayOutputStream baos = new ByteArrayOutputStream();
// //            ObjectOutputStream oos = new ObjectOutputStream(baos);
// //            oos.writeObject(message);
// //            oos.flush();
// //            byte[] messageBytes = baos.toByteArray();
//             byte[] bytes = objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
//             channel.queueDeclare(queueName, false, false, false, null);
//             channel.basicPublish("", queueName, new AMQP.BasicProperties.Builder().timestamp(new Date()).build(), bytes);
//             System.err.printf("%1$-15s", message);
//         } catch (IOException e) {
//             System.err.println(e.getMessage());
//             throw new RuntimeException(e);
//         }
//     }

//     public <T> void sendMessageUpdateBillState(String queueName, T message) {
//         try {
//             ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             ObjectOutputStream oos = new ObjectOutputStream(baos);
//             oos.writeObject(message);
//         } catch (IOException e) {
//             System.err.println(e.getMessage());
//             throw new RuntimeException(e);
//         }
//     }
// }
