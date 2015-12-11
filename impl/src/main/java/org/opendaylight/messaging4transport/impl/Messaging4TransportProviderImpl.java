/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.mdsal.dom.api.*;
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
import org.opendaylight.yangtools.yang.data.api.schema.AnyXmlNode;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
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


    private ListenerRegistration<Messaging4TransportProviderImpl> notificationReg;
    private ListenerRegistration<Messaging4TransportProviderImpl> configurationReg;

    private Map<ReceiverKey, AmqpReceiverContext> receivers = new HashMap<>();
    private InstanceIdentifier<AmqpUserAgent> identifier;

    private static final YangInstanceIdentifier.NodeIdentifier EVENT_SOURCE_ARG =
            new YangInstanceIdentifier.NodeIdentifier(QName.create(QNAME, "node-id"));
    private static final YangInstanceIdentifier.NodeIdentifier PAYLOAD_ARG =
            new YangInstanceIdentifier.NodeIdentifier(QName.create(QNAME, "payload"));

//    private static final SchemaPath TOPIC_NOTIFICATION_PATH = SchemaPath.create(true, QNAME);


    public Messaging4TransportProviderImpl() {
        LOG.info("Messaging4Transport Provider Initialized");
    }

    private Messaging4TransportProviderImpl(final InstanceIdentifier<AmqpUserAgent> id, final AmqpUserAgent userAgent,
                                            final DOMDataBroker dataBroker,
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
//        notificationReg = notificationService.registerNotificationListener(this, TOPIC_NOTIFICATION_PATH);

        final InstanceIdentifier<Receiver> receiverPath = identifier.child(Receiver.class);

//        final DOMDataTreeIdentifier receiverConfigPath =
//                new DOMDataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, receiverPath); // todo - uncomment
        configurationReg = null; //dataBroker.registerDataTreeChangeListener(receiverConfigPath, this);  
// todo - 2: registerDataTreeChangeListener
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
                                                  final AmqpUserAgent configuration, final DOMDataBroker dataBroker,
                                                  final DOMNotificationService notificationService) {
        return new Messaging4TransportProviderImpl(id,configuration, dataBroker, notificationService);
    }


    @Override
    public void onSessionInitiated(final ProviderSession session) {
        String initiationSuccessful = "Messaging4TransportProviderImpl Session Initiated";
        LOG.info(initiationSuccessful);
        AmqpPublisher.publish(initiationSuccessful);

//        final DOMDataBroker DOMDataBroker = session.getService(DOMDataBroker.class);  //todo
        final SchemaService schemaService = session.getService(SchemaService.class);
    }


    @Override
    public Collection<ProviderFunctionality> getProviderFunctionality() {
        return Collections.emptySet();
    }

    @Override
    public void onNotification(final DOMNotification notification) {
        final String nodeName = notification.getBody().getChild(EVENT_SOURCE_ARG).get().getValue().toString();

        try {
            final AnyXmlNode encapData = (AnyXmlNode) notification.getBody().getChild(PAYLOAD_ARG).get();
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.transform(encapData.getValue(), result);
            writer.flush();
            final String message = writer.toString();


            synchronized (this) {
                for (final Map.Entry<ReceiverKey, AmqpReceiverContext> receiver : receivers.entrySet()) {
                    receiver.getValue().sendMessage(message);
                }
            }
            LOG.info("Published notification for Agent {}: \nNotification {} ", nodeName, message);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

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
