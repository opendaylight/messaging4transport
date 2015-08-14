/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.sample;


import org.apache.qpid.amqp_1_0.jms.impl.*;
import org.opendaylight.messaging4transport.constants.Messaging4TransportConstants;
import org.opendaylight.messaging4transport.impl.AMQPConfig;

import javax.jms.*;

/**
 * Sample AMQP Listener Implementation.
 */
public class Listener {
    public static void main(String []args) throws JMSException {
        String user = AMQPConfig.getUser();
        String password = AMQPConfig.getPassword();
        String host = AMQPConfig.getHost();
        int port = AMQPConfig.getPort();

        String destination = arg(args, 0, Messaging4TransportConstants.AMQP_TOPIC_EVENT_DESTINATION);

        ConnectionFactoryImpl factory = new ConnectionFactoryImpl(host, port, user, password);

        Connection connection = factory.createConnection(user, password);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(AMQPConfig.getDestination(destination));
        long start = System.currentTimeMillis();
        long count = 1;
        System.out.println("Waiting for messages...");
        while(true) {
            Message msg = consumer.receive();
            if( msg instanceof  TextMessage ) {
                String body = ((TextMessage) msg).getText();
                if( "SHUTDOWN".equals(body)) {
                    long diff = System.currentTimeMillis() - start;
                    System.out.println(String.format("Received %d in %.2f seconds", count, (1.0*diff/1000.0)));
                    connection.close();
                    System.exit(1);
                } else {
                    try {
                        if( count != msg.getIntProperty("id") ) {
                            System.out.println("mismatch: " + count + "!=" + msg.getIntProperty("id"));
                        }
                    } catch (NumberFormatException ignore) {
                    }
                    if( count == 1 ) {
                        start = System.currentTimeMillis();
                    } else if( count % 1000 == 0 ) {
                        System.out.println(String.format("Received %d messages.", count));
                    }
                    count ++;
                }

            } else {
                System.out.println("Unexpected message type: " + msg.getClass());
            }
        }
    }

    private static String arg(String []args, int index, String defaultValue) {
        if( index < args.length )
            return args[index];
        else
            return defaultValue;
    }
}
