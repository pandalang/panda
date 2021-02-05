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

package org.panda_lang.panda.language.std;

import org.panda_lang.framework.PandaFrameworkException;
import org.panda_lang.framework.architecture.module.Module;
import org.panda_lang.framework.architecture.module.ModulePath;
import org.panda_lang.framework.architecture.module.TypeLoader;
import org.panda_lang.framework.architecture.type.generator.TypeGenerator;
import org.panda_lang.framework.resource.internal.InternalModuleInfo;
import org.panda_lang.framework.resource.internal.InternalModuleInfo.CustomInitializer;
import org.panda_lang.utilities.commons.ClassUtils;
import org.panda_lang.utilities.commons.StringUtils;

public final class StdLoader {

    public void load(ModulePath modulePath, TypeGenerator typeGenerator, TypeLoader typeLoader) {
        load(modulePath, typeGenerator, typeLoader, PandaModules.getMappings());
    }

    public void load(ModulePath modulePath, TypeGenerator typeGenerator, TypeLoader typeLoader, Object[] mappings) {
        for (Object object : mappings) {
            loadClass(modulePath, typeGenerator, typeLoader, object);
        }
    }

    private void loadClass(ModulePath modulePath, TypeGenerator typeGenerator, TypeLoader typeLoader, Object mappings) {
        InternalModuleInfo moduleInfo = mappings.getClass().getAnnotation(InternalModuleInfo.class);

        Module module = modulePath.acquire(moduleInfo.module()).orThrow(() -> {
            throw new IllegalStateException("Cannot acquire module " + moduleInfo.module());
        });

        if (mappings instanceof CustomInitializer) {
            CustomInitializer initializer = (CustomInitializer) mappings;
            initializer.initialize(module, typeGenerator, typeLoader);
        }

        String packageName = moduleInfo.pkg().isEmpty() ? StringUtils.EMPTY : moduleInfo.pkg() + ".";

        for (String name : moduleInfo.classes()) {
            ClassUtils.forName(packageName + name)
                    .map(type -> typeGenerator.generate(module, name, type))
                    .peek(module::add)
                    .peek(reference -> typeLoader.load(reference.fetchType()))
                    .orThrow(() -> {
                        throw new PandaFrameworkException("Cannot find class " + name + " in " + packageName);
                    });
        }
    }

}