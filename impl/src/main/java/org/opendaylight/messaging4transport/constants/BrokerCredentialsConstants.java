/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.constants;

/**
 * Constants of Broker credentials
 */
public final class BrokerCredentialsConstants {

    private BrokerCredentialsConstants() {
        throw new AssertionError("Instantiating utility class BrokerCredentialsConstants.");
    }

    /**
     * Key for the ActiveMQ user in the key, value mapping.
     */
    public static final String ACTIVEMQ_USER_KEY = "ACTIVEMQ_USER";

    /**
     * Key for the ActiveMQ password instance in the key, value mapping.
     */
    public static final String ACTIVEMQ_PASSWORD_KEY = "ACTIVEMQ_PASSWORD";

    /**
     * Value (default) for the ActiveMQ user in the key, value mapping.
     */
    public static final String ACTIVEMQ_USER_VALUE = "admin";

    /**
     * Value (default) for the ActiveMQ password instance in the key, value mapping.
     */
    public static final String ACTIVEMQ_PASSWORD_VALUE = "password";

    /**
     * Value (default) for the ActiveMQ (karaf deployment) user in the key, value mapping.
     */
    public static final String ACTIVEMQ_KARAF_USER_VALUE = "karaf";

    /**
     * Value (default) for the ActiveMQ (karaf deployment) password in the key, value mapping.
     */
    public static final String ACTIVEMQ_KARAF_PASSWORD_VALUE = "karaf";
}
