/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.apache.qpid.amqp_1_0.jms.impl.QueueImpl;
import org.apache.qpid.amqp_1_0.jms.impl.TopicImpl;
import org.opendaylight.messaging4transport.constants.BrokerCredentialsConstants;
import org.opendaylight.messaging4transport.constants.Messaging4TransportConstants;

import javax.jms.Destination;

/**
 * Configuration class for the AMQP broker
 */
public final class AmqpConfig {

    private AmqpConfig() {
        throw new AssertionError("Instantiating utility class AmqpConfig.");
    }

    /**
     * Gets the AMQP broker user name
     * @return username
     */
    public static String getUser() {
        return Messaging4TransportConstants.IS_KARAF_BASED ? env(BrokerCredentialsConstants.ACTIVEMQ_USER_KEY,
                BrokerCredentialsConstants.ACTIVEMQ_KARAF_USER_VALUE) :
                env(BrokerCredentialsConstants.ACTIVEMQ_USER_KEY, BrokerCredentialsConstants.ACTIVEMQ_USER_VALUE);
    }

    /**
     * Gets the AMQP broker password
     * @return password
     */
    public static String getPassword() {
        return Messaging4TransportConstants.IS_KARAF_BASED ? env(BrokerCredentialsConstants.
                ACTIVEMQ_PASSWORD_KEY, BrokerCredentialsConstants.ACTIVEMQ_KARAF_PASSWORD_VALUE) :
                env(BrokerCredentialsConstants.ACTIVEMQ_PASSWORD_KEY, BrokerCredentialsConstants.
                        ACTIVEMQ_PASSWORD_VALUE);
    }

    /**
     * Gets the AMQP broker deployment host
     * @return host
     */
    public static String getHost() {
        return env(Messaging4TransportConstants.ACTIVEMQ_HOST_KEY, Messaging4TransportConstants.ACTIVEMQ_HOST_VALUE);
    }

    /**
     * Gets the AMQP broker deployment port
     * @return port
     */
    public static int getPort() {
        return Integer.parseInt(env(Messaging4TransportConstants.ACTIVEMQ_PORT_KEY,
                Messaging4TransportConstants.ACTIVEMQ_PORT_VALUE));
    }

    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if (rc == null) {
            return defaultValue;
        }
        return rc;
    }

    /**
     * Gets the Destination of the messages
     * @param destination the destination string
     * @return the Destination object
     */
    public static Destination getDestination(String destination) {
        Destination dest;
        if (destination.startsWith(Messaging4TransportConstants.AMQP_TOPIC_PREFIX)) {
            dest = new TopicImpl(destination);
        } else {
            dest = new QueueImpl(destination);
        }
        return dest;
    }
}
