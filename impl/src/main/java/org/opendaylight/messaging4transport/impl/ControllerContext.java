/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.impl;

import com.google.common.collect.ImmutableMap;
import org.opendaylight.controller.md.sal.common.impl.util.compat.DataNormalizer;
import org.opendaylight.controller.md.sal.dom.api.DOMMountPointService;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.RpcDefinition;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ControllerContext Singleton Class for Messaging4Transport. Code initially borrowed from NetConf.
 */
public class ControllerContext  implements SchemaContextListener {
    private final static Logger LOG = LoggerFactory.getLogger(ControllerContext.class);

    private final static ControllerContext INSTANCE = new ControllerContext();

    private final AtomicReference<Map<QName, RpcDefinition>> qnameToRpc =
            new AtomicReference<>(Collections.<QName, RpcDefinition>emptyMap());

    private volatile SchemaContext globalSchema;
    private volatile DOMMountPointService mountService;

    private DataNormalizer dataNormalizer;

    private ControllerContext() {
    }

    public static ControllerContext getInstance() {
        return ControllerContext.INSTANCE;
    }

    public void setGlobalSchema(final SchemaContext globalSchema) {
        this.globalSchema = globalSchema;
        dataNormalizer = new DataNormalizer(globalSchema);
    }

    @Override
    public void onGlobalContextUpdated(SchemaContext schemaContext) {
        if (schemaContext != null) {
            final Collection<RpcDefinition> defs = schemaContext.getOperations();
            final Map<QName, RpcDefinition> newMap = new HashMap<>(defs.size());

            for (final RpcDefinition operation : defs) {
                newMap.put(operation.getQName(), operation);
            }

            // FIXME: still not completely atomic
            qnameToRpc.set(ImmutableMap.copyOf(newMap));
            setGlobalSchema(schemaContext);
        }
    }

    public void setSchemas(final SchemaContext schemas) {
        onGlobalContextUpdated(schemas);
    }

    public void setMountService(final DOMMountPointService mountService) {
        this.mountService = mountService;
    }

}
