/*
 * Copyright (c) 2015-2017 Dzikoysk
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

package org.panda_lang.panda.language.structure.prototype.structure.field.parser;

import org.panda_lang.panda.core.interpreter.lexer.pattern.TokenPattern;
import org.panda_lang.panda.framework.language.interpreter.parser.ParserInfo;
import org.panda_lang.panda.framework.language.interpreter.parser.UnifiedParser;
import org.panda_lang.panda.language.syntax.tokens.Separators;

//@ParserRegistration(target = DefaultPipelines.PROTOTYPE, parserClass = FieldParser.class, handlerClass = FieldParserHandler.class)
public class FieldParser implements UnifiedParser {

    protected static final TokenPattern PATTERN = TokenPattern.builder()
            .simpleHollow()
            .unit(Separators.SEMICOLON)
            .hollow()
            .build();

    @Override
    public void parse(ParserInfo info) {

    }

}
