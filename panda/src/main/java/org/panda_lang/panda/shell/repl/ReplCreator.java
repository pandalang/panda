/*
 * Copyright (c) 2020 Dzikoysk
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

package org.panda_lang.panda.shell.repl;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.framework.design.architecture.module.Module;
import org.panda_lang.framework.design.architecture.type.State;
import org.panda_lang.framework.design.architecture.type.Type;
import org.panda_lang.framework.design.interpreter.parser.Components;
import org.panda_lang.framework.design.interpreter.parser.Context;
import org.panda_lang.framework.design.runtime.Process;
import org.panda_lang.framework.design.runtime.ProcessStack;
import org.panda_lang.framework.language.architecture.statement.PandaVariableData;
import org.panda_lang.framework.language.architecture.type.PandaConstructor;
import org.panda_lang.framework.language.architecture.type.PandaMethod;
import org.panda_lang.framework.language.architecture.type.PandaType;
import org.panda_lang.framework.language.architecture.type.TypeComponents;
import org.panda_lang.framework.language.architecture.type.TypeScope;
import org.panda_lang.framework.language.interpreter.source.PandaClassSource;
import org.panda_lang.framework.language.interpreter.token.PandaSourceLocationUtils;
import org.panda_lang.framework.language.runtime.PandaProcess;
import org.panda_lang.panda.language.interpreter.parser.PandaContextUtils;
import org.panda_lang.utilities.commons.function.ThrowingFunction;

import java.util.Collections;
import java.util.function.Supplier;

/**
 * REPL creator
 */
public final class ReplCreator {

    protected final ReplConsole console;
    protected final Context context;
    protected final TypeScope typeScope;
    protected final ReplScope replScope;
    protected ReplExceptionListener exceptionListener;
    protected Supplier<Process> processSupplier;
    protected ThrowingFunction<ProcessStack, Object, Exception> instanceSupplier;

    ReplCreator(ReplConsole console) {
        this.console = console;
        this.context = PandaContextUtils.createStubContext(console.getFrameworkController());

        Module module = context.getComponent(Components.SCRIPT).getModule();
        Type type = PandaType.builder()
                .name("ShellType")
                .module(module)
                .location(new PandaClassSource(ReplCreator.class).toLocation())
                .state(State.FINAL)
                .build();

        context.withComponent(TypeComponents.PROTOTYPE, type);
        this.typeScope = new TypeScope(PandaSourceLocationUtils.unknownLocation("repl"), type);

        type.getConstructors().declare(PandaConstructor.builder()
                .type(type)
                .callback((typeConstructor, frame, instance, arguments) -> typeScope.createInstance(frame, instance, typeConstructor, new Class<?>[0], arguments))
                .location(type.getLocation())
                .build());

        this.replScope = new ReplScope(typeScope.getSourceLocation(), Collections.emptyList());
        context.withComponent(Components.SCOPE, replScope);
    }

    /**
     * Create REPL
     *
     * @return a REPL instance generated by the creator
     * @throws Exception if something happen
     */
    public Repl create() throws Exception {
        this.processSupplier = () -> new PandaProcess(context.getComponent(Components.APPLICATION), replScope);
        this.instanceSupplier = stack -> typeScope.createInstance(stack, typeScope, typeScope.getType().getConstructors().getConstructor(new Type[0]).getOrNull(), new Class<?>[0], new Object[0]);

        return new Repl(this);
    }

    /**
     * Define a method in the repl type
     *
     * @param method the method to register
     * @return the REPL creator instance
     */
    public ReplCreator define(PandaMethod method) {
        typeScope.getType().getMethods().declare(method);
        return this;
    }

    /**
     * Define variable in the repl scope
     *
     * @param variableData the variable to register
     * @param defaultValue value of variable assigned by default
     * @return the REPL creator instance
     */
    public ReplCreator variable(PandaVariableData variableData, @Nullable Object defaultValue) {
        replScope.setDefaultValue(replScope.createVariable(variableData), defaultValue);
        return this;
    }

    /**
     * Register variable change listener
     *
     * @param variableChangeListener the listener to register
     * @return the REPL creator instance
     */
    public ReplCreator addVariableChangeListener(ReplVariableChangeListener variableChangeListener) {
        replScope.addVariableChangeListener(variableChangeListener);
        return this;
    }

    /**
     * Set custom exception handler, by default exceptions are passed through the {@link org.panda_lang.framework.design.interpreter.messenger.Messenger}
     *
     * @param exceptionListener a new exception listener
     * @return the REPL creator instance
     */
    public ReplCreator withCustomExceptionListener(@Nullable ReplExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    public Context getContext() {
        return context;
    }

}
