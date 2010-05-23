/**
 * Copyright (c) 2010 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.guice.bean.binders;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.sonatype.guice.bean.locators.BeanLocator;
import org.sonatype.inject.BeanMediator;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * {@link InjectionListener} that listens for mediated watchers and registers them with the {@link BeanLocator}.
 */
@SuppressWarnings( "unchecked" )
final class BeanListener
    implements TypeListener, InjectionListener
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final List<Mediation> mediation = new ArrayList<Mediation>();

    @Inject
    private BeanLocator locator;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * Adds a {@link Mediation} record containing the necessary details about a mediated watcher.
     * 
     * @param watchedKey The watched key
     * @param mediator The bean mediator
     * @param watcherType The watcher type
     */
    public void mediate( final Key watchedKey, final BeanMediator mediator, final Class watcherType )
    {
        mediation.add( new Mediation( watchedKey, mediator, watcherType ) );
    }

    public void hear( final TypeLiteral type, final TypeEncounter encounter )
    {
        for ( final Mediation m : mediation )
        {
            if ( m.watcherType.isAssignableFrom( type.getRawType() ) )
            {
                encounter.register( this ); // look out for watcher instances
            }
        }
    }

    public void afterInjection( final Object watcher )
    {
        for ( final Mediation m : mediation )
        {
            if ( m.watcherType.isInstance( watcher ) )
            {
                locator.watch( m.watchedKey, m.mediator, watcher );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Implementation types
    // ----------------------------------------------------------------------

    /**
     * Record containing all the necessary details about a mediated watcher.
     */
    private static final class Mediation
    {
        final Key watchedKey;

        final BeanMediator mediator;

        final Class watcherType;

        Mediation( final Key watchedKey, final BeanMediator mediator, final Class watcherType )
        {
            this.watchedKey = watchedKey;
            this.mediator = mediator;
            this.watcherType = watcherType;
        }
    }
}
