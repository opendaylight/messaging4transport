/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.messaging4transport.impl;

import com.google.common.base.Preconditions;
import org.opendaylight.controller.md.sal.dom.api.DOMDataChangeListener;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.mdsal.dom.api.*;
import org.opendaylight.controller.md.sal.dom.api.DOMNotificationService;
import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.AmqpUserAgent;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.tree.DataTreeCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AmqpUserAgentFactory implements DOMDataTreeChangeListener, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpUserAgentFactory.class);
    private static final YangInstanceIdentifier AGENT_PATH = YangInstanceIdentifier.create();
    private static final DOMDataTreeIdentifier AGENT_CONFIG_PATH =
            new DOMDataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, AGENT_PATH);

    private final ListenerRegistration<DOMDataChangeListener> amqpAgentsConfigReg;
    private final Map<YangInstanceIdentifier, Messaging4TransportProviderImpl> agents = new HashMap<>();
    private final DOMNotificationService notificationService;
    private final DOMDataBroker dataBroker;

    public AmqpUserAgentFactory(final DOMDataBroker broker, final DOMNotificationService domNotification) {
        this.dataBroker = Preconditions.checkNotNull(broker, "broker");
        this.notificationService = Preconditions.checkNotNull(domNotification, "domNotification");

        amqpAgentsConfigReg = null;
        //  broker.registerDataTreeChangeListener(AGENT_CONFIG_PATH, this);
        // todo - 1: registerDataChangeListener
    }


    private synchronized void removeAndClose(final YangInstanceIdentifier agentKey) {
        LOG.info("Removing agent {}", agentKey);
        final Messaging4TransportProviderImpl removed = agents.remove(agentKey);
        if (removed != null) {
            removed.close();
        } else {
            LOG.warn("Agent {} was not removed.", agentKey);
        }
    }


    private synchronized void createOrReplace(final YangInstanceIdentifier agentKey,
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
        for (final Entry<YangInstanceIdentifier, Messaging4TransportProviderImpl> agent : agents.entrySet()) {
            agent.getValue().close();
        }
    }

    @Override
    public void onDataTreeChanged(final Collection<DataTreeCandidate> dataTreeCandidates) {
        for (final DataTreeCandidate dataTreeCandidate : dataTreeCandidates) {
            final YangInstanceIdentifier agentKey = dataTreeCandidate.getRootPath();
            final DataObjectModification<AmqpUserAgent> changeDiff = null;
            // todo - remove null; and initialize
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
}
