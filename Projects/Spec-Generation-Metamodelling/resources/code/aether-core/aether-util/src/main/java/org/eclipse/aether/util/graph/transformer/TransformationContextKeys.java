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
package org.eclipse.aether.util.graph.transformer;

/**
 * A collection of keys used by the dependency graph transformers when exchanging information via the graph
 * transformation context.
 * 
 * @see org.eclipse.aether.collection.DependencyGraphTransformationContext#get(Object)
 */
public final class TransformationContextKeys
{

    /**
     * The key in the graph transformation context where a {@code Map<DependencyNode, Object>} is stored which maps
     * dependency nodes to their conflict ids. All nodes that map to an equal conflict id belong to the same group of
     * conflicting dependencies. Note that the map keys use reference equality.
     * 
     * @see ConflictMarker
     */
    public static final Object CONFLICT_IDS = "conflictIds";

    /**
     * The key in the graph transformation context where a {@code List<Object>} is stored that denotes a topological
     * sorting of the conflict ids.
     * 
     * @see ConflictIdSorter
     */
    public static final Object SORTED_CONFLICT_IDS = "sortedConflictIds";

    /**
     * The key in the graph transformation context where a {@code Collection<Collection<Object>>} is stored that denotes
     * cycles among conflict ids. Each element in the outer collection denotes one cycle, i.e. if the collection is
     * empty, the conflict ids have no cyclic dependencies.
     * 
     * @see ConflictIdSorter
     */
    public static final Object CYCLIC_CONFLICT_IDS = "cyclicConflictIds";

    /**
     * The key in the graph transformation context where a {@code Map<String, Object>} is stored that can be used to
     * include some runtime/performance stats in the debug log. If this map is not present, no stats should be recorded.
     */
    public static final Object STATS = "stats";

    private TransformationContextKeys()
    {
        // hide constructor
    }

}
