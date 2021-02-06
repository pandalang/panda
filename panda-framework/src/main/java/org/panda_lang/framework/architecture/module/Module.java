/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.panda_lang.framework.architecture.module;

import org.panda_lang.framework.architecture.packages.Package;
import org.panda_lang.framework.architecture.type.Reference;
import org.panda_lang.utilities.commons.function.Option;

import java.util.Collection;

/**
 * Identifiable container of resources
 */
public interface Module {

    /**
     * Add reference to type to the module
     *
     * @param reference the reference to add
     * @return the added reference
     */
    Reference add(Reference reference);

    /**
     * Get reference if exists
     *
     * @param name the name of reference
     * @return the option with reference, otherwise none
     */
    Option<Reference> get(String name);

    /**
     * Get types that belongs to the module
     *
     * @return collection of types
     */
    Collection<? extends Reference> getReferences();

    Package getPackage();

    /**
     * Get non prefixed name of module
     *
     * @return the simple name of module
     */
    String getSimpleName();

    /**
     * Get name of module
     *
     * @return the name
     */
    String getName();

}
