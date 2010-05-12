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
package org.sonatype.inject;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

/**
 * Mediates updates between the {@code BeanLocator} and an associated watcher.
 */
public interface Mediator<Q extends Annotation, T, W>
{
    /**
     * Notifies the associated watcher that a qualified bean has been added.
     * 
     * @param qualifier Bean qualifier
     * @param bean Bean provider
     * @param watcher Associated watcher
     */
    void add( Q qualifier, Provider<T> bean, W watcher )
        throws Exception;

    /**
     * Notifies the associated watcher that a qualified bean has been removed.
     * 
     * @param qualifier Bean qualifier
     * @param bean Bean provider
     * @param watcher Associated watcher
     */
    void remove( Q qualifier, Provider<T> bean, W watcher )
        throws Exception;
}
