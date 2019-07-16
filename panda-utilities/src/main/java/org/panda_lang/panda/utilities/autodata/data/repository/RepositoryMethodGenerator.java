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

package org.panda_lang.panda.utilities.autodata.data.repository;

import org.panda_lang.panda.utilities.autodata.AutomatedDataException;
import org.panda_lang.panda.utilities.autodata.data.collection.CollectionScheme;
import org.panda_lang.panda.utilities.autodata.data.entity.EntityScheme;
import org.panda_lang.panda.utilities.commons.ArrayUtils;
import org.panda_lang.panda.utilities.commons.function.ThrowingConsumer;
import org.panda_lang.panda.utilities.commons.function.ThrowingFunction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

final class RepositoryMethodGenerator {

    private static final String CAMEL_CASE_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

    protected RepositoryGeneratorFunction generateMethod(DataController<?> controller, CollectionScheme collectionScheme, Method method) {
        List<String> elements = Arrays.stream(method.getName().split(CAMEL_CASE_PATTERN))
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        RepositoryOperationType operation = RepositoryOperationType.of(elements.get(0));

        if (operation == null) {
            throw new AutomatedDataException("Unknown operation: '" + operation + "' (source: " + method.toGenericString() + ")");
        }

        return new RepositoryGeneratorFunction(operation, generate(controller, collectionScheme, operation, elements.subList(1, elements.size())));
    }

    private MethodFunction generate(DataController<?> controller, CollectionScheme scheme, RepositoryOperationType operation, List<String> specification) {
        DataHandler handler = controller.getHandler(scheme.getName());

        switch (operation) {
            case CREATE:
                return createFunction(handler, scheme.getEntityScheme());
            case DELETE:
                return deleteFunction(handler);
            case UPDATE:
                return updateFunction(handler);
            case FIND:
                return findFunction(handler, specification);
            default:
                throw new AutomatedDataException("Unsupported operation: " + operation);
        }
    }

    private MethodFunction createFunction(DataHandler handler, EntityScheme entityScheme) {
        return handler::create;
    }

    @SuppressWarnings("unchecked")
    private MethodFunction deleteFunction(DataHandler handler) {
        return parameters -> {
            ArrayUtils.forEachThrowing(parameters, (ThrowingConsumer<Object, Exception>) handler::delete);
            return null;
        };
    }

    private MethodFunction updateFunction(DataHandler handler) {
        return parameters -> null;
    }

    private MethodFunction findFunction(DataHandler handler, List<String> specification) {
        return parameters -> null;
    }

    private interface MethodFunction extends ThrowingFunction<Object[], Object, Exception> { }

}
