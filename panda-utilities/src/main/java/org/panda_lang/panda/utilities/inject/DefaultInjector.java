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

package org.panda_lang.panda.utilities.inject;

import java.lang.reflect.Method;

final class DefaultInjector implements Injector {

    private final InjectorResources resources;

    public DefaultInjector(InjectorResources resources) {
        this.resources = resources;
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        return new ConstructorInjection(this).invoke(type);
    }

    @Override
    public <T> T invokeMethod(Method method, Object instance) {
        return new MethodInjector(this).invoke(method, instance);
    }

    public InjectorResources getResources() {
        return resources;
    }

}
