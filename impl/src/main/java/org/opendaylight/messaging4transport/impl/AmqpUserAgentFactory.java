/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.messaging4transport.impl;

import com.google.common.base.Preconditions;
import org.opendaylight.controller.md.sal.binding.api.*;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.dom.api.DOMNotificationService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.AmqpUserAgents;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.AmqpUserAgent;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AmqpUserAgentFactory implements DataTreeChangeListener<AmqpUserAgent>, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpUserAgentFactory.class);
    private static final InstanceIdentifier<AmqpUserAgent> AGENT_PATH = InstanceIdentifier.create(AmqpUserAgents.class)
            .child(AmqpUserAgent.class);
    private static final DataTreeIdentifier<AmqpUserAgent> AGENT_CONFIG_PATH = new DataTreeIdentifier<>(
            LogicalDatastoreType.CONFIGURATION, AGENT_PATH);

    private final ListenerRegistration<AmqpUserAgentFactory> amqpAgentsConfigReg;
    private final Map<InstanceIdentifier<AmqpUserAgent>, Messaging4TransportProviderImpl> agents = new HashMap<>();
    private final DOMNotificationService notificationService;
    private final DOMDataBrokerMessaging dataBroker;

    public AmqpUserAgentFactory(final DOMDataBrokerMessaging broker, final DOMNotificationService domNotification) {
        this.dataBroker = Preconditions.checkNotNull(broker, "broker");
        this.notificationService = Preconditions.checkNotNull(domNotification, "domNotification");
        amqpAgentsConfigReg = broker.registerDataTreeChangeListener(AGENT_CONFIG_PATH, this);
    }

    @Override
    public void onDataTreeChanged(final Collection<DataTreeModification<AmqpUserAgent>> changed) {
        for (final DataTreeModification<AmqpUserAgent> change : changed) {
            final InstanceIdentifier<AmqpUserAgent> agentKey = change.getRootPath().getRootIdentifier();
            final DataObjectModification<AmqpUserAgent> changeDiff = change.getRootNode();
            switch (changeDiff.getModificationType()) {
                case WRITE:
                    createOrReplace(agentKey, changeDiff.getDataAfter());
                    break;
                case DELETE:
                    removeAndClose(agentKey);
                default:
                    LOG.info("Unsupported change type {} for {}", changeDiff.getModificationType(), agentKey);
                    break;
            }
        }

    }

    private synchronized void removeAndClose(final InstanceIdentifier<AmqpUserAgent> agentKey) {
        LOG.info("Removing agent {}", agentKey);
        final Messaging4TransportProviderImpl removed = agents.remove(agentKey);
        if (removed != null) {
            removed.close();
        } else {
            LOG.warn("Agent {} was not removed.", agentKey);
        }
    }


    private synchronized void createOrReplace(final InstanceIdentifier<AmqpUserAgent> agentKey,
            final AmqpUserAgent configuration) {
        LOG.info("Going to create / replace agent {}", agentKey);
        final Messaging4TransportProviderImpl previous = agents.get(agentKey);
        if (previous != null) {
            LOG.info("Previous instance of {} found. Closing it.", agentKey);
            previous.close();
        }
        try {
            final Messaging4TransportProviderImpl newAgent =
                    Messaging4TransportProviderImpl.create(agentKey, configuration, dataBroker, notificationService);
            agents.put(agentKey, newAgent);
        } catch (final IllegalStateException e) {
            LOG.error("Unable to create agent {} with configuration {}", agentKey, configuration, e);
        }
    }

    @Override
    public synchronized void close() throws Exception {
        amqpAgentsConfigReg.close();
        for (final Entry<InstanceIdentifier<AmqpUserAgent>, Messaging4TransportProviderImpl> agent : agents.entrySet()) {
            agent.getValue().close();
        }
    }
}
