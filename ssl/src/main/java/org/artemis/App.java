package org.artemis;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "./client.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        String brokerUrl = "tcp://localhost:61617?sslEnabled=true"; // Artemis broker URL
        String username = "admin";
        String password = "admin";

        // 🔹 Step 2: Create connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        // 🔹 Step 3: Create connection using username/password
        try (Connection connection = connectionFactory.createConnection(username, password)) {

            // 🔹 Step 4: Create session (non-transacted, auto acknowledge)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 🔹 Step 5: Create a queue reference
            Queue queue = session.createQueue("queue/sslCheck");

            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("Hi over ssl");
            producer.send(message);
            System.out.println("Message send over ssl");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
