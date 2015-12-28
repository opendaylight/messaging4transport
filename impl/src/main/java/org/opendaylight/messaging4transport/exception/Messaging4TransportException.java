/*
 * Copyright (c) 2015 Pradeeban Kathiravelu and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.messaging4transport.exception;

/**
 * Run time exceptions specific to the Messaging4Transport Project.
 */
public class Messaging4TransportException extends RuntimeException {
    public Messaging4TransportException() {
        super();
    }

    public Messaging4TransportException(String message) {
        super(message);
    }

    public Messaging4TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public Messaging4TransportException(Throwable cause) {
        super(cause);
    }
}
