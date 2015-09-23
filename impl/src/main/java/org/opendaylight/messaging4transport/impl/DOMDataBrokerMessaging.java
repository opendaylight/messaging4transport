/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.mdsal.common.api.*;
import org.opendaylight.mdsal.binding.api.*;

public interface DOMDataBrokerMessaging extends DataTreeChangeService {

    /**
     * {@inheritDoc}
     */
    @Override
    ListenerRegistration<DataChangeListener> registerDataChangeListener(LogicalDatastoreType store,
                                                                        InstanceIdentifier<?> path, DataChangeListener listener, AsyncDataBroker.DataChangeScope triggeringScope);

}
