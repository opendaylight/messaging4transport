/*
 * Copyright (c) 2015 Pradeeban Kathiravelu  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210;

import org.opendaylight.messaging4transport.impl.Messaging4transportProvider;

public class Messaging4transportModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210.AbstractMessaging4transportModule {
    public Messaging4transportModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public Messaging4transportModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210.Messaging4transportModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        Messaging4transportProvider provider = new Messaging4transportProvider();
        getBrokerDependency().registerProvider(provider);
        return provider;
    }

}
