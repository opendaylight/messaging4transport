/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.amqp.user.agent.ReceiverKey;
import org.opendaylight.yangtools.concepts.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpReceiverContext implements AutoCloseable, Identifiable<ReceiverKey> {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpReceiverContext.class);

    private final ReceiverKey user;

    private AmqpReceiverContext(final ReceiverKey userName) {
        this.user = userName;
    }

    static AmqpReceiverContext create(final ReceiverKey userName) {
        return new AmqpReceiverContext(userName);
    }

    /**
     * Sends message using the AMQP Publisher
     * @param text message to be sent
     */
    void sendMessage(final String text) {
        LOG.debug("Sending notification to {} using Messaging4Transport", user);
        AmqpPublisher.publish(text);
    }

    @Override
    public void close() {
        LOG.info("Closing Messaging4Transport AMQP Connection.");
    }

    @Override
    public ReceiverKey getIdentifier() {
        return user;
    }
}
