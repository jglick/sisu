package org.codehaus.plexus.logging;

/*******************************************************************************
 * Copyright (c) 2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

public abstract class AbstractLogEnabled
    implements LogEnabled
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private Logger logger;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public void enableLogging( final Logger theLogger )
    {
        logger = theLogger;
    }

    // ----------------------------------------------------------------------
    // Shared methods
    // ----------------------------------------------------------------------

    protected Logger getLogger()
    {
        return logger;
    }

    protected final void setupLogger( final Object component )
    {
        setupLogger( component, logger );
    }

    protected final void setupLogger( final Object component, final String category )
    {
        if ( category == null )
        {
            throw new IllegalStateException( "Logging category must be defined." );
        }
        setupLogger( component, logger.getChildLogger( category ) );
    }

    protected final void setupLogger( final Object component, final Logger logger )
    {
        if ( component instanceof LogEnabled )
        {
            ( (LogEnabled) component ).enableLogging( logger );
        }
    }
}
