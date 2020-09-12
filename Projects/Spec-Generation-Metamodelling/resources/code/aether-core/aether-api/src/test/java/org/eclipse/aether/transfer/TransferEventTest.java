/*******************************************************************************
 * Copyright (c) 2010, 2012 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.transfer;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.junit.Test;

/**
 */
public class TransferEventTest
{

    private static TransferResource res = new TransferResource( "file://nil", "void", null, null );

    private static RepositorySystemSession session = new DefaultRepositorySystemSession();

    @Test
    public void testByteArrayConversion()
    {
        byte[] buffer = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int length = buffer.length - 2;
        int offset = 1;

        TransferEvent event = new TransferEvent.Builder( session, res ).setDataBuffer( buffer, offset, length ).build();

        ByteBuffer bb = event.getDataBuffer();
        byte[] dst = new byte[bb.remaining()];
        bb.get( dst );

        byte[] expected = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        assertArrayEquals( expected, dst );
    }

    @Test
    public void testRepeatableReadingOfDataBuffer()
    {
        byte[] data = { 0, 1, 2, 3, 4, 5, 6, 7 };
        ByteBuffer buffer = ByteBuffer.wrap( data );

        TransferEvent event = new TransferEvent.Builder( session, res ).setDataBuffer( buffer ).build();

        assertEquals( 8, event.getDataLength() );

        ByteBuffer eventBuffer = event.getDataBuffer();
        assertNotNull( eventBuffer );
        assertEquals( 8, eventBuffer.remaining() );

        byte[] eventData = new byte[8];
        eventBuffer.get( eventData );
        assertArrayEquals( data, eventData );
        assertEquals( 0, eventBuffer.remaining() );
        assertEquals( 8, event.getDataLength() );

        eventBuffer = event.getDataBuffer();
        assertNotNull( eventBuffer );
        assertEquals( 8, eventBuffer.remaining() );
        eventBuffer.get( eventData );
        assertArrayEquals( data, eventData );
    }

}
