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

package org.panda_lang.panda.language.syntax.head;

import org.panda_lang.framework.architecture.module.Module;
import org.panda_lang.framework.architecture.type.Type;
import org.panda_lang.framework.interpreter.parser.Context;
import org.panda_lang.framework.interpreter.parser.PandaParserFailure;
import org.panda_lang.framework.interpreter.token.Snippet;

final class ConveyanceUtils {

    private ConveyanceUtils() { }

    protected static Type fetchType(Context<?> context, Snippet javaTypeSource) {
        try {
            Class<?> importedClass = Class.forName(javaTypeSource.asSource(), true, context.getEnvironment().getController().getClassLoader());

            Module module = context.getScript().getModule().orThrow(() -> {
                throw new PandaParserFailure(context, javaTypeSource, "Undefined script module");
            });

            Type type = context.getEnvironment().getTypeGenerator().generate(module, importedClass.getSimpleName(), importedClass).fetchType();
            return context.getTypeLoader().load(type);
        } catch (ClassNotFoundException classNotFoundException) {
            throw new PandaParserFailure(context, javaTypeSource, "Class " + javaTypeSource.asSource() + " does not exist");
        }
    }

}