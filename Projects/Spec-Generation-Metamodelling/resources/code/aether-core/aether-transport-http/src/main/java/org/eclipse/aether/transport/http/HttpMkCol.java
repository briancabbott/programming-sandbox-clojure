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
package org.eclipse.aether.transport.http;

import java.net.URI;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * WebDAV MKCOL request to create parent directories.
 */
final class HttpMkCol
    extends HttpRequestBase
{

    public HttpMkCol( URI uri )
    {
        setURI( uri );
    }

    @Override
    public String getMethod()
    {
        return "MKCOL";
    }

}
