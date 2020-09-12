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

import java.util.List;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.graph.Dependency;

/**
 * @see DefaultDependencyCollector
 */
final class DefaultDependencyCollectionContext
    implements DependencyCollectionContext
{

    private final RepositorySystemSession session;

    private Artifact artifact;

    private Dependency dependency;

    private List<Dependency> managedDependencies;

    public DefaultDependencyCollectionContext( RepositorySystemSession session, Artifact artifact,
                                               Dependency dependency, List<Dependency> managedDependencies )
    {
        this.session = session;
        this.artifact = ( dependency != null ) ? dependency.getArtifact() : artifact;
        this.dependency = dependency;
        this.managedDependencies = managedDependencies;
    }

    public RepositorySystemSession getSession()
    {
        return session;
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public Dependency getDependency()
    {
        return dependency;
    }

    public List<Dependency> getManagedDependencies()
    {
        return managedDependencies;
    }

    public void set( Dependency dependency, List<Dependency> managedDependencies )
    {
        artifact = dependency.getArtifact();
        this.dependency = dependency;
        this.managedDependencies = managedDependencies;
    }

    @Override
    public String toString()
    {
        return String.valueOf( getDependency() );
    }

}
