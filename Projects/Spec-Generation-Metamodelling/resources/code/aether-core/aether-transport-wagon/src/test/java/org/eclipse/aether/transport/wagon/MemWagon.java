/*******************************************************************************
 * Copyright (c) 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.transport.wagon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.resource.Resource;

/**
 */
public class MemWagon
    extends AbstractWagon
    implements Configurable
{

    private Map<String, String> fs;

    private Properties headers;

    private Object config;

    public void setConfiguration( Object config )
    {
        this.config = config;
    }

    public Object getConfiguration()
    {
        return config;
    }

    public void setHttpHeaders( Properties httpHeaders )
    {
        headers = httpHeaders;
    }

    @Override
    protected void openConnectionInternal()
        throws ConnectionException, AuthenticationException
    {
        fs =
            MemWagonUtils.openConnection( this, getAuthenticationInfo(),
                                          getProxyInfo( "mem", getRepository().getHost() ), headers );
    }

    @Override
    protected void closeConnection()
        throws ConnectionException
    {
        fs = null;
    }

    private String getData( String resource )
    {
        return fs.get( URI.create( resource ).getSchemeSpecificPart() );
    }

    @Override
    public boolean resourceExists( String resourceName )
        throws TransferFailedException, AuthorizationException
    {
        String data = getData( resourceName );
        return data != null;
    }

    public void get( String resourceName, File destination )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        getIfNewer( resourceName, destination, 0 );
    }

    public boolean getIfNewer( String resourceName, File destination, long timestamp )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        Resource resource = new Resource( resourceName );
        fireGetInitiated( resource, destination );
        resource.setLastModified( timestamp );
        getTransfer( resource, destination, getInputStream( resource ) );
        return true;
    }

    protected InputStream getInputStream( Resource resource )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        InputData inputData = new InputData();
        inputData.setResource( resource );
        try
        {
            fillInputData( inputData );
        }
        catch ( TransferFailedException e )
        {
            fireTransferError( resource, e, TransferEvent.REQUEST_GET );
            cleanupGetTransfer( resource );
            throw e;
        }
        catch ( ResourceDoesNotExistException e )
        {
            fireTransferError( resource, e, TransferEvent.REQUEST_GET );
            cleanupGetTransfer( resource );
            throw e;
        }
        catch ( AuthorizationException e )
        {
            fireTransferError( resource, e, TransferEvent.REQUEST_GET );
            cleanupGetTransfer( resource );
            throw e;
        }
        finally
        {
            if ( inputData.getInputStream() == null )
            {
                cleanupGetTransfer( resource );
            }
        }
        return inputData.getInputStream();
    }

    protected void fillInputData( InputData inputData )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        String data = getData( inputData.getResource().getName() );
        if ( data == null )
        {
            throw new ResourceDoesNotExistException( "Missing resource: " + inputData.getResource().getName() );
        }
        byte[] bytes;
        try
        {
            bytes = data.getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new TransferFailedException( e.getMessage(), e );
        }
        inputData.getResource().setContentLength( bytes.length );
        inputData.setInputStream( new ByteArrayInputStream( bytes ) );
    }

    public void put( File source, String resourceName )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        Resource resource = new Resource( resourceName );
        firePutInitiated( resource, source );
        resource.setContentLength( source.length() );
        resource.setLastModified( source.lastModified() );
        OutputStream os = getOutputStream( resource );
        putTransfer( resource, source, os, true );
    }

    protected OutputStream getOutputStream( Resource resource )
        throws TransferFailedException
    {
        OutputData outputData = new OutputData();
        outputData.setResource( resource );
        try
        {
            fillOutputData( outputData );
        }
        catch ( TransferFailedException e )
        {
            fireTransferError( resource, e, TransferEvent.REQUEST_PUT );
            throw e;
        }
        finally
        {
            if ( outputData.getOutputStream() == null )
            {
                cleanupPutTransfer( resource );
            }
        }

        return outputData.getOutputStream();
    }

    protected void fillOutputData( OutputData outputData )
        throws TransferFailedException
    {
        outputData.setOutputStream( new ByteArrayOutputStream() );
    }

    @Override
    protected void finishPutTransfer( Resource resource, InputStream input, OutputStream output )
        throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException
    {
        String data;
        try
        {
            data = ( (ByteArrayOutputStream) output ).toString( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new TransferFailedException( e.getMessage(), e );
        }
        fs.put( URI.create( resource.getName() ).getSchemeSpecificPart(), data );
    }

}
