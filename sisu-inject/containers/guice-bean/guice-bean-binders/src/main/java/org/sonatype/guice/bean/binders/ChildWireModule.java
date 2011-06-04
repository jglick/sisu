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
package org.sonatype.guice.bean.binders;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * Child {@link WireModule} that avoids wiring dependencies that already exist in a parent {@link Injector}.
 */
public class ChildWireModule
    extends WireModule
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    final Injector parent;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public ChildWireModule( final Injector parent, final Module... modules )
    {
        this( parent, Arrays.asList( modules ) );
    }

    public ChildWireModule( final Injector parent, final List<Module> modules )
    {
        super( modules );
        this.parent = parent;
    }

    // ----------------------------------------------------------------------
    // Customizable methods
    // ----------------------------------------------------------------------

    @Override
    protected Wiring wiring( final Binder binder )
    {
        return new ChildWiring( super.wiring( binder ) );
    }

    // ----------------------------------------------------------------------
    // Implementation types
    // ----------------------------------------------------------------------

    private final class ChildWiring
        implements Wiring
    {
        // ----------------------------------------------------------------------
        // Implementation fields
        // ----------------------------------------------------------------------

        private final Wiring wiring;

        // ----------------------------------------------------------------------
        // Constructors
        // ----------------------------------------------------------------------

        ChildWiring( final Wiring wiring )
        {
            this.wiring = wiring;
        }

        // ----------------------------------------------------------------------
        // Public methods
        // ----------------------------------------------------------------------

        public boolean wire( final Key<?> key )
        {
            return parent.getExistingBinding( key ) != null || wiring.wire( key );
        }
    }
}
