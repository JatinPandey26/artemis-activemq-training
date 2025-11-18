package org.artemis;

import org.apache.activemq.artemis.component.WebServerComponent;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.server.management.ManagementContext;
import org.apache.activemq.artemis.dto.AppDTO;
import org.apache.activemq.artemis.dto.BindingDTO;
import org.apache.activemq.artemis.dto.ManagementContextDTO;
import org.apache.activemq.artemis.dto.WebServerDTO;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.LogManager;

/**
 * Hello world!
 *
 */
public class DivertingExample
{
    public static void main( String[] args ) throws Exception {

        System.setProperty("hawtio.realm", "activemq");
        System.setProperty("hawtio.role", "amq");
        System.setProperty("hawtio.rolePrincipalClasses", "org.apache.activemq.artemis.spi.core.security.jaas.RolePrincipal");

        URL loginConfig = Thread.currentThread()
                .getContextClassLoader()
                .getResource("login.config");
        String decodedPath = java.net.URLDecoder.decode(loginConfig.getPath(), "UTF-8");
        System.setProperty("java.security.auth.login.config", decodedPath);

        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("logging.properties");

        LogManager.getLogManager().readConfiguration(
                is
        );



        EmbeddedActiveMQ server = new EmbeddedActiveMQ();
        server.setConfigResourcePath("broker.xml");
        ActiveMQSecurityManager activeMQSecurityManager = new ActiveMQJAASSecurityManager();
        server.setSecurityManager(activeMQSecurityManager);
        server.start();

        server.setMbeanServer(ManagementFactory.getPlatformMBeanServer());
        server.start();
        WebServerComponent webServerComponent = new WebServerComponent();
        WebServerDTO webServerDTO = new WebServerDTO();
        webServerDTO.path = "web";
        BindingDTO bindingDTO = new BindingDTO();
        bindingDTO.uri = "http://localhost:8161";
        AppDTO branding = new AppDTO();
        branding.name = "branding";
        branding.url = "activemq-branding";
        branding.war = "activemq-branding.war";
        AppDTO plugin = new AppDTO();
        plugin.name = "plugin";
        plugin.url = "artemis-plugin";
        plugin.war = "artemis-plugin.war";
        AppDTO console = new AppDTO();
        console.name = "console";
        console.url = "console";
        console.war = "console.war";
        bindingDTO.addApp(branding);
        bindingDTO.addApp(plugin);
        bindingDTO.addApp(console);
        webServerDTO.addBinding(bindingDTO);
        ActiveMQSecurityManager securityManager = server.getActiveMQServer().getSecurityManager();
        ManagementContextDTO managementDTO = new ManagementContextDTO();
        ManagementContext managementContext = org.apache.activemq.artemis.cli.factory.jmx.ManagementFactory.create(managementDTO, securityManager);
        server.getActiveMQServer().getManagementService().registerHawtioSecurity(managementContext.getArtemisMBeanServerGuard());
        URL webDirUrl = Thread.currentThread().getContextClassLoader()
                .getResource("");
        File webDir = new File(webDirUrl.toURI());
        webServerComponent.configure(webServerDTO,webDir.getAbsolutePath(), webDir.getAbsolutePath());
        server.getActiveMQServer().addExternalComponent(webServerComponent, true);


        System.out.println("artemis started");
        // 🔹 Step 1: Connection details
        String brokerUrl = "vm://localhost?create=false"; // Artemis broker URL
        String username = "admin";
        String password = "admin";

        // 🔹 Step 2: Create connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?create=false");

        // 🔹 Step 3: Create connection using username/password
        try (Connection connection = connectionFactory.createConnection(username, password)) {

            // 🔹 Step 4: Create session (non-transacted, auto acknowledge)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 🔹 Step 5: Create a queue reference
            Queue queue = session.createQueue("marketing");

            // 🔹 Step 6: Create producer
            MessageProducer producer = session.createProducer(queue);

            // 🔹 Step 7: Create a message with a filter property
            TextMessage message = session.createTextMessage("Order created successfully");
            message.setStringProperty("region", "US");  // filter property

            // 🔹 Step 8: Send the message
            producer.send(message);
            System.out.println("✅ Sent message with filter properties: region=USA, type=high");
        }
    }
}
