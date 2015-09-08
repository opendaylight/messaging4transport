/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.dom.api.*;
import org.opendaylight.controller.sal.core.api.Broker.ProviderSession;
import org.opendaylight.controller.sal.core.api.Provider;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.amqp.user.agent.Receiver;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Collection;

public class Messaging4TransportProviderImpl implements Provider, DOMNotificationListener, AutoCloseable, DataTreeChangeListener<Receiver> {

    private static final Logger LOG = LoggerFactory.getLogger(Messaging4TransportProviderImpl.class);
    private ListenerRegistration<SchemaContextListener> listenerRegistration;

    @Override
    public void onSessionInitiated(final ProviderSession session) {
        String initiationSuccessful = "Messaging4TransportProviderImpl Session Initiated";
        LOG.info(initiationSuccessful);
        AmqpPublisher.publish(initiationSuccessful);

        final DOMDataBroker domDataBroker = session.getService(DOMDataBroker.class);
        final SchemaService schemaService = session.getService(SchemaService.class);
    }


    @Override
    public Collection<ProviderFunctionality> getProviderFunctionality() {
        return Collections.emptySet();
    }

    @Override
    public void onNotification(final DOMNotification domNotification) {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<Receiver>> dataTreeModifications) {

    }
}
