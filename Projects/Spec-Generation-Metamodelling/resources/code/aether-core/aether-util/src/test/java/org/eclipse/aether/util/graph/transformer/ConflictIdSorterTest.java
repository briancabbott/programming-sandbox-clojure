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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.internal.test.util.DependencyGraphParser;
import org.eclipse.aether.util.graph.transformer.ConflictIdSorter;
import org.eclipse.aether.util.graph.transformer.TransformationContextKeys;
import org.junit.Test;

/**
 */
public class ConflictIdSorterTest
    extends AbstractDependencyGraphTransformerTest
{

    @Override
    protected DependencyGraphTransformer newTransformer()
    {
        return new ChainedDependencyGraphTransformer( new SimpleConflictMarker(), new ConflictIdSorter() );
    }

    @Override
    protected DependencyGraphParser newParser()
    {
        return new DependencyGraphParser( "transformer/conflict-id-sorter/" );
    }

    private void expectOrder( List<String> sorted, String... ids )
    {
        Queue<String> queue = new LinkedList<String>( sorted );

        for ( String id : ids )
        {
            String item = queue.poll();
            assertNotNull( String.format( "not enough conflict groups (no match for '%s'", id ), item );

            if ( !"*".equals( id ) )
            {
                assertEquals( id, item );
            }
        }

        assertTrue( String.format( "leftover conflict groups (remaining: '%s')", queue ), queue.isEmpty() );
    }

    private void expectOrder( String... id )
    {
        @SuppressWarnings( "unchecked" )
        List<String> sorted = (List<String>) context.get( TransformationContextKeys.SORTED_CONFLICT_IDS );
        expectOrder( sorted, id );
    }

    private void expectCycle( boolean cycle )
    {
        Collection<?> cycles = (Collection<?>) context.get( TransformationContextKeys.CYCLIC_CONFLICT_IDS );
        assertEquals( cycle, !cycles.isEmpty() );
    }

    @Test
    public void testSimple()
        throws Exception
    {
        DependencyNode node = parseResource( "simple.txt" );
        assertSame( node, transform( node ) );

        expectOrder( "gid2:aid::jar", "gid:aid::jar", "gid:aid2::jar" );
        expectCycle( false );
    }

    @Test
    public void testCycle()
        throws Exception
    {
        DependencyNode node = parseResource( "cycle.txt" );
        assertSame( node, transform( node ) );

        expectOrder( "gid:aid::jar", "gid2:aid::jar" );
        expectCycle( true );
    }

    @Test
    public void testCycles()
        throws Exception
    {
        DependencyNode node = parseResource( "cycles.txt" );
        assertSame( node, transform( node ) );

        expectOrder( "*", "*", "*", "gid:aid::jar" );
        expectCycle( true );
    }

    @Test
    public void testNoConflicts()
        throws Exception
    {
        DependencyNode node = parseResource( "no-conflicts.txt" );
        assertSame( node, transform( node ) );

        expectOrder( "gid:aid::jar", "gid3:aid::jar", "gid2:aid::jar", "gid4:aid::jar" );
        expectCycle( false );
    }

}
