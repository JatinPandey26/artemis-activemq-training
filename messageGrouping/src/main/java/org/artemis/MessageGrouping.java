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
import java.util.concurrent.ConcurrentHashMap;

public class MessageGrouping
{
    public static void main( String[] args ) throws JMSException {
        String brokerUrl = "tcp://localhost:61616"; // Artemis broker URL
        String username = "admin";
        String password = "admin";

        // 🔹 Step 2: Create connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        final Map<String, String> messageReceiverMap = new ConcurrentHashMap<>();


        // 🔹 Step 3: Create connection using username/password
        try (Connection connection = connectionFactory.createConnection(username, password)) {

            // 🔹 Step 4: Create session (non-transacted, auto acknowledge)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 🔹 Step 5: Create a queue reference
            Queue queue = session.createQueue("queue/messageGroupQueue");
            MessageProducer producer = session.createProducer(queue);

            // Step 7. Create two consumers
            MessageConsumer consumer1 = session.createConsumer(queue);
            consumer1.setMessageListener(new SimpleMessageListener("consumer-1", messageReceiverMap));
            MessageConsumer consumer2 = session.createConsumer(queue);
            consumer2.setMessageListener(new SimpleMessageListener("consumer-2", messageReceiverMap));

            // Step 8. Create and send 10 text messages with group id 'Group-0'
            int msgCount = 10;
            TextMessage[] groupMessages = new TextMessage[msgCount];
            for (int i = 0; i < msgCount; i++) {
                groupMessages[i] = session.createTextMessage("Group-0 message " + i);
                groupMessages[i].setStringProperty("JMSXGroupID", "Group-0");
                producer.send(groupMessages[i]);
                System.out.println("Sent message: " + groupMessages[i].getText());
            }

            System.out.println("all messages are sent");

            // Step 9. Start the connection
            connection.start();



        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static class SimpleMessageListener implements MessageListener {

        private final String name;
        private final Map<String, String> messageReceiverMap;

        SimpleMessageListener(final String listenerName, Map<String, String> messageReceiverMap) {
            name = listenerName;
            this.messageReceiverMap = messageReceiverMap;
        }

        @Override
        public void onMessage(final Message message) {
            try {
                TextMessage msg = (TextMessage) message;
                System.out.format("Message: [%s] received by %s%n", msg.getText(), name);
                messageReceiverMap.put(msg.getText(), name);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
