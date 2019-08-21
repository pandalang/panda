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

package org.panda_lang.panda.framework.language.resource.expression.subparsers.operation.subparsers.logical;

import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.design.runtime.flow.Flow;
import org.panda_lang.panda.framework.language.resource.PandaTypes;
import org.panda_lang.panda.framework.language.resource.expression.subparsers.operation.rpn.RPNOperationAction;
import org.panda_lang.panda.framework.language.resource.expression.subparsers.operation.rpn.RPNOperationSupplier;

public class OrOperation implements RPNOperationSupplier, RPNOperationAction<Boolean, Boolean, Boolean> {

    @Override
    public Boolean get(Flow flow, Boolean aValue, Boolean bValue) {
        return aValue || bValue;
    }

    @Override
    public RPNOperationAction of(Expression a, Expression b) {
        return this;
    }

    @Override
    public ClassPrototype returnType(ClassPrototype a, ClassPrototype b) {
        return returnType();
    }

    @Override
    public ClassPrototype returnType() {
        return requiredType();
    }

    @Override
    public ClassPrototype requiredType() {
        return PandaTypes.BOOLEAN;
    }

}