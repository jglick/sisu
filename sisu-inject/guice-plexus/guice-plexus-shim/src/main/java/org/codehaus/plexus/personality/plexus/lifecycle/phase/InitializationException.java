package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/*******************************************************************************
 * Copyright (c) 2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

public final class InitializationException
    extends Exception
{
    private static final long serialVersionUID = 1L;

    public InitializationException( final String message )
    {
        super( message );
    }

    public InitializationException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
