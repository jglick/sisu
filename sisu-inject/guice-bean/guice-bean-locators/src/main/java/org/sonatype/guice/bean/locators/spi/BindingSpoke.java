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
package org.sonatype.guice.bean.locators.spi;

import java.util.List;

import com.google.inject.Binding;

/**
 * Observes {@link Binding}s distributed from a {@link BindingHub}.
 */
public interface BindingSpoke<T>
{
    /**
     * Adds the given {@link Binding}s.
     * 
     * @param bindings The new bindings
     */
    void add( List<Binding<T>> bindings );

    /**
     * Removes the given {@link Binding}s.
     * 
     * @param bindings The old bindings
     */
    void remove( List<Binding<T>> bindings );
}
