/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.sample;


import org.apache.qpid.amqp_1_0.jms.impl.*;
import org.opendaylight.messaging4transport.impl.AmqpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Sample AmqpListener Implementation.
 */
public final class AmqpListener {
    private static final Logger LOG = LoggerFactory.getLogger(AmqpListener.class);

    private AmqpListener() {
        throw new AssertionError("Instantiating utility class AmqpListener.");
    }

    public static void main(String[] args) throws JMSException {
        String user = AmqpConfig.getUser();
        String password = AmqpConfig.getPassword();
        String host = AmqpConfig.getHost();
        int port = AmqpConfig.getPort();

        String destination = arg(args, 0, AmqpConfig.getDestination());

        ConnectionFactoryImpl factory = new ConnectionFactoryImpl(host, port, user, password);

        Connection connection = factory.createConnection(user, password);
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(AmqpConfig.getJmsDestination(destination));

            LOG.info("Waiting for messages...");
            while (true) {
                Message msg = consumer.receive();
                if (msg instanceof TextMessage) {
                    String body = ((TextMessage) msg).getText();
                    LOG.info(body);
                } else {
                    LOG.error("Unexpected message type: " + msg.getClass());
                }
            }
        } finally {
            connection.close();
        }
    }

    private static String arg(String[] args, int index, String defaultValue) {
        if (index < args.length) {
            return args[index];
        } else {
            return defaultValue;
        }
    }
}
