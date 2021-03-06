/*******************************************************************************
 * Copyright (c) 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.sonatype.guice.bean.reflect;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility methods for dealing with {@link WeakReference} collections.
 */
public final class Weak
{
    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    private Weak()
    {
        // static utility class, not allowed to create instances
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    /**
     * @return {@link Collection} whose elements are kept alive with {@link WeakReference}s
     */
    public static <T> Collection<T> elements()
    {
        return elements( 10 );
    }

    /**
     * @param capacity The initial capacity
     * @return {@link Collection} whose elements are kept alive with {@link WeakReference}s
     */
    public static <T> Collection<T> elements( final int capacity )
    {
        return new MildElements<T>( new ArrayList<Reference<T>>( capacity ), false );
    }

    /**
     * @return {@link Map} whose keys are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> keys()
    {
        return keys( 16 );
    }

    /**
     * @param capacity The initial capacity
     * @return {@link Map} whose keys are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> keys( final int capacity )
    {
        return new MildKeys<K, V>( new HashMap<Reference<K>, V>( capacity ), false );
    }

    /**
     * @return {@link ConcurrentMap} whose keys are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> concurrentKeys()
    {
        return concurrentKeys( 16, 4 );
    }

    /**
     * @param capacity The initial capacity
     * @param concurrency The concurrency level
     * @return {@link ConcurrentMap} whose keys are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> concurrentKeys( final int capacity, final int concurrency )
    {
        return new MildKeys<K, V>( new ConcurrentHashMap<Reference<K>, V>( capacity, 0.75f, concurrency ), false );
    }

    /**
     * @return {@link Map} whose values are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> values()
    {
        return values( 16 );
    }

    /**
     * @param capacity The initial capacity
     * @return {@link Map} whose values are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> values( final int capacity )
    {
        return new MildValues<K, V>( new HashMap<K, Reference<V>>( capacity ), false );
    }

    /**
     * @return {@link ConcurrentMap} whose values are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> concurrentValues()
    {
        return concurrentValues( 16, 4 );
    }

    /**
     * @param capacity The initial capacity
     * @param concurrency The concurrency level
     * @return {@link ConcurrentMap} whose values are kept alive with {@link WeakReference}s
     */
    public static <K, V> Map<K, V> concurrentValues( final int capacity, final int concurrency )
    {
        return new MildValues<K, V>( new ConcurrentHashMap<K, Reference<V>>( capacity, 0.75f, concurrency ), false );
    }
}
