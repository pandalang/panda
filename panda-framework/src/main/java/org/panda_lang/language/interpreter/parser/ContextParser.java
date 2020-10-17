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

package org.panda_lang.language.interpreter.parser;

import org.panda_lang.utilities.commons.collection.Component;
import org.panda_lang.utilities.commons.function.Option;

import java.util.concurrent.CompletableFuture;

/**
 * Represents parsers supported by pipelines
 *
 * @param <T> type of result
 */
public interface ContextParser<T, R> extends Parser {

    double DEFAULT_PRIORITY = 1.0;

    /**
     * Get parser priority
     *
     * @return the priority
     */
    default double priority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Get targeted pools by this parser
     *
     * @return the targeted pools
     */
    Component<?>[] targets();

    /**
     * Initialize parser
     *
     * @param context the context used to initialize parser
     */
    default void initialize(Context<? extends T> context) { }

    /**
     * Parse context
     *
     * @param context set of information about source and interpretation process
     */
    Option<CompletableFuture<R>> parse(Context<? extends T> context);

}
