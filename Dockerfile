# Use Debian-based JRE
FROM eclipse-temurin:17-jre

ENV ARTEMIS_VERSION=2.38.0
ENV ARTEMIS_HOME=/opt/artemis
ENV ARTEMIS_USER=admin
ENV ARTEMIS_PASSWORD=admin

# Install dependencies
RUN apt-get update && \
    apt-get install -y wget unzip vim && \
    wget https://archive.apache.org/dist/activemq/activemq-artemis/${ARTEMIS_VERSION}/apache-artemis-${ARTEMIS_VERSION}-bin.zip && \
    unzip apache-artemis-${ARTEMIS_VERSION}-bin.zip -d /opt && \
    mv /opt/apache-artemis-${ARTEMIS_VERSION} ${ARTEMIS_HOME} && \
    rm apache-artemis-${ARTEMIS_VERSION}-bin.zip && \
    apt-get clean

RUN chmod -R 777 /opt/artemis && \
    chmod -R 777 /tmp

# Expose ports (console + broker)
EXPOSE 8161 61616

# Set working directory
WORKDIR ${ARTEMIS_HOME}

# # Run Artemis in foreground (so container stays alive)
# CMD ["./bin/artemis", "run"]

# Keep container running without starting broker
CMD ["tail", "-f", "/dev/null"]
