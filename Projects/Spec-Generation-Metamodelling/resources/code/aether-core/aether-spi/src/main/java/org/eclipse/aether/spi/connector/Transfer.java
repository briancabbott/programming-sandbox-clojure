/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.spi.connector;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferListener;

/**
 * An artifact/metadata transfer.
 * 
 * @noextend This class is not intended to be extended by clients.
 */
public abstract class Transfer
{

    private TransferListener listener;

    private RequestTrace trace;

    Transfer()
    {
        // hide from public
    }

    /**
     * Gets the exception that occurred during the transfer (if any).
     * 
     * @return The exception or {@code null} if the transfer was successful.
     */
    public abstract Exception getException();

    /**
     * Gets the listener that is to be notified during the transfer.
     * 
     * @return The transfer listener or {@code null} if none.
     */
    public TransferListener getListener()
    {
        return listener;
    }

    /**
     * Sets the listener that is to be notified during the transfer.
     * 
     * @param listener The transfer listener to notify, may be {@code null} if none.
     * @return This transfer for chaining, never {@code null}.
     */
    Transfer setListener( TransferListener listener )
    {
        this.listener = listener;
        return this;
    }

    /**
     * Gets the trace information that describes the higher level request/operation in which this transfer is issued.
     * 
     * @return The trace information about the higher level operation or {@code null} if none.
     */
    public RequestTrace getTrace()
    {
        return trace;
    }

    /**
     * Sets the trace information that describes the higher level request/operation in which this transfer is issued.
     * 
     * @param trace The trace information about the higher level operation, may be {@code null}.
     * @return This transfer for chaining, never {@code null}.
     */
    Transfer setTrace( RequestTrace trace )
    {
        this.trace = trace;
        return this;
    }

}
