package org.artemis;

import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Hello world!
 *
 */
public class LastValueQueue {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616"; // Artemis broker URL
        String username = "admin";
        String password = "admin";

        // 🔹 Step 2: Create connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        // 🔹 Step 3: Create connection using username/password
        try (Connection connection = connectionFactory.createConnection(username, password)) {

            // 🔹 Step 4: Create session (non-transacted, auto acknowledge)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 🔹 Step 5: Create a queue reference
            Queue queue = session.createQueue("lastValueQueue?last-value-key=unique_last_value_code");

            // 🔹 Step 6: Create producer
            MessageProducer producer = session.createProducer(queue);

            MessageConsumer messageConsumer = session.createConsumer(queue);

            // send 1st message with Last-Value property `reuters_code` set to `VOD`
            TextMessage message = session.createTextMessage("1st message with Last-Value property set");
            message.setStringProperty("unique_last_value_code", "1234");
            producer.send(message);

// send 2nd message with Last-Value property `reuters_code` set to `VOD`
            message = session.createTextMessage("2nd message with Last-Value property set");
            message.setStringProperty("unique_last_value_code", "1234");
            producer.send(message);

// only the 2nd message will be received: it is the latest with
// the Last-Value property set
            connection.start();
            TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);
            System.out.format("Received message: %s\n", messageReceived.getText());

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
