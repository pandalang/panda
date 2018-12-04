package org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.updated.subparsers;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.old.callbacks.logic.NegateLogicalExpressionCallback;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.updated.ExpressionParser;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.updated.ExpressionSubparser;
import org.panda_lang.panda.framework.language.resource.syntax.operator.Operators;
import org.panda_lang.panda.framework.language.runtime.expression.PandaExpression;

class NegateExpressionParser implements ExpressionSubparser {

    @Override
    public @Nullable Tokens read(ExpressionParser main, Tokens source) {
        if (!source.getFirst().contentEquals(Operators.NOT)) {
            return null;
        }

        return source;
    }

    @Override
    public Expression parse(ExpressionParser main, ParserData data, Tokens source) {
        Expression expression = main.parse(data, source.subSource(1, source.size()));
        return new PandaExpression(expression.getReturnType(), new NegateLogicalExpressionCallback(expression));
    }

    @Override
    public double getPriority() {
        return DefaultSubparserPriorities.SINGLE;
    }

}
