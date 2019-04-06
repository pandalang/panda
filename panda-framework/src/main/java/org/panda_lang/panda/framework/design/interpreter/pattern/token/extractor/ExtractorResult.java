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

package org.panda_lang.panda.framework.design.interpreter.pattern.token.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtractorResult {

    private final String errorMessage;
    private final List<String> identifiers;
    private final List<ExtractorResultElement> wildcards;

    public ExtractorResult() {
        this.errorMessage = null;
        this.identifiers = new ArrayList<>();
        this.wildcards = new ArrayList<>();
    }

    public ExtractorResult(String errorMessage) {
        this.errorMessage = errorMessage;
        this.identifiers = null;
        this.wildcards = null;
    }

    public ExtractorResult merge(ExtractorResult otherResult) {
        if (!otherResult.isMatched() || !isMatched()) {
            throw new RuntimeException("Cannot merge unmatched result");
        }

        getIdentifiers().addAll(otherResult.getIdentifiers());
        getWildcards().addAll(otherResult.getWildcards());
        return this;
    }

    public ExtractorResult exclude(ExtractorResult otherResult) {
        if (!isMatched() || !otherResult.isMatched()) {
            throw new RuntimeException("Cannot merge unmatched result");
        }

        getIdentifiers().removeAll(otherResult.getIdentifiers());
        getWildcards().removeAll(otherResult.getWildcards());
        return this;
    }

    public ExtractorResult identified(String identifier) {
        if (!isMatched() || identifier == null) {
            return this;
        }

        getIdentifiers().add(identifier);
        return this;
    }

    public ExtractorResult addWildcard(String name, Object wildcardContent) {
        getWildcards().add(new ExtractorResultElement(name, wildcardContent));
        return this;
    }

    public boolean hasIdentifier(String identifier) {
        return getIdentifiers().contains(identifier);
    }

    public boolean hasErrorMessage() {
        return errorMessage != null;
    }

    public boolean isMatched() {
        return errorMessage == null;
    }

    public Optional<ExtractorResultElement> getWildcard(String name) {
        return getWildcards().stream()
                .filter(element -> element.getName().equals(name))
                .findFirst();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<ExtractorResultElement> getWildcards() {
        return wildcards;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

}
