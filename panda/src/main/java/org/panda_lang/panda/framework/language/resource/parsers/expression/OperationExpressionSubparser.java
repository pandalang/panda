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
import org.panda_lang.panda.framework.design.interpreter.token.TokenType;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionContext;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionResult;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionSubparser;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionSubparserType;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionSubparserWorker;
import org.panda_lang.panda.framework.design.runtime.expression.ExpressionCategory;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.util.AbstractExpressionSubparserWorker;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.language.resource.parsers.expression.operation.Operation;
import org.panda_lang.panda.framework.language.resource.parsers.expression.operation.OperationParser;

import java.util.ArrayList;
import java.util.List;

public class OperationExpressionSubparser implements ExpressionSubparser {

    private static final OperationParser OPERATION_PARSER = new OperationParser();

    @Override
    public ExpressionSubparserWorker createWorker() {
        return new OperationWorker();
    }

    @Override
    public ExpressionSubparserType getSubparserType() {
        return ExpressionSubparserType.MUTUAL;
    }

    @Override
    public ExpressionCategory getType() {
        return ExpressionCategory.COMBINED;
    }

    @Override
    public String getSubparserName() {
        return "operation";
    }

    private static class OperationWorker extends AbstractExpressionSubparserWorker implements ExpressionSubparserWorker {

        private List<Operation.OperationElement> elements;

        @Override
        public @Nullable ExpressionResult<Expression> next(ExpressionContext context) {
            if (!context.hasResults()) {
                return null;
            }

            if (context.getCurrentRepresentation().getType() != TokenType.OPERATOR) {/*
                if (elements != null) {
                    elements.add(new Operation.OperationElement(context.popExpression()));
                    return finish(context);
                }
                */
                return null;
            }

            if (elements == null) {
                this.elements = new ArrayList<>(3);
            }

            elements.add(new Operation.OperationElement(context.popExpression()));
            elements.add(new Operation.OperationElement(context.getCurrentRepresentation()));

            return ExpressionResult.empty();
        }

        @Override
        public @Nullable ExpressionResult<Expression> finish(ExpressionContext context) {
            if (elements == null) {
                return null;
            }

            if (context.hasResults()) {
                elements.add(new Operation.OperationElement(context.popExpression()));
            }

            Operation operation = new Operation(elements);
            this.elements = null;

            return ExpressionResult.of(OPERATION_PARSER.parse(context.getData(), operation));
        }

    }

}
