/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.Messaging4transportService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.MessagingTransportInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.MessagingTransportOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.messaging4transport.rev150105.MessagingTransportOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import java.util.concurrent.Future;

public class Messaging4TransportImpl implements Messaging4transportService {
    @Override
    public Future<RpcResult<MessagingTransportOutput>> messagingTransport(MessagingTransportInput input) {
        MessagingTransportOutputBuilder messagingTransportOutputBuilder = new MessagingTransportOutputBuilder();
        messagingTransportOutputBuilder.setMessage("Initial Message: " + input.getName());
        return RpcResultBuilder.success(messagingTransportOutputBuilder.build()).buildFuture();
    }
}
