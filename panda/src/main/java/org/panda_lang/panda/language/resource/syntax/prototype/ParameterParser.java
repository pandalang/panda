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

package org.panda_lang.panda.language.resource.syntax.prototype;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.framework.design.architecture.module.Imports;
import org.panda_lang.framework.design.architecture.prototype.PropertyParameter;
import org.panda_lang.framework.design.architecture.prototype.Prototype;
import org.panda_lang.framework.design.architecture.prototype.Reference;
import org.panda_lang.framework.design.interpreter.parser.Components;
import org.panda_lang.framework.design.interpreter.parser.Context;
import org.panda_lang.framework.design.interpreter.parser.Parser;
import org.panda_lang.framework.design.interpreter.token.Snippet;
import org.panda_lang.framework.design.interpreter.token.SnippetUtils;
import org.panda_lang.framework.design.interpreter.token.Token;
import org.panda_lang.framework.language.architecture.prototype.PandaPropertyParameter;
import org.panda_lang.framework.language.interpreter.parser.PandaParserFailure;
import org.panda_lang.framework.language.resource.syntax.separator.Separators;
import org.panda_lang.utilities.commons.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ParameterParser implements Parser {

    public List<PropertyParameter> parse(Context context, @Nullable Snippet snippet) {
        if (SnippetUtils.isEmpty(snippet)) {
            return Collections.emptyList();
        }

        Snippet[] parametersSource = snippet.split(Separators.COMMA);
        List<PropertyParameter> parameters = new ArrayList<>(parametersSource.length);

        if (ArrayUtils.isEmpty(parametersSource)) {
            return parameters;
        }

        for (int index = 0; index < parametersSource.length; index++) {
            Snippet source = parametersSource[index];
            Token name = source.getLast();

            if (name == null) {
                throw new PandaParserFailure(context, snippet, "Missing parameter at " + index + " position");
            }

            int end = source.size() - 1;

            if (source.contains(Separators.PERIOD)) {
                end -= 3;
            }

            Imports imports = context.getComponent(Components.IMPORTS);
            Optional<Reference> reference = imports.forName(source.subSource(0, end).asSource());

            if (!reference.isPresent()) {
                throw new PandaParserFailure(context, source.subSource(0, end), "Unknown type", "Make sure that type is imported");
            }

            Prototype prototype = reference.get().fetch();
            boolean varargs = end + 1 < source.size();

            if (varargs) {
                prototype = prototype.toArray(context.getComponent(Components.MODULE_LOADER));
            }

            PropertyParameter parameter = new PandaPropertyParameter(index, prototype, name.getValue(), varargs, false);
            parameters.add(parameter);
        }

        return parameters;
    }

}