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
package org.sonatype.guice.bean.example;

import javax.inject.Named;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

final class Components
{
    @Named( "Tab1" )
    static class Tab1
        extends JPanel
    {
        Tab1()
        {
            add( new JButton( "Button" ) );
        }
    }

    @Named( "Tab2" )
    static class Tab2
        extends JPanel
    {
        Tab2()
        {
            add( new JRadioButton( "Radio" ) );
        }
    }

    @Named( "Tab3" )
    static class Tab3
        extends JPanel
    {
        Tab3()
        {
            add( new JCheckBox( "Check" ) );
        }
    }
}