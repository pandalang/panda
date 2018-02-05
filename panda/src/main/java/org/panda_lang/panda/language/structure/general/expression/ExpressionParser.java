/*
 * Copyright (c) 2015-2018 Dzikoysk
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

package org.panda_lang.panda.language.structure.general.expression;

import org.panda_lang.panda.core.interpreter.lexer.pattern.TokenPattern;
import org.panda_lang.panda.core.interpreter.parser.linker.ScopeLinker;
import org.panda_lang.panda.core.interpreter.parser.util.Components;
import org.panda_lang.panda.core.structure.value.PandaValue;
import org.panda_lang.panda.core.structure.value.Variable;
import org.panda_lang.panda.core.structure.wrapper.Scope;
import org.panda_lang.panda.framework.implementation.interpreter.parser.PandaParserException;
import org.panda_lang.panda.framework.implementation.interpreter.token.reader.PandaTokenReader;
import org.panda_lang.panda.framework.language.interpreter.parser.Parser;
import org.panda_lang.panda.framework.language.interpreter.parser.ParserInfo;
import org.panda_lang.panda.framework.language.interpreter.token.Token;
import org.panda_lang.panda.framework.language.interpreter.token.TokenType;
import org.panda_lang.panda.framework.language.interpreter.token.TokenUtils;
import org.panda_lang.panda.framework.language.interpreter.token.TokenizedSource;
import org.panda_lang.panda.framework.language.interpreter.token.reader.TokenReader;
import org.panda_lang.panda.language.structure.general.expression.callbacks.instance.InstanceExpressionCallback;
import org.panda_lang.panda.language.structure.general.expression.callbacks.instance.InstanceExpressionParser;
import org.panda_lang.panda.language.structure.general.expression.callbacks.instance.ThisExpressionCallback;
import org.panda_lang.panda.language.structure.general.expression.callbacks.invoker.MethodInvokerExpressionCallback;
import org.panda_lang.panda.language.structure.general.expression.callbacks.invoker.MethodInvokerExpressionParser;
import org.panda_lang.panda.language.structure.general.expression.callbacks.invoker.MethodInvokerExpressionUtils;
import org.panda_lang.panda.language.structure.general.expression.callbacks.math.MathExpressionCallback;
import org.panda_lang.panda.language.structure.general.expression.callbacks.math.MathExpressionUtils;
import org.panda_lang.panda.language.structure.general.expression.callbacks.math.MathParser;
import org.panda_lang.panda.language.structure.general.expression.callbacks.memory.FieldExpressionCallback;
import org.panda_lang.panda.language.structure.general.expression.callbacks.memory.VariableExpressionCallback;
import org.panda_lang.panda.language.structure.general.number.NumberExpressionParser;
import org.panda_lang.panda.language.structure.general.number.NumberUtils;
import org.panda_lang.panda.language.structure.prototype.structure.ClassPrototype;
import org.panda_lang.panda.language.structure.prototype.structure.field.PrototypeField;
import org.panda_lang.panda.language.structure.scope.variable.VariableParserUtils;
import org.panda_lang.panda.language.syntax.tokens.Separators;

import java.util.List;

public class ExpressionParser implements Parser {

    protected static final TokenPattern FIELD_PATTERN = TokenPattern.builder()
            .hollow()
            .unit(Separators.PERIOD)
            .hollow()
            .build();

    public Expression parse(ParserInfo info, TokenizedSource expressionSource) {
        if (expressionSource.size() == 1) {
            Token token = expressionSource.getToken(0);
            String value = token.getTokenValue();

            if (token.getType() == TokenType.LITERAL) {
                switch (token.getTokenValue()) {
                    case "null":
                        return new Expression(new PandaValue(null, null));
                    case "true":
                        return toSimpleKnownExpression("panda.lang:Boolean", true);
                    case "false":
                        return toSimpleKnownExpression("panda.lang:Boolean", false);
                    case "this":
                        ClassPrototype type = info.getComponent(Components.CLASS_PROTOTYPE);
                        return new Expression(type, new ThisExpressionCallback());
                    default:
                        throw new PandaParserException("Unknown literal: " + token);
                }
            }

            if (token.getType() == TokenType.SEQUENCE) {
                switch (token.getName()) {
                    case "String":
                        return toSimpleKnownExpression("panda.lang:String", value);
                    default:
                        throw new PandaParserException("Unknown sequence: " + token);
                }
            }

            NumberExpressionParser numberExpressionParser = new NumberExpressionParser();
            numberExpressionParser.parse(expressionSource, info);

            if (numberExpressionParser.getValue() != null) {
                return new Expression(numberExpressionParser.getValue());
            }


            ScopeLinker scopeLinker = info.getComponent(Components.SCOPE_LINKER);
            Scope scope = scopeLinker.getCurrentScope();
            Variable variable = VariableParserUtils.getVariable(scope, value);

            if (variable != null) {
                int memoryIndex = VariableParserUtils.indexOf(scope, variable);
                return new Expression(variable.getType(), new VariableExpressionCallback(memoryIndex));
            }

            ClassPrototype prototype = info.getComponent(Components.CLASS_PROTOTYPE);
            PrototypeField field = prototype.getField(value);

            if (field != null) {
                int memoryIndex = prototype.getFields().indexOf(field);
                return new Expression(field.getType(), new FieldExpressionCallback(ThisExpressionCallback.asExpression(prototype), field, memoryIndex));
            }
        }
        else if (TokenUtils.equals(expressionSource.get(0), TokenType.KEYWORD, "new")) {
            InstanceExpressionParser callbackParser = new InstanceExpressionParser();

            callbackParser.parse(expressionSource, info);
            InstanceExpressionCallback callback = callbackParser.toCallback();

            return new Expression(callback.getReturnType(), callback);
        }

        MethodInvokerExpressionParser methodInvokerParser = MethodInvokerExpressionUtils.match(expressionSource);

        if (methodInvokerParser != null) {
            methodInvokerParser.parse(expressionSource, info);
            MethodInvokerExpressionCallback callback = methodInvokerParser.toCallback();

            return new Expression(callback.getReturnType(), callback);
        }

        TokenReader expressionReader = new PandaTokenReader(expressionSource);
        List<TokenizedSource> fieldMatches = FIELD_PATTERN.match(expressionReader);

        if (fieldMatches != null && fieldMatches.size() == 2 && !NumberUtils.startsWithNumber(fieldMatches.get(1))) {
            Expression instanceExpression = parse(info, fieldMatches.get(0));
            ClassPrototype instanceType = instanceExpression.getReturnType();
            String instanceFieldName = fieldMatches.get(1).getLast().getToken().getTokenValue();
            PrototypeField instanceField = instanceType.getField(instanceFieldName);

            if (instanceField == null) {
                throw new PandaParserException("Class " + instanceType.getClassName() + " does not contain field " + instanceFieldName);
            }

            int memoryIndex = instanceType.getFields().indexOf(instanceField);
            return new Expression(instanceType, new FieldExpressionCallback(instanceExpression, instanceField, memoryIndex));
        }

        NumberExpressionParser numberExpressionParser = new NumberExpressionParser();
        numberExpressionParser.parse(expressionSource, info);

        if (numberExpressionParser.getValue() != null) {
            return new Expression(numberExpressionParser.getValue());
        }

        if (MathExpressionUtils.isMathExpression(expressionSource)) {
            MathParser mathParser = new MathParser();
            MathExpressionCallback expression = mathParser.parse(expressionSource, info);
            return new Expression(expression.getReturnType(), expression);
        }

        throw new PandaParserException("Cannot recognize expression: " + expressionSource.toString());
    }

    public static Expression toSimpleKnownExpression(String forName, Object value) {
        return new Expression(new PandaValue(ClassPrototype.forName(forName), value));
    }

}
