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
package org.sonatype.nexus.plugins.repository;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;

/**
 * {@link File} backed {@link NexusWritablePluginRepository} that supplies user plugins.
 */
@Component( role = NexusPluginRepository.class, hint = UserNexusPluginRepository.ID )
final class UserNexusPluginRepository
    extends AbstractFileNexusWritablePluginRepository
{
    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    static final String ID = "user";

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    @Configuration( value = "${nexus-work}/plugin-repository" )
    private File pluginsFolder;

    private final Map<?, ?> contextMap;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    @Inject
    public UserNexusPluginRepository( final Context context )
    {
        contextMap = new ContextMapAdapter( context );
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public int compareTo( final NexusPluginRepository rhs )
    {
        return this == rhs ? 0 : 1;
    }

    public String getId()
    {
        return ID;
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    @Override
    Map<?, ?> getContextMap()
    {
        return contextMap;
    }

    @Override
    File getPluginsFolder()
    {
        return pluginsFolder;
    }
}