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

package org.sonatype.guice.bean.locators;

import java.lang.reflect.Modifier;
import java.util.List;

import org.sonatype.guice.bean.locators.spi.BindingPublisher;
import org.sonatype.guice.bean.locators.spi.BindingSubscriber;
import org.sonatype.guice.bean.reflect.ClassSpace;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Publisher of {@link Binding}s from a single {@link Injector}; ranked according to a given {@link RankingFunction}.
 */
final class InjectorPublisher
    implements BindingPublisher
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final Injector injector;

    private final RankingFunction function;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    InjectorPublisher( final Injector injector, final RankingFunction function )
    {
        this.injector = injector;
        this.function = function;
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public <T> void subscribe( final TypeLiteral<T> type, final BindingSubscriber importer )
    {
        final List<Binding<T>> bindings = injector.findBindingsByType( type );
        for ( int i = 0, size = bindings.size(); i < size; i++ )
        {
            final Binding<T> binding = bindings.get( i );
            if ( false == binding.getSource() instanceof HiddenBinding )
            {
                importer.add( binding, function.rank( binding ) );
            }
        }
        final Class<?> clazz = type.getRawType();
        if ( bindings.isEmpty() && ( clazz.getModifiers() & ( Modifier.INTERFACE | Modifier.ABSTRACT ) ) == 0 )
        {
            try
            {
                if ( injector.getInstance( ClassSpace.class ).loadedClass( clazz ) )
                {
                    importer.add( injector.getBinding( Key.get( type ) ), -Integer.MAX_VALUE );
                }
            }
            catch ( final Throwable e )
            {
                // ignore
            }
        }
    }

    public <T> boolean contains( final Binding<T> binding )
    {
        // make sure we really own the binding: use identity not equality
        return binding == injector.getBindings().get( binding.getKey() );
    }

    public <T> void unsubscribe( final TypeLiteral<T> type, final BindingSubscriber importer )
    {
        // nothing to do, we don't publish injector bindings asynchronously
    }

    @Override
    public int hashCode()
    {
        return injector.hashCode();
    }

    @Override
    public boolean equals( final Object rhs )
    {
        if ( this == rhs )
        {
            return true;
        }
        if ( rhs instanceof InjectorPublisher )
        {
            return injector.equals( ( (InjectorPublisher) rhs ).injector );
        }
        return false;
    }
}
