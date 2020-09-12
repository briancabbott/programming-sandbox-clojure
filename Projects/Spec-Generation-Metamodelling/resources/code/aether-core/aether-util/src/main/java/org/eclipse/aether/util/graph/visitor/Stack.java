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
package org.eclipse.aether.util.graph.visitor;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * A non-synchronized stack with a non-modifiable list view which starts at the top of the stack. While
 * {@code LinkedList} can provide the same behavior, it creates many temp objects upon frequent pushes/pops.
 */
class Stack<E>
    extends AbstractList<E>
    implements RandomAccess
{

    @SuppressWarnings( "unchecked" )
    private E[] elements = (E[]) new Object[96];

    private int size;

    public void push( E element )
    {
        if ( size >= elements.length )
        {
            @SuppressWarnings( "unchecked" )
            E[] tmp = (E[]) new Object[size + 64];
            System.arraycopy( elements, 0, tmp, 0, elements.length );
            elements = tmp;
        }
        elements[size++] = element;
    }

    public E pop()
    {
        if ( size <= 0 )
        {
            throw new NoSuchElementException();
        }
        return elements[--size];
    }

    public E peek()
    {
        if ( size <= 0 )
        {
            return null;
        }
        return elements[size - 1];
    }

    @Override
    public E get( int index )
    {
        if ( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
        }
        return elements[size - index - 1];
    }

    @Override
    public int size()
    {
        return size;
    }

}
