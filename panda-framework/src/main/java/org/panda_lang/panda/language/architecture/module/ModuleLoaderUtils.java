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

package org.panda_lang.panda.language.architecture.module;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.module.ModuleLoader;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototypeReference;
import org.panda_lang.panda.framework.design.interpreter.parser.Context;
import org.panda_lang.panda.framework.design.interpreter.parser.component.UniversalComponents;
import org.panda_lang.panda.framework.design.interpreter.token.Snippet;
import org.panda_lang.panda.language.interpreter.parser.PandaParserFailure;

import java.util.Optional;
import java.util.function.Function;

public final class ModuleLoaderUtils {

    private ModuleLoaderUtils() { }

    public static @Nullable ClassPrototypeReference getReferenceOrNull(Context context, String className) {
        return getReferenceOrOptional(context, className).orElse(null);
    }

    public static Optional<ClassPrototypeReference> getReferenceOrOptional(Context context, String className) {
        return context.getComponent(UniversalComponents.MODULE_LOADER).forName(className);
    }

    public static ClassPrototypeReference getReferenceOrThrow(Context context, String className, @Nullable Snippet source) {
        return getReferenceOrThrow(context, className, "Unknown type " + className, source);
    }

    public static ClassPrototypeReference getReferenceOrThrow(Context context, String className, String message, @Nullable Snippet source) {
        return getReferenceOrThrow(context, loader -> loader.forName(className), "Unknown type " + className, source);
    }

    public static ClassPrototypeReference getReferenceOrThrow(Context context, Class<?> type, @Nullable Snippet source) {
        return getReferenceOrThrow(context, type, "Unknown type " + type, source);
    }

    public static ClassPrototypeReference getReferenceOrThrow(Context context, Class<?> type, String message, @Nullable Snippet source) {
        return getReferenceOrThrow(context, loader -> loader.forName(type.getCanonicalName()), message, source);
    }

    static ClassPrototypeReference getReferenceOrThrow(Context context, Function<ModuleLoader, Optional<ClassPrototypeReference>> mapper, String message, Snippet source) {
        Optional<ClassPrototypeReference> reference = mapper.apply(context.getComponent(UniversalComponents.MODULE_LOADER));

        if (!reference.isPresent()) {
            throw PandaParserFailure.builder(message, context)
                    .withStreamOrigin(source)
                    .build();
        }

        return reference.get();
    }

}
