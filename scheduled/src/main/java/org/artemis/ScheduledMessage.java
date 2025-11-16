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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class ScheduledMessage
{
    public static void main( String[] args ) throws JMSException {
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
            Queue queue = session.createQueue("ordersScheduled");

            // 🔹 Step 6: Create producer
            MessageProducer producer = session.createProducer(queue);

            // 🔹 Step 7: Create a message with a filter property
            TextMessage message = session.createTextMessage("Order created successfully");
            long time = System.currentTimeMillis();
            time += 5000;
            message.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), time);


            // 🔹 Step 8: Send the message
            producer.send(message);
            System.out.println("Sent message: " + message.getText());
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            System.out.println("Time of send: " + formatter.format(new Date()));
            MessageConsumer messageConsumer = session.createConsumer(queue);
            connection.start();

            TextMessage messageReceived = (TextMessage) messageConsumer.receive();


            System.out.println("Received message: " + messageReceived.getText());
            System.out.println("Time of receive: " + formatter.format(new Date()));
        }
    }
}
