/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Generates random message
 */
public class MessageGen {
    private static final Logger LOG = LoggerFactory.getLogger(MessageGen.class);

    /**
     * Generates random messages
     * @param session session
     * @param producer message producer
     * @throws JMSException if message transfer failed.
     */
    public static void sendMessages(Session session, MessageProducer producer) throws JMSException {
        int messages = 10000;
        for (int i = 1; i <= messages; i++) {
            TextMessage msg = session.createTextMessage("#:" + i);
            msg.setIntProperty("id", i);
            producer.send(msg);
            if ((i % 1000) == 0) {
                LOG.info(String.format("Sent %d messages", i));
            }
        }
    }
}
