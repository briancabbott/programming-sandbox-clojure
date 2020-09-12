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
package org.eclipse.aether.internal.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.LocalArtifactRegistration;
import org.eclipse.aether.repository.LocalArtifactRequest;
import org.eclipse.aether.repository.LocalArtifactResult;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.log.Logger;
import org.eclipse.aether.util.ConfigUtils;

/**
 * These are implementation details for enhanced local repository manager, subject to change without prior notice.
 * Repositories from which a cached artifact was resolved are tracked in a properties file named
 * <code>_remote.repositories</code>, with content key as filename&gt;repo_id and value as empty string. If a file has
 * been installed in the repository, but not downloaded from a remote repository, it is tracked as empty repository id
 * and always resolved. For example:
 * 
 * <pre>
 * artifact-1.0.pom>=
 * artifact-1.0.jar>=
 * artifact-1.0.pom>central=
 * artifact-1.0.jar>central=
 * artifact-1.0.zip>central=
 * artifact-1.0-classifier.zip>central=
 * artifact-1.0.pom>my_repo_id=
 * </pre>
 * 
 * @see EnhancedLocalRepositoryManagerFactory
 */
class EnhancedLocalRepositoryManager
    extends SimpleLocalRepositoryManager
{

    private static final String LOCAL_REPO_ID = "";

    private final String trackingFilename;

    private final TrackingFileManager trackingFileManager;

    public EnhancedLocalRepositoryManager( File basedir, RepositorySystemSession session )
    {
        super( basedir, "enhanced" );
        String filename = ConfigUtils.getString( session, "", "aether.enhancedLocalRepository.trackingFilename" );
        if ( filename.length() <= 0 || filename.contains( "/" ) || filename.contains( "\\" )
            || filename.contains( ".." ) )
        {
            filename = "_remote.repositories";
        }
        trackingFilename = filename;
        trackingFileManager = new TrackingFileManager();
    }

    @Override
    public EnhancedLocalRepositoryManager setLogger( Logger logger )
    {
        super.setLogger( logger );
        trackingFileManager.setLogger( logger );
        return this;
    }

    @Override
    public LocalArtifactResult find( RepositorySystemSession session, LocalArtifactRequest request )
    {
        String path = getPathForArtifact( request.getArtifact(), false );
        File file = new File( getRepository().getBasedir(), path );

        LocalArtifactResult result = new LocalArtifactResult( request );

        if ( file.isFile() )
        {
            result.setFile( file );

            Properties props = readRepos( file );

            if ( props.get( getKey( file, LOCAL_REPO_ID ) ) != null )
            {
                // artifact installed into the local repo is always accepted
                result.setAvailable( true );
            }
            else
            {
                String context = request.getContext();
                for ( RemoteRepository repository : request.getRepositories() )
                {
                    if ( props.get( getKey( file, getRepositoryKey( repository, context ) ) ) != null )
                    {
                        // artifact downloaded from remote repository is accepted only downloaded from request
                        // repositories
                        result.setAvailable( true );
                        result.setRepository( repository );
                        break;
                    }
                }
                if ( !result.isAvailable() && !isTracked( props, file ) )
                {
                    /*
                     * NOTE: The artifact is present but not tracked at all, for inter-op with simple local repo, assume
                     * the artifact was locally installed.
                     */
                    result.setAvailable( true );
                }
            }
        }

        return result;
    }

    @Override
    public void add( RepositorySystemSession session, LocalArtifactRegistration request )
    {
        Collection<String> repositories;
        if ( request.getRepository() == null )
        {
            repositories = Collections.singleton( LOCAL_REPO_ID );
        }
        else
        {
            repositories = getRepositoryKeys( request.getRepository(), request.getContexts() );
        }
        addArtifact( request.getArtifact(), repositories, request.getRepository() == null );
    }

    private Collection<String> getRepositoryKeys( RemoteRepository repository, Collection<String> contexts )
    {
        Collection<String> keys = new HashSet<String>();

        if ( contexts != null )
        {
            for ( String context : contexts )
            {
                keys.add( getRepositoryKey( repository, context ) );
            }
        }

        return keys;
    }

    private void addArtifact( Artifact artifact, Collection<String> repositories, boolean local )
    {
        if ( artifact == null )
        {
            throw new IllegalArgumentException( "artifact to register not specified" );
        }
        String path = getPathForArtifact( artifact, local );
        File file = new File( getRepository().getBasedir(), path );
        addRepo( file, repositories );
    }

    private Properties readRepos( File artifactFile )
    {
        File trackingFile = getTrackingFile( artifactFile );

        Properties props = trackingFileManager.read( trackingFile );

        return ( props != null ) ? props : new Properties();
    }

    private void addRepo( File artifactFile, Collection<String> repositories )
    {
        Map<String, String> updates = new HashMap<String, String>();
        for ( String repository : repositories )
        {
            updates.put( getKey( artifactFile, repository ), "" );
        }

        File trackingFile = getTrackingFile( artifactFile );

        trackingFileManager.update( trackingFile, updates );
    }

    private File getTrackingFile( File artifactFile )
    {
        return new File( artifactFile.getParentFile(), trackingFilename );
    }

    private String getKey( File file, String repository )
    {
        return file.getName() + '>' + repository;
    }

    private boolean isTracked( Properties props, File file )
    {
        if ( props != null )
        {
            String keyPrefix = file.getName() + '>';
            for ( Object key : props.keySet() )
            {
                if ( key.toString().startsWith( keyPrefix ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

}
