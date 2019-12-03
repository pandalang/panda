/*
 * Copyright (c) 2015-2019 Dzikoysk
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

package org.panda_lang.framework.language.architecture.prototype.generator;

import org.panda_lang.framework.design.architecture.module.Module;
import org.panda_lang.framework.design.architecture.prototype.Reference;
import org.panda_lang.framework.design.architecture.prototype.State;
import org.panda_lang.framework.design.architecture.prototype.Visibility;
import org.panda_lang.framework.language.architecture.prototype.PandaPrototype;
import org.panda_lang.framework.language.architecture.prototype.PandaReference;
import org.panda_lang.framework.language.interpreter.source.PandaClassSource;
import org.panda_lang.utilities.commons.ClassUtils;
import org.panda_lang.utilities.commons.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class PrototypeGenerator {

    protected final Map<String, Reference> cachedReferences = new HashMap<>();

    protected Reference generate(Module module, Class<?> type, String name) {
        Reference reference = cachedReferences.get(getId(module, name));

        if (reference != null) {
            return reference;
        }

        // System.out.println("TO GENERATE: " + type + " as " + name + " | " + (i++));

        reference = new PandaReference(name, type, (ref) -> {
            ref.addInitializer(prototype -> {
                if (!Modifier.isPublic(type.getModifiers())) {
                    return;
                }

                for (Field field : type.getFields()) {
                    if (!Modifier.isPublic(field.getModifiers())) {
                        continue;
                    }

                    FieldGenerator generator = new FieldGenerator(this, prototype, field);
                    prototype.getFields().declare(field.getName(), generator::generate);
                }

                for (Constructor<?> constructor : ReflectionUtils.getByModifier(type.getConstructors(), Modifier.PUBLIC)) {
                    ConstructorGenerator generator = new ConstructorGenerator(prototype, constructor);
                    prototype.getConstructors().declare(ref.getName(), generator::generate);
                }

                for (Method method : ReflectionUtils.getByModifier(type.getMethods(), Modifier.PUBLIC)) {
                    MethodGenerator generator = new MethodGenerator(this, prototype, method);
                    prototype.getMethods().declare(method.getName(), generator::generate);
                }
            });

            return PandaPrototype.builder()
                    .name(name)
                    .module(module)
                    .location(new PandaClassSource(type).toLocation())
                    .associated(type)
                    .type(type.isInterface() ? "interface" : "class")
                    .state(State.of(type))
                    .visibility(Visibility.PUBLIC)
                    .build();
        });

        cachedReferences.put(getId(module, reference.getName()), reference);
        return reference;
    }

    protected Reference findOrGenerate(Module module, Class<?> type) {
        if (type.isPrimitive()) {
            Class<?> equivalent = ClassUtils.PRIMITIVE_EQUIVALENT.get(type);

            if (equivalent != null) {
                return findOrGenerate(module, equivalent);
            }
        }

        Optional<Reference> referenceValue = module.getModuleLoader().forClass(type);

        if (referenceValue.isPresent()) {
            return referenceValue.get();
        }

        Reference reference = cachedReferences.get(getId(module, type.getSimpleName()));

        if (reference != null) {
            return reference;
        }

        return generate(module, type, type.getSimpleName());
    }

    private String getId(Module module, String name) {
        return module.getName() + "::" + name;
    }

}
