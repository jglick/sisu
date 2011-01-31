package org.sonatype.guice.plexus.annotations;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/

import java.lang.annotation.Annotation;

import org.codehaus.plexus.component.annotations.Configuration;

/**
 * Runtime implementation of Plexus @{@link Configuration} annotation.
 */
public final class ConfigurationImpl
    implements Configuration
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final String name;

    private final String value;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public ConfigurationImpl( final String name, final String value )
    {
        if ( null == name || null == value )
        {
            throw new IllegalArgumentException( "@Configuration cannot contain null values" );
        }

        this.name = name;
        this.value = value;
    }

    // ----------------------------------------------------------------------
    // Annotation properties
    // ----------------------------------------------------------------------

    public String name()
    {
        return name;
    }

    public String value()
    {
        return value;
    }

    // ----------------------------------------------------------------------
    // Standard annotation behaviour
    // ----------------------------------------------------------------------

    @Override
    public boolean equals( final Object rhs )
    {
        if ( this == rhs )
        {
            return true;
        }

        if ( rhs instanceof Configuration )
        {
            final Configuration conf = (Configuration) rhs;

            return name.equals( conf.name() ) && value.equals( conf.value() );
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return ( 127 * "name".hashCode() ^ name.hashCode() ) + ( 127 * "value".hashCode() ^ value.hashCode() );
    }

    @Override
    public String toString()
    {
        return String.format( "@%s(name=%s, value=%s)", Configuration.class.getName(), name, value );
    }

    public Class<? extends Annotation> annotationType()
    {
        return Configuration.class;
    }
}
