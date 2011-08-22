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

import org.sonatype.guice.bean.reflect.Logs;
import org.sonatype.guice.bean.reflect.TypeParameters;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;

/**
 * {@link BindingTargetVisitor} that verifies any injected dependencies.
 */
final class DependencyVerifier
    extends DefaultBindingTargetVisitor<Object, Boolean>
{
    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    @Override
    public Boolean visit( final UntargettedBinding<?> binding )
    {
        return verifyImplementation( binding.getKey().getTypeLiteral() );
    }

    @Override
    public Boolean visit( final LinkedKeyBinding<?> binding )
    {
        final Key<?> linkedKey = binding.getLinkedKey();
        if ( linkedKey.getAnnotationType() == null )
        {
            return verifyImplementation( linkedKey.getTypeLiteral() );
        }
        return Boolean.TRUE; // indirect binding, don't scan
    }

    @Override
    public Boolean visitOther( final Binding<?> binding )
    {
        return Boolean.TRUE;
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    private static Boolean verifyImplementation( final TypeLiteral<?> type )
    {
        if ( TypeParameters.isConcrete( type ) && !type.toString().startsWith( "java" ) )
        {
            try
            {
                InjectionPoint.forInstanceMethodsAndFields( type );
                InjectionPoint.forConstructorOf( type );
            }
            catch ( final Throwable e )
            {
                Logs.debug( "Potential problem: {}", type, e );
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
