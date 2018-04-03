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

package org.panda_lang.panda.language.structure.scope.block.looping.blocks;

import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.value.Value;
import org.panda_lang.panda.framework.design.runtime.ExecutableBranch;
import org.panda_lang.panda.framework.design.runtime.expression.Expression;
import org.panda_lang.panda.framework.design.runtime.flow.ControlFlow;
import org.panda_lang.panda.framework.design.runtime.flow.ControlFlowCaller;
import org.panda_lang.panda.framework.language.architecture.value.PandaValue;
import org.panda_lang.panda.language.structure.scope.block.PandaBlock;

public class ForEachBlock extends PandaBlock implements ControlFlowCaller {

    private final int variableId;
    private final ClassPrototype variableType;
    private final Expression expression;

    public ForEachBlock(int variableId, ClassPrototype variableType, Expression expression) {
        this.variableId = variableId;
        this.variableType = variableType;
        this.expression = expression;
    }

    @Override
    public void execute(ExecutableBranch branch) {
        branch.callFlow(getStatementCells(), this);
    }

    @Override
    public void call(ExecutableBranch branch, ControlFlow flow) {
        Value[] variables = branch.getCurrentScope().getVariables();

        Value iterableValue = expression.getExpressionValue(branch);
        Iterable iterable = iterableValue.getValue();

        for (Object value : iterable) {
            variables[variableId] = new PandaValue(variableType, value);

            flow.reset();
            flow.call();

            if (flow.isEscaped() || branch.isInterrupted()) {
                break;
            }
        }
    }

}
