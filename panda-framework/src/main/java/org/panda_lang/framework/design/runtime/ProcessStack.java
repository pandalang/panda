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

package org.panda_lang.framework.design.runtime;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.framework.design.architecture.dynamic.Frame;
import org.panda_lang.framework.design.architecture.statement.Scope;
import org.panda_lang.framework.design.architecture.statement.Statement;
import org.panda_lang.utilities.commons.function.ThrowingSupplier;

public interface ProcessStack {

    @Nullable Result<?> call(Object instance, Frame frame) throws Exception;

    @Nullable Result<?> call(Object instance, Frame frame, ThrowingSupplier<Result<?>, Exception> resultSupplier) throws Exception;

    @Nullable Result<?> call(Object instance, Scope scope) throws Exception;

    @Nullable Result<?> call(Object instance, Statement statement) throws Exception;

    Statement[] getLivingFramesOnStack();

    /**
     * @return instance of the current scope
     */
    Frame getCurrentScope();

    /**
     * Get associated process
     *
     * @return the process
     */
    Process getProcess();

}
