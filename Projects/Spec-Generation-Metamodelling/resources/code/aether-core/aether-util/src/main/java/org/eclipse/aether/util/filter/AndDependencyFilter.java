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
package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

/**
 * A dependency filter that combines zero or more other filters using a logical {@code AND}. The resulting filter
 * accepts a given dependency node if and only if all constituent filters accept it.
 */
public final class AndDependencyFilter
    implements DependencyFilter
{

    private final Set<DependencyFilter> filters = new LinkedHashSet<DependencyFilter>();

    /**
     * Creates a new filter from the specified filters. Prefer {@link #newInstance(DependencyFilter, DependencyFilter)}
     * if any of the input filters might be {@code null}.
     * 
     * @param filters The filters to combine, may be {@code null} but must not contain {@code null} elements.
     */
    public AndDependencyFilter( DependencyFilter... filters )
    {
        if ( filters != null )
        {
            Collections.addAll( this.filters, filters );
        }
    }

    /**
     * Creates a new filter from the specified filters.
     * 
     * @param filters The filters to combine, may be {@code null} but must not contain {@code null} elements.
     */
    public AndDependencyFilter( Collection<DependencyFilter> filters )
    {
        if ( filters != null )
        {
            this.filters.addAll( filters );
        }
    }

    /**
     * Creates a new filter from the specified filters.
     * 
     * @param filter1 The first filter to combine, may be {@code null}.
     * @param filter2 The second filter to combine, may be {@code null}.
     * @return The combined filter or {@code null} if both filter were {@code null}.
     */
    public static DependencyFilter newInstance( DependencyFilter filter1, DependencyFilter filter2 )
    {
        if ( filter1 == null )
        {
            return filter2;
        }
        else if ( filter2 == null )
        {
            return filter1;
        }
        return new AndDependencyFilter( filter1, filter2 );
    }

    public boolean accept( DependencyNode node, List<DependencyNode> parents )
    {
        for ( DependencyFilter filter : filters )
        {
            if ( !filter.accept( node, parents ) )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null || !getClass().equals( obj.getClass() ) )
        {
            return false;
        }

        AndDependencyFilter that = (AndDependencyFilter) obj;

        return this.filters.equals( that.filters );
    }

    @Override
    public int hashCode()
    {
        int hash = getClass().hashCode();
        hash = hash * 31 + filters.hashCode();
        return hash;
    }

}
