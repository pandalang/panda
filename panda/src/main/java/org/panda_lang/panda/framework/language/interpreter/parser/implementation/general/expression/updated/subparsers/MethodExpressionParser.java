package org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.updated.subparsers;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.framework.design.interpreter.parser.ParserData;
import org.panda_lang.panda.framework.design.interpreter.token.TokenRepresentation;
import org.panda_lang.panda.framework.design.interpreter.token.Tokens;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.old.callbacks.invoker.MethodInvokerExpressionCallback;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.old.callbacks.invoker.MethodInvokerExpressionParser;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.old.callbacks.invoker.MethodInvokerExpressionUtils;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.updated.ExpressionParser;
import org.panda_lang.panda.framework.language.interpreter.parser.implementation.general.expression.updated.ExpressionSubparser;
import org.panda_lang.panda.framework.language.interpreter.token.TokenUtils;
import org.panda_lang.panda.framework.language.resource.syntax.separator.Separators;
import org.panda_lang.panda.framework.language.runtime.expression.PandaExpression;

class MethodExpressionParser implements ExpressionSubparser {

    @Override
    public @Nullable Tokens read(ExpressionParser main, Tokens source) {
        Tokens selected = SubparserUtils.readDotted(main, source, matchable -> {
            while (matchable.hasNext()) {
                TokenRepresentation representation = matchable.next();
                matchable.verify();

                if (!matchable.isMatchable()) {
                    continue;
                }

                if (TokenUtils.equals(representation, Separators.RIGHT_PARENTHESIS_DELIMITER)) {
                    break;
                }
            }
        });

        if (selected == null || selected.size() < 3 ) {
            return null;
        }

        if (!TokenUtils.equals(selected.getLast(), Separators.RIGHT_PARENTHESIS_DELIMITER)) {
            return null;
        }

        if (!selected.contains(Separators.PERIOD) && !TokenUtils.equals(selected.get(1), Separators.LEFT_PARENTHESIS_DELIMITER)) {
            return null;
        }

        return selected;
    }

    @Override
    public Expression parse(ExpressionParser main, ParserData data, Tokens source) {
        MethodInvokerExpressionParser methodInvokerParser = MethodInvokerExpressionUtils.match(source);

        if (methodInvokerParser != null) {
            methodInvokerParser.parse(source, data);
            MethodInvokerExpressionCallback callback = methodInvokerParser.toCallback();
            return new PandaExpression(callback.getReturnType(), callback);
        }

        return null;
    }

    @Override
    public double getPriority() {
        return DefaultSubparserPriorities.DYNAMIC;
    }

}
