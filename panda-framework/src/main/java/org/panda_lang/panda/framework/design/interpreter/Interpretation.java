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

package org.panda_lang.panda.framework.design.interpreter;

import org.panda_lang.panda.framework.design.architecture.*;
import org.panda_lang.panda.framework.design.interpreter.messenger.*;
import org.panda_lang.panda.framework.design.resource.Language;

import java.util.*;
import java.util.function.*;

public interface Interpretation {

    Interpretation execute(Runnable runnable);

    <T> T execute(Supplier<T> callback);

    default boolean isHealthy() {
        return getFailures().size() == 0;
    }

    Collection<InterpreterFailure> getFailures();

    Messenger getMessenger();

    Language getLanguage();

    Interpreter getInterpreter();

    Environment getEnvironment();

}
