package com.hrm.module_email.rabbitmq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.module_email.EmailInit;
import com.hrm.module_email.enums.RMQQueueName;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hrm.module_email.dto.CVisitor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Configuration
@Getter
@Setter
public class ConsumerRabbit {
    @Value("${rabbitmq.host}")
    private String host;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final EmailInit emailInit;
    private ObjectMapper objectMapper = new ObjectMapper();


    public ConsumerRabbit(EmailInit emailInit) {
        this.emailInit = emailInit;
        factory = new ConnectionFactory();
        factory.setHost(host); //localhost
        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public int receiveSimpleMessage() {
        CVisitor[] visitor = {null};
        try {
            channel.queueDeclare("CRUSH", false, false, false, null);
            DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    String json = new String(message.getBody(), StandardCharsets.UTF_8);
                    visitor[0] = objectMapper.readValue(json, CVisitor.class);
                    System.err.printf("Message: %1s\nTimestamp: %2tc\n", json, message.getProperties().getTimestamp());

                    if (visitor[0] != null) {
                        StringBuilder builder = new StringBuilder("Username: " + visitor[0].username());
                        builder.append("\n").append("Password: ").append(visitor[0].password());
                        emailInit.sendEMail(visitor[0].email(), "Register successfully !!!", builder.toString());
                    }
                }
            };
            channel.basicConsume("CRUSH", true, deliverCallback, new ConsumerShutdownSignalCallback() {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.err.println("CRUSH GET MESSAGE");
                }
            });
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Bean
    public int receiveMessage() {
        CVisitor[] visitor = {null, null, null, null, null};
        try {
            channel.queueDeclare(RMQQueueName.REGISTER.getValue(), false, false, false, null);
            channel.queueDeclare(RMQQueueName.CHANGE_PASSWORD.getValue(), false, false, false, null);
            channel.queueDeclare(RMQQueueName.CHANGE_EMAIL.getValue(), false, false, false, null);
            channel.queueDeclare(RMQQueueName.PURCHASE.getValue(), false, false, false, null);
            channel.queueDeclare(RMQQueueName.PURCHASE_STATE.getValue(), false, false, false, null);

            DeliverCallback deliverCallbackRegister = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    String json = new String(message.getBody(), StandardCharsets.UTF_8);
//                    visitor[0] = objectMapper.readValue(json, CVisitor.class);
                    visitor[0] = objectMapper.readValue(json, new TypeReference<CVisitor>() {
                    });

                    System.err.printf("Message: %1s\nTimestamp: %2tc\n", json, message.getProperties().getTimestamp());

                    if (visitor[0] != null) {
                        StringBuilder builder = new StringBuilder("Username: " + visitor[0].username());
                        builder.append("\n").append("Password: ").append(visitor[0].password());
                        emailInit.sendEMail(visitor[0].email(), "Register successfully !!!", builder.toString());
                    }
                }
            };
            //CHANGE-PASSWORD
            DeliverCallback deliverCallbackChangePassword = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    String json = new String(message.getBody(), StandardCharsets.UTF_8);
                    visitor[1] = objectMapper.readValue(json, CVisitor.class);
                    System.err.printf("Message: %1s\nTimestamp: %2tc\n", json, message.getProperties().getTimestamp());

                    if (visitor[1] != null) {
                        emailInit.sendEMail(visitor[1].email(), "CHANGE PASSWORD SUCCESSFULLY !!!",
                                "Hi " + visitor[1].username() + ",<br><br>" +
                                        "Your new password: " + visitor[1].password().replace("{noop}", "") +
                                        "<br><br>Thanks,<br>Book Empire"
                        );
                    }
                }
            };
            //CHANGE-EMAIL
            DeliverCallback deliverCallbackChangeEmail = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    String json = new String(message.getBody(), StandardCharsets.UTF_8);
                    visitor[2] = objectMapper.readValue(json, CVisitor.class);
                    System.err.printf("Message: %1s\nTimestamp: %2tc\n", json, message.getProperties().getTimestamp());

                    if (visitor[2] != null) {
                        emailInit.sendEMail(visitor[2].email(), "CHANGE EMAIL SUCCESSFULLY !!!",
                                "Hi " + visitor[2].username() + ",<br><br>" +
                                        "We will now be using this email address for all future communication, including important updates, invoices, and project information.<br><br>" +
                                        "Thanks,<br>Book Empire"
                        );
                    }
                }
            };
            //PURCHASE
            DeliverCallback deliverCallbackPurchase = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    String json = new String(message.getBody(), StandardCharsets.UTF_8);
                    Map<String, Object> objectMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
                    });
                    System.err.printf("Message: %1s\nTimestamp: %2tc\n", json, message.getProperties().getTimestamp());
                    emailTemplate(objectMap);
                }
            };
            //PURCHASE-STATE
            DeliverCallback deliverCallbackPurchaseState = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    String json = new String(message.getBody(), StandardCharsets.UTF_8);
                    List<Map<String, Object>> objectMap = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
                    });
                    System.err.printf("Message: %1s\nTimestamp: %2tc\n", json, message.getProperties().getTimestamp());
                    for (int i = 0; i < objectMap.size(); i++) {
                        emailTemplateState(objectMap.get(i));
                    }
                }
            };
            channel.basicConsume(RMQQueueName.REGISTER.getValue(), true, deliverCallbackRegister, new ConsumerShutdownSignalCallback() {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.err.println("CRUSH GET MESSAGE REGISTER");
                }
            });
            channel.basicConsume(RMQQueueName.CHANGE_PASSWORD.getValue(), true, deliverCallbackChangePassword, new ConsumerShutdownSignalCallback() {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.err.println("CRUSH GET MESSAGE CHANGE PASSWORD");
                }
            });
            channel.basicConsume(RMQQueueName.CHANGE_EMAIL.getValue(), true, deliverCallbackChangeEmail, new ConsumerShutdownSignalCallback() {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.err.println("CRUSH GET MESSAGE EMAIL");
                }
            });
            channel.basicConsume(RMQQueueName.PURCHASE.getValue(), true, deliverCallbackPurchase, new ConsumerShutdownSignalCallback() {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.err.println("CRUSH GET PURCHASE INFORMATION");
                }
            });
            channel.basicConsume(RMQQueueName.PURCHASE_STATE.getValue(), true, deliverCallbackPurchaseState, new ConsumerShutdownSignalCallback() {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.err.println("CRUSH GET PURCHASE STATE INFORMATION");
                }
            });
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return 1;
    }

    private void emailTemplate(Map<String, Object> objectMap) {
        Map<String, Object> objBill = (Map<String, Object>) objectMap.get("bill");
        Map<String, String> objVisitor = (Map<String, String>) objBill.get("visitor");
        if (objVisitor.get("email") != null) {
            String price = String.format("%,.2f", Double.parseDouble(objBill.get("totalPrice").toString()));
            String emailContent = """
                    <!DOCTYPE html>
                             <html>
                             <head>
                                 <title>Purchase Confirmation</title>
                             </head>
                             <body>
                                 <h1 style="text-align: center;">PURCHASE SUCCESSFUL !!!</h1>
                                 <p>Dear <b>%s</b>,</p>
                                 <p>Thank you for your email regarding the purchase order. We appreciate your business and are pleased to confirm the following details:</p>
                                 <article>
                                     <ul>
                                        <li><b>Order ID:</b> %s</li>
                                        <li><b>Quantity:</b> %s</li>
                                        <li><b>Total price:</b> %s</li>
                                        <li><b>Order state:</b> <i>%s</i></li>
                                     </ul>
                                     <b>Buyer Information:</b>
                                     <ul>
                                         <li><b>Buyer name:</b> %s</li>
                                         <li><b>Buyer phone:</b> %s</li>
                                         <li><b>Buyer Address:</b> %s</li>
                                     </ul>
                                     <p>Thank you for your attention, and please feel free to reach out if you need any further information.</p>
                                 </article><p>Regards,<br>Book Empire</p>
                             </body>
                             </html>
                                    """.formatted(
                    objVisitor.get("username"),
                    objBill.get("id"),
                    objBill.get("totalAmount"),
                    price,
                    objBill.get("state"),
                    objBill.get("buyerName"),
                    objBill.get("buyerPhone"),
                    objBill.get("deliveryAddress")
            );
            emailInit.sendEMail(objVisitor.get("email"), "PURCHASE SUCCESSFUL !!!", emailContent);
        } else throw new RuntimeException("OBJECT IS NULL");
    }
    private void emailTemplateState(Map<String, Object> objectMap) {
        Map<String, String> objVisitor = (Map<String, String>) objectMap.get("visitor");
        if (objVisitor.get("email") != null) {
            String price = String.format("%,.2f", Double.parseDouble(objectMap.get("totalPrice").toString()));
            String emailContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Purchase Confirmation</title>
                    </head>
                    <body>
                        <h1 style="text-align: center;">PURCHASE STATE !!!</h1>
                        <p>Dear <b>%s</b>,</p>
                        <p>We’re pleased to share that your order is now in the <strong><em>%s</em></strong> stage. Here are the details:</p>
                        <article>
                            <ul>
                                <li><b>Order ID:</b> %s</li>
                                <li><b>Quantity:</b> %s</li>
                                <li><b>Total price:</b> %s</li>
                            </ul>
                            <b>Buyer Information:</b>
                            <ul>
                                <li><b>Buyer name:</b> %s</li>
                                <li><b>Buyer phone:</b> %s</li>
                                <li><b>Buyer Address:</b> %s</li>
                            </ul>
                            <p>Thank you for your attention, and please feel free to reach out if you need any further information.</p>
                        </article>
                        <p>Regards,<br>Book Empire</p>
                    </body>
                    </html>""".formatted(
                    objVisitor.get("username"),
                    objectMap.get("state"),
                    objectMap.get("id"),
                    objectMap.get("totalAmount"),
                    price,
                    objectMap.get("buyerName"),
                    objectMap.get("buyerPhone"),
                    objectMap.get("deliveryAddress")
            );
            emailInit.sendEMail(objVisitor.get("email"), "PURCHASE SUCCESSFUL !!!", emailContent);
        } else throw new RuntimeException("OBJECT IS NULL");
    }
}
