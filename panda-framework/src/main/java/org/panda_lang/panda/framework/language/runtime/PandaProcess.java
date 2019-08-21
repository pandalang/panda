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

package org.panda_lang.panda.framework.language.runtime;

import org.panda_lang.panda.framework.design.architecture.Application;
import org.panda_lang.panda.framework.design.architecture.dynamic.ScopeFrame;
import org.panda_lang.panda.framework.design.architecture.statement.Scope;
import org.panda_lang.panda.framework.design.runtime.Process;
import org.panda_lang.panda.framework.design.runtime.flow.Flow;

public class PandaProcess implements Process {

    private final Application application;
    private final Scope mainScope;
    private final String[] parameters;

    public PandaProcess(Application application, Scope mainScope, String... parameters) {
        this.application = application;
        this.mainScope = mainScope;
        this.parameters = parameters;
    }

    @Override
    public <T> T execute() {
        ScopeFrame instance = mainScope.createFrame(null); // TODO: check behaviour of branch after applying the 'null' value

        Flow flow = new PandaFlow(this, null, instance);
        flow.call();

        return flow.getReturnedValue();
    }

    public String[] getParameters() {
        return parameters;
    }

    public Application getApplication() {
        return application;
    }

}
