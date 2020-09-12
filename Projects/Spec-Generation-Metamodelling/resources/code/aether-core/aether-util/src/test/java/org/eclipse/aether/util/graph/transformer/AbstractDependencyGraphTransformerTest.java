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
package org.eclipse.aether.util.graph.transformer;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.internal.test.util.DependencyGraphParser;
import org.eclipse.aether.internal.test.util.TestUtils;
import org.junit.After;
import org.junit.Before;

/**
 */
public abstract class AbstractDependencyGraphTransformerTest
{

    protected DependencyGraphTransformer transformer;

    protected DependencyGraphParser parser;

    protected DefaultRepositorySystemSession session;

    protected DependencyGraphTransformationContext context;

    protected abstract DependencyGraphTransformer newTransformer();

    protected abstract DependencyGraphParser newParser();

    protected DependencyNode transform( DependencyNode root )
        throws Exception
    {
        context = TestUtils.newTransformationContext( session );
        root = transformer.transformGraph( root, context );
        assertNotNull( root );
        return root;
    }

    protected DependencyNode parseResource( String resource, String... substitutions )
        throws Exception
    {
        parser.setSubstitutions( substitutions );
        return parser.parseResource( resource );
    }

    protected DependencyNode parseLiteral( String literal, String... substitutions )
        throws Exception
    {
        parser.setSubstitutions( substitutions );
        return parser.parseLiteral( literal );
    }

    protected List<DependencyNode> find( DependencyNode node, String id )
    {
        LinkedList<DependencyNode> trail = new LinkedList<DependencyNode>();
        find( trail, node, id );
        return trail;
    }

    private boolean find( LinkedList<DependencyNode> trail, DependencyNode node, String id )
    {
        trail.addFirst( node );

        if ( isMatch( node, id ) )
        {
            return true;
        }

        for ( DependencyNode child : node.getChildren() )
        {
            if ( find( trail, child, id ) )
            {
                return true;
            }
        }

        trail.removeFirst();

        return false;
    }

    private boolean isMatch( DependencyNode node, String id )
    {
        if ( node.getDependency() == null )
        {
            return false;
        }
        return id.equals( node.getDependency().getArtifact().getArtifactId() );
    }

    @Before
    public void setUp()
    {
        transformer = newTransformer();
        parser = newParser();
        session = new DefaultRepositorySystemSession();
    }

    @After
    public void tearDown()
    {
        transformer = null;
        parser = null;
        session = null;
        context = null;
    }

}
