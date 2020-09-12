/*******************************************************************************
 * Copyright (c) 2010, 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.spi.connector;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferListener;

/**
 * A download of an artifact from a remote repository. A repository connector processing this download has to use
 * {@link #setException(ArtifactTransferException)} and {@link #setSupportedContexts(Collection)} (if applicable) to
 * report the results of the transfer.
 */
public final class ArtifactDownload
    extends ArtifactTransfer
{

    private boolean existenceCheck;

    private String checksumPolicy = "";

    private String context = "";

    private Collection<String> contexts;

    private List<RemoteRepository> repositories = Collections.emptyList();

    /**
     * Creates a new uninitialized download.
     */
    public ArtifactDownload()
    {
        // enables default constructor
    }

    /**
     * Creates a new download with the specified properties.
     * 
     * @param artifact The artifact to download, may be {@code null}.
     * @param context The context in which this download is performed, may be {@code null}.
     * @param file The local file to download the artifact to, may be {@code null}.
     * @param checksumPolicy The checksum policy, may be {@code null}.
     */
    public ArtifactDownload( Artifact artifact, String context, File file, String checksumPolicy )
    {
        setArtifact( artifact );
        setRequestContext( context );
        setFile( file );
        setChecksumPolicy( checksumPolicy );
    }

    @Override
    public ArtifactDownload setArtifact( Artifact artifact )
    {
        super.setArtifact( artifact );
        return this;
    }

    /**
     * {@inheritDoc} <em>Note:</em> In case of {@link #isExistenceCheck()}, this method may return {@code null}.
     */
    @Override
    public File getFile()
    {
        return super.getFile();
    }

    @Override
    public ArtifactDownload setFile( File file )
    {
        super.setFile( file );
        return this;
    }

    /**
     * Indicates whether this transfer shall only verify the existence of the artifact in the remote repository rather
     * than actually downloading the file. Just like with an actual transfer, a connector is expected to signal the
     * non-existence of the artifact by associating an {@link org.eclipse.aether.transfer.ArtifactNotFoundException
     * ArtifactNotFoundException} with this download. <em>Note:</em> If an existence check is requested,
     * {@link #getFile()} may be {@code null}, i.e. the connector must not try to access the local file.
     * 
     * @return {@code true} if only the artifact existence shall be verified, {@code false} to actually download the
     *         artifact.
     */
    public boolean isExistenceCheck()
    {
        return existenceCheck;
    }

    /**
     * Controls whether this transfer shall only verify the existence of the artifact in the remote repository rather
     * than actually downloading the file.
     * 
     * @param existenceCheck {@code true} if only the artifact existence shall be verified, {@code false} to actually
     *            download the artifact.
     * @return This transfer for chaining, never {@code null}.
     */
    public ArtifactDownload setExistenceCheck( boolean existenceCheck )
    {
        this.existenceCheck = existenceCheck;
        return this;
    }

    /**
     * Gets the checksum policy for this transfer.
     * 
     * @return The checksum policy, never {@code null}.
     */
    public String getChecksumPolicy()
    {
        return checksumPolicy;
    }

    /**
     * Sets the checksum policy for this transfer.
     * 
     * @param checksumPolicy The checksum policy, may be {@code null}.
     * @return This transfer for chaining, never {@code null}.
     */
    public ArtifactDownload setChecksumPolicy( String checksumPolicy )
    {
        this.checksumPolicy = ( checksumPolicy != null ) ? checksumPolicy : "";
        return this;
    }

    /**
     * Gets the context of this transfer.
     * 
     * @return The context id, never {@code null}.
     */
    public String getRequestContext()
    {
        return context;
    }

    /**
     * Sets the context of this transfer.
     * 
     * @param context The context id, may be {@code null}.
     * @return This transfer for chaining, never {@code null}.
     */
    public ArtifactDownload setRequestContext( String context )
    {
        this.context = ( context != null ) ? context : "";
        return this;
    }

    /**
     * Gets the set of request contexts in which the artifact is generally available. Repository managers can indicate
     * that an artifact is available in more than the requested context to avoid future remote trips for the same
     * artifact in a different context.
     * 
     * @return The set of requests context in which the artifact is available, never {@code null}.
     */
    public Collection<String> getSupportedContexts()
    {
        return ( contexts != null ) ? contexts : Collections.singleton( context );
    }

    /**
     * Sets the set of request contexts in which the artifact is generally available. Repository managers can indicate
     * that an artifact is available in more than the requested context to avoid future remote trips for the same
     * artifact in a different context. The set of supported contexts defaults to the original request context if not
     * overridden by the repository connector.
     * 
     * @param contexts The set of requests context in which the artifact is available, may be {@code null}.
     * @return This transfer for chaining, never {@code null}.
     */
    public ArtifactDownload setSupportedContexts( Collection<String> contexts )
    {
        if ( contexts == null || contexts.isEmpty() )
        {
            this.contexts = Collections.singleton( context );
        }
        else
        {
            this.contexts = contexts;
        }
        return this;
    }

    /**
     * Gets the remote repositories that are being aggregated by the physically contacted remote repository (i.e. a
     * repository manager).
     * 
     * @return The remote repositories being aggregated, never {@code null}.
     */
    public List<RemoteRepository> getRepositories()
    {
        return repositories;
    }

    /**
     * Sets the remote repositories that are being aggregated by the physically contacted remote repository (i.e. a
     * repository manager).
     * 
     * @param repositories The remote repositories being aggregated, may be {@code null}.
     * @return This transfer for chaining, never {@code null}.
     */
    public ArtifactDownload setRepositories( List<RemoteRepository> repositories )
    {
        if ( repositories == null )
        {
            this.repositories = Collections.emptyList();
        }
        else
        {
            this.repositories = repositories;
        }
        return this;
    }

    @Override
    public ArtifactDownload setException( ArtifactTransferException exception )
    {
        super.setException( exception );
        return this;
    }

    @Override
    public ArtifactDownload setListener( TransferListener listener )
    {
        super.setListener( listener );
        return this;
    }

    @Override
    public ArtifactDownload setTrace( RequestTrace trace )
    {
        super.setTrace( trace );
        return this;
    }

    @Override
    public String toString()
    {
        return getArtifact() + " - " + ( isExistenceCheck() ? "?" : "" ) + getFile();
    }

}
