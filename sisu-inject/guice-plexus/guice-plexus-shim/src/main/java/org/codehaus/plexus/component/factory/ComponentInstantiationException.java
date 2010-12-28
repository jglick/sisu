package org.codehaus.plexus.component.factory;

/*******************************************************************************
 * Copyright (c) 2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

public final class ComponentInstantiationException
    extends Exception
{
    private static final long serialVersionUID = 1L;

    public ComponentInstantiationException( final String message )
    {
        super( message );
    }

    public ComponentInstantiationException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
