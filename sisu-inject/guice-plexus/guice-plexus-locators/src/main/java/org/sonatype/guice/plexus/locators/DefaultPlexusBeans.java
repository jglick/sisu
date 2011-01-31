package org.sonatype.guice.plexus.locators;

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

import java.util.Iterator;

import org.sonatype.guice.plexus.config.PlexusBean;
import org.sonatype.inject.BeanEntry;

import com.google.inject.name.Named;

final class DefaultPlexusBeans<T>
    implements Iterable<PlexusBean<T>>
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    Iterable<BeanEntry<Named, T>> beans;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    DefaultPlexusBeans( final Iterable<BeanEntry<Named, T>> beans )
    {
        this.beans = beans;
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public Iterator<PlexusBean<T>> iterator()
    {
        return new Itr();
    }

    // ----------------------------------------------------------------------
    // Implementation types
    // ----------------------------------------------------------------------

    final class Itr
        implements Iterator<PlexusBean<T>>
    {
        private final Iterator<BeanEntry<Named, T>> itr = beans.iterator();

        public boolean hasNext()
        {
            return itr.hasNext();
        }

        public PlexusBean<T> next()
        {
            return new LazyPlexusBean<T>( itr.next() );
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
