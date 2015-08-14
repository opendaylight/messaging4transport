/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.sample;

import org.opendaylight.messaging4transport.constants.Messaging4TransportConstants;
import org.opendaylight.messaging4transport.impl.Publisher;

import javax.jms.JMSException;

/**
 * Sample Publisher Execution
 */
public class PublisherMain {
    public static void main(String[] args) throws Exception {
        publish(args);
    }

    public static void publish(String[] args) throws JMSException, InterruptedException {
        String destination = arg(args, 0, Messaging4TransportConstants.AMQP_TOPIC_EVENT_DESTINATION);
        Publisher.publish(destination);
    }

    private static String arg(String[] args, int index, String defaultValue) {
        if (index < args.length)
            return args[index];
        else
            return defaultValue;
    }
}
