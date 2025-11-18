package org.artemis;

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
import java.util.Random;

public class App
{
    public static void main( String[] args ) throws JMSException, InterruptedException {
            String brokerUrl = "tcp://localhost:61616"; // Artemis broker URL
            String username = "admin";
            String password = "admin";

            // 🔹 Step 2: Create connection factory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            String [] queueNames = {"pqueue,test_queue,orderQueue,PackingQueue"};

            while(true) {

                String randomQueue = queueNames[(int) (Math.random() * queueNames.length)];

                // 🔹 Step 3: Create connection using username/password
                try (Connection connection = connectionFactory.createConnection(username, password)) {

                    // 🔹 Step 4: Create session (non-transacted, auto acknowledge)
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    // 🔹 Step 5: Create a queue reference
                    Queue queue = session.createQueue("randomQueue");

                    // 🔹 Step 6: Create producer
                    MessageProducer producer = session.createProducer(queue);
                    Random random = new Random();
                    int messageCount = 10 + random.nextInt(91);
                    for(int i = 0 ; i < messageCount ; i++){
                        // 🔹 Step 7: Create a message with a filter property
                        TextMessage message = session.createTextMessage("Order created successfully");
                        // 🔹 Step 8: Send the message
                        producer.send(message);
                    }

                    System.out.println("Send");

                }

                Thread.sleep(5);
            }
    }
}
