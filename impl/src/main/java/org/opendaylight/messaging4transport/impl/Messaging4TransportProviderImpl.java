/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.controller.md.sal.dom.api.DOMMountPointService;
import org.opendaylight.controller.md.sal.dom.api.DOMRpcService;
import org.opendaylight.controller.sal.core.api.Broker.ProviderSession;
import org.opendaylight.controller.sal.core.api.Provider;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.Messaging4transportService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Collection;

public class Messaging4TransportProviderImpl implements Provider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Messaging4TransportProviderImpl.class);
    private RpcRegistration<Messaging4transportService> messaging4transportService;
    private ListenerRegistration<SchemaContextListener> listenerRegistration;

    @Override
    public void onSessionInitiated(final ProviderSession session) {
        LOG.info("Messaging4TransportProviderImpl Session Initiated");
        Publisher.publish();

        final DOMDataBroker domDataBroker = session.getService(DOMDataBroker.class);

//        BrokerFacade.getInstance().setContext(session);
//        BrokerFacade.getInstance().setDomDataBroker( domDataBroker);
        final SchemaService schemaService = session.getService(SchemaService.class);
//        listenerRegistration = schemaService.registerSchemaContextListener(ControllerContext.getInstance());
//        BrokerFacade.getInstance().setRpcService(session.getService(DOMRpcService.class));


//        ControllerContext.getInstance().setSchemas(schemaService.getGlobalContext());
//        ControllerContext.getInstance().setMountService(session.getService(DOMMountPointService.class));
    }

    @Override
    public void close() throws Exception {
        LOG.info("Messaging4TransportProviderImpl Closed");
        if (messaging4transportService != null) {
            messaging4transportService.close();
        }
    }

    @Override
    public Collection<ProviderFunctionality> getProviderFunctionality() {
        return Collections.emptySet();
    }

}
