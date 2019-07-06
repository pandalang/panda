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

package org.panda_lang.panda.framework.language.resource.parsers.expression.subparsers.callbacks;

import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.prototype.constructor.PrototypeConstructor;
import org.panda_lang.panda.framework.design.architecture.value.Value;
import org.panda_lang.panda.framework.design.runtime.Frame;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.design.runtime.expression.ExpressionCallback;
import org.panda_lang.panda.framework.language.interpreter.parser.expression.ExpressionUtils;
import org.panda_lang.panda.framework.language.runtime.PandaRuntimeException;

public class InstanceExpressionCallback implements ExpressionCallback {

    private final ClassPrototype returnType;
    private final PrototypeConstructor constructor;
    private final Expression[] arguments;

    public InstanceExpressionCallback(ClassPrototype returnType, PrototypeConstructor constructor, Expression... arguments) {
        this.returnType = returnType;
        this.constructor = constructor;
        this.arguments = arguments;
    }

    @Override
    public Value call(Expression expression, Frame frame) {
        Value[] values = ExpressionUtils.getValues(frame, arguments);

        try {
            return constructor.invoke(frame, null, values);
        } catch (Exception e) {
            throw new PandaRuntimeException("Cannot create instance: " + e.getMessage(), e);
        }
    }

    @Override
    public ClassPrototype getReturnType() {
        return returnType;
    }

}
