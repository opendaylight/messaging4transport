/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.apache.qpid.amqp_1_0.jms.impl.*;
import org.apache.qpid.jms.*;
import javax.jms.*;

import org.opendaylight.messaging4transport.constants.Messaging4TransportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that publishes AMQP messages.
 */
public final class AmqpPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(AmqpPublisher.class);

    private AmqpPublisher() {
        throw new AssertionError("Instantiating utility class AmqpPublisher.");
    }

    /**
     * Publishes the RPC, data tree, and notifications to the given destination/topic.
     *
     * @param msg - the message text to be sent
     */
    public static void publish(String msg) {
        String destination = Messaging4TransportConstants.AMQP_TOPIC_EVENT_DESTINATION;
        try {
            publish(destination, msg);
            LOG.info(msg);
        } catch (JMSException e) {
            LOG.error("JMS Exception in publishing to the AMQP broker", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted Exception in publishing to the AMQP broker", e);
        }
    }

    /**
     * Publishes the data to the given destination
     *
     * @param destinationStr The destination topic
     * @param msg         - the message text to be sent
     * @throws JMSException         if sending the data to the broker fails
     * @throws InterruptedException if interrupted
     */
    public static void publish(String destinationStr, String msg) throws JMSException, InterruptedException {
        String user = AmqpConfig.getUser();
        String password = AmqpConfig.getPassword();
        String connectionUri = AmqpConfig.getConnectionURI();

      try {
        JmsConnectionFactory factory = new JmsConnectionFactory(connectionUri);

        Connection connection = factory.createConnection(user, password);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(destinationStr);

        MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            sendMessages(session, producer, msg);
        } catch (JMSException exception) {
            LOG.info("External Broker Not Initialized To Publish The Messages", exception);
        }
    }

    /**
     * Gets the AMQP session to initiate the connection with the broker.
     * @param host     the host
     * @param port     the port
     * @param user     the user name
     * @param password the password
     * @return the AMQP session
     * @throws JMSException if initializing the session failed.
     */
    @Deprecated
    public static Session getAmqpSession(String host, int port, String user, String password) throws JMSException {
        ConnectionFactoryImpl factory = new ConnectionFactoryImpl(host, port, user, password);

        Connection connection = factory.createConnection(user, password);
        try {
            connection.start();
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch(javax.jms.JMSException exception) {
            LOG.info("External Broker Not Initialized", exception);
            throw new JMSException("Connection was not initialized at host: "+ host + "and port: "+ port);
        } finally {
            connection.close();
        }
    }

    /**
     * Gets the AMQP session to initiate the connection with the broker.
     * @param connectionURI     the connection URI, composed of host and port.
     * @param user     the user name
     * @param password the password
     * @return the AMQP session
     * @throws JMSException if initializing the session failed.
     */
    public static Session getAmqpSession(String connectionURI, String user, String password) throws JMSException {
        JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);

        Connection connection = factory.createConnection(user, password);
        try {
            connection.start();
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch(javax.jms.JMSException exception) {
            LOG.info("External Broker Not Initialized", exception);
            throw new JMSException("Connection was not initialized at connection URI "+ connectionURI);
        } finally {
            connection.close();
        }
    }

    /**
     * Generates random messages
     *
     * @param session  session
     * @param producer message producer
     * @param msgText  the message to be sent in text format.
     * @throws JMSException if message transfer failed.
     */
    public static void sendMessages(Session session, MessageProducer producer, String msgText) throws JMSException {
        TextMessage msg = session.createTextMessage(msgText);
        producer.send(msg);
    }
}
