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

package org.panda_lang.framework.language.resource.syntax.operator;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.framework.design.interpreter.token.TokenType;
import org.panda_lang.framework.language.interpreter.token.EqualableToken;
import org.panda_lang.framework.language.resource.syntax.TokenTypes;
import org.panda_lang.utilities.commons.StringUtils;

public final class Operator extends EqualableToken {

    private final String family;
    private final String operator;

    public Operator(@Nullable String family, String operator) {
        this.family = family;
        this.operator = operator;
    }

    public boolean hasFamily() {
        return !StringUtils.isEmpty(family);
    }

    public String getFamily() {
        return family;
    }

    @Override
    public String getValue() {
        return operator;
    }

    @Override
    public TokenType getType() {
        return TokenTypes.OPERATOR;
    }

}
