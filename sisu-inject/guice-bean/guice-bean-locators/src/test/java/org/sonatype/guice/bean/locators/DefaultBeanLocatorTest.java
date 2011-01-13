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
package org.sonatype.guice.bean.locators;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class DefaultBeanLocatorTest
    extends TestCase
{
    interface Bean
    {
    }

    static class BeanImpl
        implements Bean
    {
    }

    static class BeanImpl2
        implements Bean
    {
    }

    Injector parent;

    Injector child1;

    Injector child2;

    Injector child3;

    @Override
    public void setUp()
        throws Exception
    {
        parent = Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( Bean.class ).annotatedWith( Names.named( "A" ) ).to( BeanImpl.class );
                bind( Bean.class ).annotatedWith( Names.named( "-" ) ).to( BeanImpl.class );
                bind( Bean.class ).annotatedWith( Names.named( "Z" ) ).to( BeanImpl.class );
            }
        } );

        child1 = parent.createChildInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( Bean.class ).annotatedWith( Names.named( "M1" ) ).to( BeanImpl.class );
                bind( Bean.class ).to( BeanImpl.class );
                bind( Bean.class ).annotatedWith( Names.named( "N1" ) ).to( BeanImpl.class );
            }
        } );

        child2 = parent.createChildInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                binder().withSource( new HiddenBinding()
                {
                } ).bind( Bean.class ).annotatedWith( Names.named( "HIDDEN" ) ).to( BeanImpl.class );
            }
        } );

        child3 = parent.createChildInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( Bean.class ).annotatedWith( Names.named( "M3" ) ).to( BeanImpl.class );
                bind( Bean.class ).to( BeanImpl2.class );
                bind( Bean.class ).annotatedWith( Names.named( "N3" ) ).to( BeanImpl.class );
            }
        } );
    }

    public void testDefaultLocator()
    {
        final BeanLocator locator = parent.getInstance( BeanLocator.class );
        assertSame( locator, parent.getInstance( MutableBeanLocator.class ) );

        final Iterable<? extends Entry<Named, Bean>> roles =
            locator.<Named, Bean> locate( Key.get( Bean.class, Named.class ) );

        final Iterator<? extends Entry<Named, Bean>> i = roles.iterator();

        assertTrue( i.hasNext() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        try
        {
            i.next();
            fail( "Expected NoSuchElementException" );
        }
        catch ( final NoSuchElementException e )
        {
            // expected
        }

        try
        {
            i.remove();
            fail( "Expected UnsupportedOperationException" );
        }
        catch ( final UnsupportedOperationException e )
        {
            // expected
        }
    }

    @SuppressWarnings( "deprecation" )
    public void testInjectorOrdering()
    {
        final MutableBeanLocator locator = new DefaultBeanLocator();

        final Iterable<? extends Entry<Named, Bean>> roles =
            locator.<Named, Bean> locate( Key.get( Bean.class, Named.class ) );

        locator.add( parent, 0 );
        locator.add( child1, 1 );
        locator.add( child2, 2 );
        locator.add( child3, 3 );

        Iterator<? extends Entry<Named, Bean>> i;

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.remove( child1 );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.add( child1, 4 );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.remove( child2 );
        locator.remove( child2 );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.remove( child3 );
        locator.add( child3, 5 );
        locator.add( child3, 5 );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.remove( parent );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.remove( child1 );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.remove( child3 );

        i = roles.iterator();
        assertFalse( i.hasNext() );

        locator.add( parent, 3 );
        locator.add( child1, 2 );
        locator.add( child2, 1 );
        locator.add( child3, 0 );

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertFalse( i.hasNext() );

        locator.clear();

        i = roles.iterator();
        assertFalse( i.hasNext() );
    }

    @SuppressWarnings( "deprecation" )
    public void testExistingInjectors()
    {
        final MutableBeanLocator locator = new DefaultBeanLocator();

        locator.add( parent, 0 );
        locator.add( child1, 1 );

        final Iterable<? extends Entry<Named, Bean>> roles =
            locator.<Named, Bean> locate( Key.get( Bean.class, Named.class ) );

        locator.add( child2, 2 );
        locator.add( child3, 3 );

        Iterator<? extends Entry<Named, Bean>> i;

        i = roles.iterator();
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "default" ), i.next().getKey() );
        assertEquals( Names.named( "M3" ), i.next().getKey() );
        assertEquals( Names.named( "N3" ), i.next().getKey() );
        assertEquals( Names.named( "M1" ), i.next().getKey() );
        assertEquals( Names.named( "N1" ), i.next().getKey() );
        assertEquals( Names.named( "A" ), i.next().getKey() );
        assertEquals( Names.named( "-" ), i.next().getKey() );
        assertEquals( Names.named( "Z" ), i.next().getKey() );
        assertFalse( i.hasNext() );
    }
}
