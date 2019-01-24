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

package org.panda_lang.panda.framework.design.resource.prototypes;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.panda_lang.panda.PandaException;
import org.panda_lang.panda.framework.PandaFramework;
import org.panda_lang.panda.framework.design.architecture.module.Module;
import org.panda_lang.panda.framework.design.architecture.module.ModulePath;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototype;
import org.panda_lang.panda.framework.design.architecture.prototype.ClassPrototypeReference;
import org.panda_lang.panda.framework.design.architecture.prototype.PandaClassPrototype;
import org.panda_lang.panda.framework.design.architecture.prototype.generator.ClassPrototypeGeneratorUtils;
import org.panda_lang.panda.framework.design.architecture.prototype.method.MethodCallback;
import org.panda_lang.panda.framework.design.architecture.prototype.method.PandaMethod;
import org.panda_lang.panda.framework.design.architecture.value.Value;
import org.panda_lang.panda.framework.design.resource.prototypes.ClassPrototypeModel.ClassDeclaration;
import org.panda_lang.panda.framework.design.resource.prototypes.ClassPrototypeModel.MethodDeclaration;
import org.panda_lang.panda.framework.design.resource.prototypes.ClassPrototypeModel.ModuleDeclaration;
import org.panda_lang.panda.framework.design.runtime.ExecutableBranch;
import org.panda_lang.panda.framework.language.resource.PandaTypes;
import org.panda_lang.panda.utilities.commons.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ClassPrototypeModelLoader {

    private static final AtomicInteger idAssigner = new AtomicInteger();
    private final ModulePath modulePath;

    public ClassPrototypeModelLoader(ModulePath modulePath) {
        this.modulePath = modulePath;
    }

    public void load(Collection<Class<? extends ClassPrototypeModel>> models) {
        try {
            loadModels(models);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadModels(Collection<Class<? extends ClassPrototypeModel>> models) throws Exception {
        Collection<ClassPrototypeModelMethodRegister> methodRegisters = new ArrayList<>();
        Collection<Class<?>> loaded = new ArrayList<>();

        ClassPool pool = ClassPool.getDefault();
        CtClass objectCtClass = pool.getCtClass(Object.class.getName());
        CtClass methodCallbackCtClass = pool.get(ClassPrototypeModelMethodCallback.class.getName());
        CtClass executableBranchCtClass = pool.get(ExecutableBranch.class.getName());
        CtClass valueArrayCtClass = pool.get(Value[].class.getName());
        CtClass valueCtClass = pool.get(Value.class.getName());
        CtClass[] implementationTypes = new CtClass[]{ executableBranchCtClass, objectCtClass, valueArrayCtClass };

        for (Class<? extends ClassPrototypeModel> modelClass : models) {
            ModuleDeclaration moduleDeclaration = modelClass.getAnnotation(ModuleDeclaration.class);
            ClassDeclaration classDeclaration = modelClass.getAnnotation(ClassDeclaration.class);

            String moduleName = moduleDeclaration.value();
            Optional<Module> optionalModule = modulePath.get(moduleName);

            if (optionalModule.isPresent() && optionalModule.get().get(classDeclaration.value()).isPresent()) {
                continue;
            }

            loaded.add(modelClass);
            Module module = optionalModule.orElseGet(() -> modulePath.create(moduleName));

            ClassPrototype prototype = PandaClassPrototype.builder()
                    .module(module)
                    .name(classDeclaration.value())
                    .associated(modelClass)
                    .build();

            module.add(prototype.getReference());
            Class.forName(modelClass.getName());

            for (Method method : modelClass.getMethods()) {
                MethodDeclaration methodDeclaration = method.getAnnotation(MethodDeclaration.class);

                if (methodDeclaration == null) {
                    continue;
                }

                String methodCallbackClassName = modelClass.getSimpleName() + StringUtils.capitalize(method.getName()) + "MethodCallback" + idAssigner.incrementAndGet();
                CtClass generatedMethodCallbackClass = pool.getOrNull(methodCallbackClassName);

                if (generatedMethodCallbackClass != null) {
                    continue;
                }

                generatedMethodCallbackClass = pool.makeClass(methodCallbackClassName);
                generatedMethodCallbackClass.setSuperclass(methodCallbackCtClass);

                CtMethod callbackImplementation = new CtMethod(CtClass.voidType, "invoke", implementationTypes, generatedMethodCallbackClass);
                boolean array = method.getParameters()[method.getParameters().length - 1].getType().isArray();
                StringBuilder values = new StringBuilder();
                int valuesCount = 0;

                for (int i = 2; i < method.getParameters().length; i++) {
                    values.append(",");

                    if (array && i == method.getParameterCount() - 1) {
                        values.append("values");
                        break;
                    }

                    values.append("(").append(Value.class.getName()).append(")");
                    values.append("$3[").append(i - 2).append("]");
                    valuesCount++;
                }

                String instanceType = method.getParameters()[1].getType().getName();
                StringBuilder bodyBuilder = new StringBuilder("{");

                if (array) {
                    bodyBuilder.append(String.format("%s[] values = new %s[$3.length - %d];", valueCtClass.getName(), valueCtClass.getName(), valuesCount));
                    bodyBuilder.append("for (int i = 0; i < values.length; i++) {");
                    bodyBuilder.append(String.format("values[i] = $3[%d + i];", valuesCount));
                    bodyBuilder.append("}");
                }

                if (methodDeclaration.isStatic()) {
                    bodyBuilder.append(String.format("%s.%s($1, (%s) null %s);", modelClass.getName(), method.getName(), instanceType, values.toString()));
                }
                else {
                    // TODO: Improve prototype mapping concept
                    bodyBuilder.append(String.format("%s typedInstance = (%s) $2;", modelClass.getName(), modelClass.getName()));
                    bodyBuilder.append(String.format("typedInstance.%s($1, (%s) $2 %s);", method.getName(), instanceType, values.toString()));
                }
                bodyBuilder.append("}");

                callbackImplementation.setBody(bodyBuilder.toString());
                generatedMethodCallbackClass.addMethod(callbackImplementation);
                Class<?> methodCallbackClass = generatedMethodCallbackClass.toClass();

                if (!MethodCallback.class.isAssignableFrom(methodCallbackClass)) {
                    throw new PandaException("Cannot load prototype, internal error - generated class is not MethodCallback");
                }

                MethodCallback<?> methodCallback = (MethodCallback<?>) methodCallbackClass.newInstance();
                ClassPrototypeReference[] parameterTypes = ClassPrototypeGeneratorUtils.toTypes(module, method.getParameterTypes());

                methodRegisters.add(() -> {
                    PandaMethod pandaMethod = PandaMethod.builder()
                            .methodName(method.getName())
                            .prototype(prototype.getReference())
                            .returnType(PandaTypes.VOID.getReference()) // TODO: Proxy or sth
                            .isStatic(methodDeclaration.isStatic())
                            .visibility(methodDeclaration.visibility())
                            .methodBody(methodCallback)
                            .parameterTypes(parameterTypes)
                            .catchAllParameters(methodDeclaration.catchAllParameters())
                            .build();

                    prototype.getMethods().registerMethod(pandaMethod);
                });
            }
        }

        for (ClassPrototypeModelMethodRegister methodRegister : methodRegisters) {
            methodRegister.register();
        }

        PandaFramework.getLogger().debug("Loaded models: " + loaded.stream().map(Class::getSimpleName).collect(Collectors.toList()).toString());
    }

}
