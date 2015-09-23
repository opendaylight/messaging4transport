/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeChangeService;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;

import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;


public interface DOMDataBrokerMessaging extends DataTreeChangeService {

    ListenerRegistration<DataChangeListener> registerDataChangeListener(LogicalDatastoreType store,
                                                                        InstanceIdentifier<?> path, DataChangeListener listener, org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope triggeringScope);

}
