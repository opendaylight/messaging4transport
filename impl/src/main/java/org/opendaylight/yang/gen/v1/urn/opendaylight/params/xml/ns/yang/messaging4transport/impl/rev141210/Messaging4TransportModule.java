package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210;

import org.opendaylight.mdsal.dom.api.DOMNotificationService;
import org.opendaylight.messaging4transport.impl.AmqpUserAgentFactory;

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
        // Create an instance of our provider
//        Messaging4TransportProviderImpl instance = new Messaging4TransportProviderImpl();

//        final DOMNotificationService notifyService = getDomBrokerDependency()
//                .registerConsumer(new NoopDOMConsumer())
//                .getService(DOMNotificationService.class);   // todo - uncomment

//        return new AmqpUserAgentFactory(dataBroker, notifyService);
        return null; //todo







//
//        // Register it with the Broker
//        getDomBrokerDependency().registerProvider(instance);
//        return instance;
    }

}
