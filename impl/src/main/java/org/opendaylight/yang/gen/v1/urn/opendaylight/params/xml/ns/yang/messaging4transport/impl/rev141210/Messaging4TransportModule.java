package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210;

import org.opendaylight.messaging4transport.impl.Messaging4TransportProviderImpl;

public class Messaging4TransportModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210.AbstractMessaging4TransportModule {
    public Messaging4TransportModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public Messaging4TransportModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210.Messaging4TransportModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final Messaging4TransportProviderImpl messaging4TransportProvider = new Messaging4TransportProviderImpl();
        // Register it with the Broker
        getDomBrokerDependency().registerProvider(messaging4TransportProvider);
        return messaging4TransportProvider;
    }

}
