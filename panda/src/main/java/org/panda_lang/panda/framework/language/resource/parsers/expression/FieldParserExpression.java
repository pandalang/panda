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

package org.panda_lang.panda.framework.language.resource.parsers.expression;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.architecture.module.ModuleLoader;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototypeReference;
import org.panda_lang.panda.framework.design.architecture.prototype.field.PrototypeField;
import org.panda_lang.panda.framework.design.architecture.statement.Scope;
import org.panda_lang.panda.framework.design.architecture.value.Variable;
import org.panda_lang.panda.framework.design.interpreter.parser.PandaComponents;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.parser.linker.ScopeLinker;
import org.panda_lang.panda.framework.design.interpreter.pattern.abyss.AbyssPattern;
import org.panda_lang.panda.framework.design.interpreter.pattern.abyss.utils.AbyssPatternBuilder;
import org.panda_lang.panda.framework.design.interpreter.token.Token;
import org.panda_lang.panda.framework.design.interpreter.token.TokenType;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.design.architecture.PandaScript;
import org.panda_lang.panda.framework.language.interpreter.parser.PandaParserFailure;
import org.panda_lang.panda.framework.design.resource.parsers.expression.ExpressionParser;
import org.panda_lang.panda.framework.design.resource.parsers.expression.ExpressionSubparser;
import org.panda_lang.panda.framework.language.resource.parsers.expression.callbacks.ThisExpressionCallback;
import org.panda_lang.panda.framework.language.resource.parsers.expression.callbacks.FieldExpressionCallback;
import org.panda_lang.panda.framework.language.resource.parsers.expression.callbacks.VariableExpressionCallback;
import org.panda_lang.panda.framework.design.resource.parsers.expression.utils.reader.DottedFinisher;
import org.panda_lang.panda.framework.design.resource.parsers.expression.utils.reader.ExpressionSeparatorExtensions;
import org.panda_lang.panda.framework.design.resource.parsers.expression.utils.reader.ExpressionSeparatorReader;
import org.panda_lang.panda.framework.language.resource.parsers.general.number.NumberUtils;
import org.panda_lang.panda.framework.language.resource.parsers.prototype.ClassPrototypeComponents;
import org.panda_lang.panda.framework.language.interpreter.token.PandaTokens;
import org.panda_lang.panda.framework.language.interpreter.token.distributors.MatchableDistributor;
import org.panda_lang.panda.framework.language.interpreter.token.stream.PandaTokenReader;
import org.panda_lang.panda.framework.language.resource.syntax.separator.Separators;
import org.panda_lang.panda.framework.language.runtime.expression.PandaExpression;
import org.panda_lang.panda.utilities.commons.ArrayUtils;

import java.util.List;

public class FieldParserExpression implements ExpressionSubparser, DottedFinisher {

    private static final Token[] FIELD_SEPARATORS = ArrayUtils.of(Separators.PERIOD);

    private static final AbyssPattern FIELD_PATTERN = new AbyssPatternBuilder()
            .hollow()
            .unit(Separators.PERIOD)
            .simpleHollow()
            .lastIndexAlgorithm(true)
            .build();

    private final ExpressionSeparatorExtensions extensions = new ExpressionSeparatorExtensions(this, SubparserUtils.NAMES_FILTER);

    @Override
    public boolean finish(ExpressionParser parser, MatchableDistributor matchable) {
        if (matchable.getUnreadLength() < 2) {
            return false;
        }

        // read dot
        matchable.nextVerified();
        // read field name
        matchable.nextVerified();

        return matchable.isMatchable();
    }

    @Override
    public @Nullable Tokens read(ExpressionParser main, Tokens source) {
        Tokens selected = ExpressionSeparatorReader.getInstance().readSeparated(main, source, FIELD_SEPARATORS, extensions);

        if (selected == null && SubparserUtils.isAllowedName(source.getFirst().getToken())) {
            selected = new PandaTokens(source.getFirst());
        }

        if (selected != null && selected.getLast().getToken().getType() != TokenType.UNKNOWN) {
            return null;
        }

        return selected;
    }

    @Override
    public Expression parse(ExpressionParser main, ParserData data, Tokens source) {
        if (source.size() == 1) {
            ScopeLinker scopeLinker = data.getComponent(PandaComponents.SCOPE_LINKER);
            Scope scope = scopeLinker.getCurrentScope();
            Variable variable = scope.getVariable(source.asString());

            if (variable != null) {
                int memoryIndex = scope.indexOf(variable);
                return new PandaExpression(new VariableExpressionCallback(variable, memoryIndex));
            }

            ClassPrototype prototype = data.getComponent(ClassPrototypeComponents.CLASS_PROTOTYPE);

            if (prototype != null) {
                PrototypeField field = prototype.getFields().getField(source.asString());

                if (field != null) {
                    int memoryIndex = prototype.getFields().getIndexOfField(field);
                    return new PandaExpression(new FieldExpressionCallback(ThisExpressionCallback.asExpression(prototype), field, memoryIndex));
                }
            }

            throw new PandaParserFailure("Cannot find variable or field called " + source.asString(), data);
        }

        List<Tokens> fieldMatches = FIELD_PATTERN.match(new PandaTokenReader(source));

        if (fieldMatches != null && fieldMatches.size() == 2 && !NumberUtils.startsWithNumber(fieldMatches.get(1))) {
            PandaScript script = data.getComponent(PandaComponents.PANDA_SCRIPT);

            Tokens instanceSource = fieldMatches.get(0);
            ClassPrototype instanceType = null;
            Expression fieldLocationExpression = null;

            if (instanceSource.size() == 1) {
                ModuleLoader moduleLoader = data.getComponent(PandaComponents.PANDA_SCRIPT).getModuleLoader();
                ClassPrototypeReference reference = moduleLoader.forClass(fieldMatches.get(0).asString());
                instanceType = reference != null ? reference.fetch() : null;
            }

            if (instanceType == null) {
                fieldLocationExpression = main.parse(data, fieldMatches.get(0));
                instanceType = fieldLocationExpression.getReturnType();
            }

            if (instanceType == null) {
                throw new PandaParserFailure("Unknown instance source", data);
            }

            String instanceFieldName = fieldMatches.get(1).asString();
            PrototypeField instanceField = instanceType.getFields().getField(instanceFieldName);

            if (instanceField == null) {
                throw new PandaParserFailure("Class " + instanceType.getClassName() + " does not contain field " + instanceFieldName, data, source);
            }

            int memoryIndex = instanceType.getFields().getIndexOfField(instanceField);
            return new PandaExpression(new FieldExpressionCallback(fieldLocationExpression, instanceField, memoryIndex));
        }

        return null;
    }

    /*
    public Variable parseVariable(ParserData data, Tokens source) {
        return null;
    }
    */

    @Override
    public double getPriority() {
        return DefaultSubparsers.Priorities.DYNAMIC;
    }

    @Override
    public String getName() {
        return DefaultSubparsers.Names.FIELD;
    }

}