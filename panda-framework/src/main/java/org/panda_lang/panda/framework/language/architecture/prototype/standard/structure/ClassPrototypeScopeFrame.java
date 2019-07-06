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

package org.panda_lang.panda.framework.language.architecture.prototype.standard.structure;

import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.value.Value;
import org.panda_lang.panda.framework.design.runtime.Frame;
import org.panda_lang.panda.framework.language.architecture.dynamic.AbstractScopeFrame;
import org.panda_lang.panda.framework.language.architecture.value.PandaStaticValue;

import java.util.concurrent.atomic.AtomicInteger;

public class ClassPrototypeScopeFrame extends AbstractScopeFrame<ClassPrototypeScope> {

    private static final AtomicInteger idAssigner = new AtomicInteger();

    private final int id;
    private final ClassPrototype prototype;

    public ClassPrototypeScopeFrame(ClassPrototypeScope scope, ClassPrototype classPrototype) {
        super(scope, classPrototype.getFields().getAmountOfFields());

        this.id = idAssigner.getAndIncrement();
        this.prototype = classPrototype;
    }

    @Override
    public void execute(Frame frame) {
        throw new RuntimeException("Cannot execute instance");
    }

    public Value toValue() {
        return new PandaStaticValue(prototype, this);
    }

    @Override
    public String toString() {
        return prototype.getClassName() + "#" + String.format("%06X", id & 0xFFFFF);
    }

}
