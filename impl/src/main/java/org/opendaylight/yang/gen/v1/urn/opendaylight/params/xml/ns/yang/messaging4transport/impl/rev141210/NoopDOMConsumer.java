package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.impl.rev141210;

import org.opendaylight.controller.sal.core.api.Broker.ConsumerSession;
import org.opendaylight.controller.sal.core.api.Consumer;

import java.util.Collection;
import java.util.Collections;

public class NoopDOMConsumer implements Consumer {

    @Override
    public void onSessionInitiated(final ConsumerSession session) {
        // NOOP
    }

    @Override
    public Collection<ConsumerFunctionality> getConsumerFunctionality() {
        return Collections.emptySet();
    }

}
