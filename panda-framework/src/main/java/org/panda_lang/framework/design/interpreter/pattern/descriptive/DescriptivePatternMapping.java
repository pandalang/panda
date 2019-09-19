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

package org.panda_lang.framework.design.interpreter.pattern.descriptive;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.framework.design.interpreter.pattern.PatternMapping;
import org.panda_lang.framework.design.interpreter.pattern.descriptive.extractor.ExtractorResult;
import org.panda_lang.framework.design.interpreter.pattern.descriptive.extractor.ExtractorResultElement;

public class DescriptivePatternMapping implements PatternMapping {

    private final ExtractorResult result;

    public DescriptivePatternMapping(ExtractorResult result) {
        this.result = result;
    }

    @Override
    public @Nullable Object get(String name) {
        if (result.getWildcards() == null) {
            return null;
        }

        return result.getWildcard(name)
                .map(ExtractorResultElement::getValue)
                .orElse(null);
    }

}