/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.dom.api.*;
import org.opendaylight.controller.sal.core.api.Broker.ProviderSession;
import org.opendaylight.controller.sal.core.api.Provider;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.opendaylight.messaging4transport.constants.Messaging4TransportConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.AmqpUserAgent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.amqp.user.agent.Receiver;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.amqp.user.agent.ReceiverKey;

import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Messaging4Transport Provider.
 */
public class Messaging4TransportProviderImpl implements Provider, DOMNotificationListener, AutoCloseable,
        DataTreeChangeListener<Receiver> {

    private static final Logger LOG = LoggerFactory.getLogger(Messaging4TransportProviderImpl.class);
    private ListenerRegistration<SchemaContextListener> listenerRegistration;

    private static QName QNAME;


    private final ListenerRegistration<Messaging4TransportProviderImpl> notificationReg;
    private final ListenerRegistration<Messaging4TransportProviderImpl> configurationReg;

    private final Map<ReceiverKey, AmqpReceiverContext> receivers = new HashMap<>();
    private final InstanceIdentifier<AmqpUserAgent> identifier;

    private static final YangInstanceIdentifier.NodeIdentifier EVENT_SOURCE_ARG =
            new YangInstanceIdentifier.NodeIdentifier(QName.create(QNAME, "node-id"));
    private static final YangInstanceIdentifier.NodeIdentifier PAYLOAD_ARG =
            new YangInstanceIdentifier.NodeIdentifier(QName.create(QNAME, "payload"));

    private static final SchemaPath TOPIC_NOTIFICATION_PATH = SchemaPath.create(true, QNAME);


    private Messaging4TransportProviderImpl(final InstanceIdentifier<AmqpUserAgent> id, final AmqpUserAgent userAgent,
                                            final DOMDataBrokerMessaging dataBroker,
                                            final DOMNotificationService notificationService) {
        identifier = id;

        try {

            Session session = AmqpPublisher.getAmqpSession(userAgent.getHost(), userAgent.getPort(),
                    userAgent.getUsername(), userAgent.getPassword());

            MessageProducer producer = session.createProducer(AmqpConfig.getDestination(Messaging4TransportConstants.
                    AMQP_TOPIC_EVENT_DESTINATION));
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

//            sendMessages(session, producer, msg);

        } catch (final Exception e) {
            throw new IllegalStateException("Unable to connect to the AMQP server", e);
        }
        notificationReg = notificationService.registerNotificationListener(this, TOPIC_NOTIFICATION_PATH);

        final InstanceIdentifier<Receiver> receiverPath = identifier.child(Receiver.class);
        final DataTreeIdentifier<Receiver> receiverConfigPath =
                new DataTreeIdentifier<>(LogicalDatastoreType.CONFIGURATION, receiverPath);
        configurationReg = dataBroker.registerDataTreeChangeListener(receiverConfigPath, this);
        LOG.info("AMQP user agent initialized. id: {}", id);
    }

    /**
     * Creates an instance of Messaging4TransportProviderImpl by calling the private constructor.
     * @param id identifier
     * @param configuration configuration
     * @param dataBroker the broker
     * @param notificationService the notification service
     * @return the instance of Messaging4TransportProviderImpl
     */
    static Messaging4TransportProviderImpl create(final InstanceIdentifier<AmqpUserAgent> id,
                                                         final AmqpUserAgent configuration, final DOMDataBrokerMessaging dataBroker,
                                    final DOMNotificationService notificationService) {
        return new Messaging4TransportProviderImpl(id,configuration, dataBroker, notificationService);
    }


    @Override
    public void onSessionInitiated(final ProviderSession session) {
        String initiationSuccessful = "Messaging4TransportProviderImpl Session Initiated";
        LOG.info(initiationSuccessful);
        AmqpPublisher.publish(initiationSuccessful);

        final DOMDataBroker DOMDataBrokerMessaging = session.getService(DOMDataBroker.class);
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
    public synchronized void close() {
        configurationReg.close();
        notificationReg.close();
        for (final Map.Entry<ReceiverKey, AmqpReceiverContext> receiver : receivers.entrySet()) {
            receiver.getValue().close();
        }
    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<Receiver>> dataTreeModifications) {

    }
}
