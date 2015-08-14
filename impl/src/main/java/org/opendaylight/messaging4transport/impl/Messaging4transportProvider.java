/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.Messaging4transportService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Messaging4transportProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Messaging4transportProvider.class);
    private RpcRegistration<Messaging4transportService> messaging4transportService;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("Messaging4transportProvider Session Initiated");
        Publisher.publish();
        messaging4transportService = session.addRpcImplementation(Messaging4transportService.class,
                new Messaging4TransportImpl());

    }

    @Override
    public void close() throws Exception {
        LOG.info("HelloProvider Closed");
        if (messaging4transportService != null) {
            messaging4transportService.close();
        }
    }
}
