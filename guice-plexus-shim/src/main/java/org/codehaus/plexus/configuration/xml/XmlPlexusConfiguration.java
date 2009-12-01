/**
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.codehaus.plexus.configuration.xml;

import org.codehaus.plexus.configuration.PlexusConfiguration;

public final class XmlPlexusConfiguration
    implements PlexusConfiguration
{
    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public PlexusConfiguration addChild( final String name, final String value )
    {
        // TODO: do we need to do anything here?
        System.out.println( "TODO XmlPlexusConfiguration.addChild(" + name + ", " + value + ")" );
        return this;
    }
}
