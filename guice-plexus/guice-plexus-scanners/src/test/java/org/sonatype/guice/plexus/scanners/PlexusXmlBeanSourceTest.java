/**
 * Copyright (c) 2009 Sonatype, Inc. All rights reserved.
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
package org.sonatype.guice.plexus.scanners;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.guice.bean.reflect.BeanProperty;
import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.reflect.DeferredClass;
import org.sonatype.guice.bean.reflect.DeferredProvider;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.guice.plexus.annotations.ComponentImpl;
import org.sonatype.guice.plexus.annotations.ConfigurationImpl;
import org.sonatype.guice.plexus.annotations.RequirementImpl;
import org.sonatype.guice.plexus.config.Hints;
import org.sonatype.guice.plexus.config.PlexusBeanMetadata;
import org.sonatype.guice.plexus.config.PlexusBeanSource;
import org.sonatype.guice.plexus.config.Strategies;

import com.google.inject.TypeLiteral;

public class PlexusXmlBeanSourceTest
    extends TestCase
{
    static class NamedProperty
        implements BeanProperty<Object>
    {
        final String name;

        public NamedProperty( final String name )
        {
            this.name = name;
        }

        public <A extends Annotation> A getAnnotation( final Class<A> annotationType )
        {
            return null;
        }

        public String getName()
        {
            return name;
        }

        public TypeLiteral<Object> getType()
        {
            return TypeLiteral.get( Object.class );
        }

        public <B> void set( final B bean, final Object value )
        {
        }
    }

    interface Bean
    {
    }

    static class DefaultBean
    {
    }

    static class DebugBean
    {
    }

    static class AnotherBean
    {
    }

    public void testLoadOnStart()
    {
        final ClassSpace space = new ClassSpace()
        {
            public Class<?> loadClass( final String name )
            {
                try
                {
                    return Class.forName( name );
                }
                catch ( final ClassNotFoundException e )
                {
                    throw new TypeNotPresentException( name, e );
                }
            }

            @SuppressWarnings( "unchecked" )
            public DeferredClass<?> deferLoadClass( final String name )
            {
                return new DeferredClass()
                {
                    public Class load()
                    {
                        return loadClass( name );
                    }

                    public String getName()
                    {
                        return name;
                    }

                    public DeferredProvider asProvider()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            public URL getResource( final String name )
            {
                return null;
            }

            public Enumeration<URL> getResources( final String name )
            {
                // hide components.xml so we can just test plexus.xml parsing
                return Collections.enumeration( Collections.<URL> emptyList() );
            }

            public Enumeration<URL> findEntries( final String path, final String glob, final boolean recurse )
            {
                // hide components.xml so we can just test plexus.xml parsing
                return Collections.enumeration( Collections.<URL> emptyList() );
            }
        };

        final URL plexusXml = getClass().getResource( "/META-INF/plexus/plexus.xml" );
        final PlexusBeanSource source = new PlexusXmlBeanSource( space, null, plexusXml );
        final Map<Component, DeferredClass<?>> componentMap = source.findPlexusComponentBeans();

        assertEquals( 2, componentMap.size() );

        final Component component1 =
            new ComponentImpl( DefaultBean.class, Hints.DEFAULT_HINT, Strategies.LOAD_ON_START, "" );
        assertEquals( DefaultBean.class, componentMap.get( component1 ).load() );

        final Component component2 = new ComponentImpl( Bean.class, "debug", Strategies.LOAD_ON_START, "For debugging" );
        assertEquals( DebugBean.class, componentMap.get( component2 ).load() );
    }

    public void testBadPlexusXml()
    {
        final ClassSpace space = new URLClassSpace( (URLClassLoader) PlexusXmlBeanSourceTest.class.getClassLoader() );

        try
        {
            new PlexusXmlBeanSource( space, null, getClass().getResource( "/META-INF/plexus/bad_plexus_1.xml" ) ).findPlexusComponentBeans();
            fail( "Expected RuntimeException" );
        }
        catch ( final RuntimeException e )
        {
        }
    }

    public void testComponents()
    {
        final ClassSpace space = new URLClassSpace( (URLClassLoader) PlexusXmlBeanSourceTest.class.getClassLoader() );

        final PlexusBeanSource source = new PlexusXmlBeanSource( space, null );
        final Map<Component, DeferredClass<?>> componentMap = source.findPlexusComponentBeans();

        assertEquals( 3, componentMap.size() );

        final Component component1 =
            new ComponentImpl( DefaultBean.class, Hints.DEFAULT_HINT, Strategies.PER_LOOKUP, "" );
        assertEquals( DefaultBean.class, componentMap.get( component1 ).load() );

        final Component component2 = new ComponentImpl( Bean.class, "debug", Strategies.SINGLETON, "For debugging" );
        assertEquals( DebugBean.class, componentMap.get( component2 ).load() );

        final Component component3 = new ComponentImpl( Bean.class, Hints.DEFAULT_HINT, Strategies.SINGLETON, "" );
        assertEquals( AnotherBean.class, componentMap.get( component3 ).load() );

        final PlexusBeanMetadata metadata1 = source.getBeanMetadata( DefaultBean.class );

        assertFalse( metadata1.isEmpty() );

        assertEquals(
                      new ConfigurationImpl( "someFieldName", "<some-field.name><item>PRIMARY</item></some-field.name>" ),
                      metadata1.getConfiguration( new NamedProperty( "someFieldName" ) ) );

        assertEquals( new ConfigurationImpl( "simple", "value" ),
                      metadata1.getConfiguration( new NamedProperty( "simple" ) ) );

        assertEquals( new ConfigurationImpl( "value", "<value with=\"attribute\"></value>" ),
                      metadata1.getConfiguration( new NamedProperty( "value" ) ) );

        assertEquals( new ConfigurationImpl( "emptyValue1", "<empty.value1 with=\"attribute\" />" ),
                      metadata1.getConfiguration( new NamedProperty( "emptyValue1" ) ) );

        assertEquals( new ConfigurationImpl( "emptyValue2", "" ),
                      metadata1.getConfiguration( new NamedProperty( "emptyValue2" ) ) );

        assertFalse( metadata1.isEmpty() );

        assertEquals( new RequirementImpl( Bean.class, true, "debug" ),
                      metadata1.getRequirement( new NamedProperty( "bean" ) ) );

        assertFalse( metadata1.isEmpty() );

        metadata1.getConfiguration( new NamedProperty( "foo" ) );

        assertEquals( new RequirementImpl( Bean.class, false, Hints.DEFAULT_HINT, "debug" ),
                      metadata1.getRequirement( new NamedProperty( "beanMap" ) ) );

        assertFalse( metadata1.isEmpty() );

        assertEquals( new RequirementImpl( Bean.class, false ),
                      metadata1.getRequirement( new NamedProperty( "beanField" ) ) );

        assertTrue( metadata1.isEmpty() );

        assertNull( source.getBeanMetadata( DebugBean.class ) );
        assertNotNull( source.getBeanMetadata( AnotherBean.class ) );
        assertNull( source.getBeanMetadata( AnotherBean.class ) );
        assertNull( source.getBeanMetadata( DefaultBean.class ) );
    }

    static class FixedClassSpace
        implements ClassSpace
    {
        final String fixedResourceName;

        FixedClassSpace( final String fixedResourceName )
        {
            this.fixedResourceName = fixedResourceName;
        }

        public Class<?> loadClass( final String name )
        {
            try
            {
                return Class.forName( name );
            }
            catch ( final ClassNotFoundException e )
            {
                throw new TypeNotPresentException( name, e );
            }
        }

        @SuppressWarnings( "unchecked" )
        public DeferredClass<?> deferLoadClass( final String name )
        {
            return new DeferredClass()
            {
                public Class load()
                {
                    return loadClass( name );
                }

                public String getName()
                {
                    return name;
                }

                public DeferredProvider asProvider()
                {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public URL getResource( final String name )
        {
            return getClass().getResource( fixedResourceName );
        }

        public Enumeration<URL> getResources( final String name )
        {
            return Collections.enumeration( Collections.singleton( getClass().getResource( fixedResourceName ) ) );
        }

        public Enumeration<URL> findEntries( final String path, final String glob, final boolean recurse )
        {
            return Collections.enumeration( Collections.singleton( getClass().getResource( fixedResourceName ) ) );
        }
    }

    public void testBadComponentsXml()
    {
        try
        {
            final ClassSpace space = new FixedClassSpace( "/META-INF/plexus/bad_components_1.xml" );
            new PlexusXmlBeanSource( space, null ).findPlexusComponentBeans();
            fail( "Expected RuntimeException" );
        }
        catch ( final RuntimeException e )
        {
        }

        try
        {
            final ClassSpace space = new FixedClassSpace( "/META-INF/plexus/bad_components_2.xml" );
            new PlexusXmlBeanSource( space, null ).findPlexusComponentBeans();
            fail( "Expected RuntimeException" );
        }
        catch ( final RuntimeException e )
        {
        }

        try
        {
            final ClassSpace space = new FixedClassSpace( "/META-INF/plexus/bad_components_3.xml" );
            new PlexusXmlBeanSource( space, null ).findPlexusComponentBeans();
            fail( "Expected RuntimeException" );
        }
        catch ( final RuntimeException e )
        {
        }

        {
            final ClassSpace space = new FixedClassSpace( "/META-INF/plexus/bad_components_4.xml" );
            final PlexusBeanSource source = new PlexusXmlBeanSource( space, null );
            assertTrue( source.findPlexusComponentBeans().isEmpty() );
        }

        try
        {
            final ClassSpace space = new FixedClassSpace( "/META-INF/plexus/bad_components_5.xml" );
            final PlexusBeanSource source = new PlexusXmlBeanSource( space, null );
            source.findPlexusComponentBeans();
            final PlexusBeanMetadata metadata = source.getBeanMetadata( DefaultBean.class );
            final Requirement badReq = metadata.getRequirement( new NamedProperty( "class" ) );

            badReq.role();
            fail( "Expected TypeNotPresentException" );
        }
        catch ( final TypeNotPresentException e )
        {
        }
    }

    public void testInterpolatedComponentsXml()
    {
        final ClassSpace space = new FixedClassSpace( "/META-INF/plexus/variable_components.xml" );

        final PlexusBeanSource uninterpolatedSource = new PlexusXmlBeanSource( space, null );
        uninterpolatedSource.findPlexusComponentBeans();
        final PlexusBeanMetadata metadata1 = uninterpolatedSource.getBeanMetadata( DefaultBean.class );
        assertEquals( "${some.value}", metadata1.getConfiguration( new NamedProperty( "variable" ) ).value() );

        final Map<?, ?> variables = Collections.singletonMap( "some.value", "INTERPOLATED" );

        final PlexusBeanSource interpolatedSource = new PlexusXmlBeanSource( space, variables );
        interpolatedSource.findPlexusComponentBeans();
        final PlexusBeanMetadata metadata2 = interpolatedSource.getBeanMetadata( DefaultBean.class );
        assertEquals( "INTERPOLATED", metadata2.getConfiguration( new NamedProperty( "variable" ) ).value() );
    }

    public void testLocalizedXmlScanning()
    {
        final ClassLoader parent = PlexusXmlBeanSourceTest.class.getClassLoader();
        final ClassSpace space = new URLClassSpace( parent, null );
        final PlexusBeanSource source = new PlexusXmlBeanSource( space, null );
        final Map<Component, DeferredClass<?>> componentMap = source.findPlexusComponentBeans();
        assertTrue( componentMap.isEmpty() );
    }
}