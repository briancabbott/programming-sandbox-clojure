/*******************************************************************************
 * Copyright (c) 2010, 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.util;

import static org.junit.Assert.*;

import org.eclipse.aether.util.StringUtils;
import org.junit.Test;

/**
 */
public class StringUtilsTest
{

    @Test
    public void testIsEmpty()
    {
        assertTrue( StringUtils.isEmpty( null ) );
        assertTrue( StringUtils.isEmpty( "" ) );
        assertFalse( StringUtils.isEmpty( " " ) );
        assertFalse( StringUtils.isEmpty( "test" ) );
    }

}
