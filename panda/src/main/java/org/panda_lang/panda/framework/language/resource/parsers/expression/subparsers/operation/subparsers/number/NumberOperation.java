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

package org.panda_lang.panda.framework.language.resource.parsers.expression.subparsers.operation.subparsers.number;

import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.language.resource.parsers.expression.subparsers.operation.rpn.RPNOperationSupplier;
import org.panda_lang.panda.framework.language.resource.parsers.common.number.NumberPriorities;

public abstract class NumberOperation extends NumberPriorities implements RPNOperationSupplier {

    protected ClassPrototype estimateType(ClassPrototype a, ClassPrototype b) {
        if (a == b) {
            return a;
        }

        return getPriority(a) < getPriority(b) ? b : a;
    }

}