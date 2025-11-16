package org.artemis;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class ConsumerPriority
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
            Queue queue = session.createQueue("queue/consumerPriority?consumer-priority=50");
            Queue queue1 = session.createQueue("queue/consumerPriority?consumer-priority=10");


            // 🔹 Step 6: Create producer
            MessageProducer producer = session.createProducer(queue);

            for (int i = 0; i < 10; i++) {
                // 🔹 Step 7: Create a message
                TextMessage message = session.createTextMessage("Order created successfully");
                // 🔹 Step 8: Send the message
                producer.send(message);
            }

            MessageConsumer consumer1 = session.createConsumer(queue);
            MessageConsumer consumer2 = session.createConsumer(queue1);

            consumer1.setMessageListener(new SimpleMessageListener("consumerWithMorePriority"));

            consumer2.setMessageListener(new SimpleMessageListener("consumerWithLessPriority"));

            connection.start();
        }
    }

    static class SimpleMessageListener implements MessageListener {

        private final String name;

        SimpleMessageListener(final String listenerName) {
            name = listenerName;
        }

        @Override
        public void onMessage(final Message message) {
            try {
                TextMessage msg = (TextMessage) message;
                System.out.format("Message: [%s] received by %s%n", msg.getText(), name);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
