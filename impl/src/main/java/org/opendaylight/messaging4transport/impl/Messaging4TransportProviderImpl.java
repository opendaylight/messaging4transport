/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.dom.api.*;
import org.opendaylight.controller.sal.core.api.Broker.ProviderSession;
import org.opendaylight.controller.sal.core.api.Provider;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.opendaylight.messaging4transport.exception.Messaging4TransportException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.AmqpUserAgent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.amqp.user.agent.Receiver;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.amqp.user.agents.amqp.user.agent.ReceiverKey;

import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.AnyXmlNode;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public final class Messaging4TransportProviderImpl implements Provider, DOMNotificationListener, AutoCloseable,
        DataTreeChangeListener<Receiver> {

    private static final Logger LOG = LoggerFactory.getLogger(Messaging4TransportProviderImpl.class);
    private ListenerRegistration<SchemaContextListener> listenerRegistration;

    private static QName qName;


    private ListenerRegistration<Messaging4TransportProviderImpl> notificationReg;
    private ListenerRegistration<Messaging4TransportProviderImpl> configurationReg;

    private Map<ReceiverKey, AmqpReceiverContext> receivers = new HashMap<>();
    private YangInstanceIdentifier identifier;

    private static final YangInstanceIdentifier.NodeIdentifier EVENT_SOURCE_ARG =
            new YangInstanceIdentifier.NodeIdentifier(QName.create(qName, "node-id"));
    private static final YangInstanceIdentifier.NodeIdentifier PAYLOAD_ARG =
            new YangInstanceIdentifier.NodeIdentifier(QName.create(qName, "payload"));

    private static final SchemaPath TOPIC_NOTIFICATION_PATH = SchemaPath.create(true, qName);

    private Messaging4TransportProviderImpl(final YangInstanceIdentifier id, final AmqpUserAgent userAgent,
                                            final DOMDataBroker dataBroker,
                                            final DOMNotificationService notificationService) {
        identifier = id;

        try {

            Session session = AmqpPublisher.getAmqpSession(userAgent.getHost(), userAgent.getPort(),
                    userAgent.getUsername(), userAgent.getPassword());

            MessageProducer producer = session.createProducer(AmqpConfig.getJmsDestination(
                    AmqpConfig.getDestination()));
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        } catch (final Exception e) {
            throw new IllegalStateException("Unable to connect to the AMQP server", e);
        }
        notificationReg = notificationService.registerNotificationListener(this, TOPIC_NOTIFICATION_PATH);

        final YangInstanceIdentifier receiverPath = identifier;

        final DOMDataTreeIdentifier receiverConfigPath =
                new DOMDataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, receiverPath);
//        configurationReg = dataBroker.registerDataChangeListener(); //receiverConfigPath, this);
// todo: registerDataTreeChangeListener
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
    static Messaging4TransportProviderImpl create(final YangInstanceIdentifier id,
                                                  final AmqpUserAgent configuration, final DOMDataBroker dataBroker,
                                                  final DOMNotificationService notificationService) {
        return new Messaging4TransportProviderImpl(id,configuration, dataBroker, notificationService);
    }


    @Override
    public void onSessionInitiated(final ProviderSession session) {
        String initiationSuccessful = "Messaging4TransportProviderImpl Session Initiated";
        LOG.info(initiationSuccessful);
        AmqpPublisher.publish(initiationSuccessful);

        final DOMDataBroker domDataBroker = session.getService(DOMDataBroker.class);
        final SchemaService schemaService = session.getService(SchemaService.class);

        listenerRegistration = schemaService.registerSchemaContextListener(ControllerContext.getInstance());

        ControllerContext.getInstance().setSchemas(schemaService.getGlobalContext());
        ControllerContext.getInstance().setMountService(session.getService(DOMMountPointService.class));
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
            throw new Messaging4TransportException(e);
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

    private static ReceiverKey getReceiverKey(final DataTreeIdentifier<Receiver> rootPath) {
        return rootPath.getRootIdentifier().firstKeyOf(Receiver.class, ReceiverKey.class);
    }


    private synchronized void createOrModifyReceiver(final ReceiverKey receiverKey, final Receiver dataAfter) {
        final AmqpReceiverContext preexisting = receivers.get(receiverKey);
        if (preexisting == null) {
            final AmqpReceiverContext receiver = AmqpReceiverContext.create(receiverKey);
            LOG.info("Created publishing context for receiver {}", receiverKey);
            receivers.put(receiverKey, receiver);
        }
    }

    private synchronized void removeReceiver(final ReceiverKey receiverKey) {
        final AmqpReceiverContext receiver = receivers.remove(receiverKey);
        if (receiver != null) {
            LOG.debug("Removing receiver {}.", receiverKey.getMessageId());
            receiver.close();
        }
    }

    @Override
    public void onDataTreeChanged(final Collection<DataTreeModification<Receiver>> changed) {

        for (final DataTreeModification<Receiver> change : changed) {
            final ReceiverKey receiverKey = getReceiverKey(change.getRootPath());
            final DataObjectModification<Receiver> rootChange = change.getRootNode();
            switch (rootChange.getModificationType()) {
                case WRITE:
                case SUBTREE_MODIFIED:
                    createOrModifyReceiver(receiverKey, rootChange.getDataAfter());
                    break;
                case DELETE:
                    removeReceiver(receiverKey);
                default:
                    break;
            }
        }
    }
}
