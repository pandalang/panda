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

package org.panda_lang.panda.manager;

import org.panda_lang.framework.design.interpreter.source.Source;
import org.panda_lang.framework.language.interpreter.source.PandaURLSource;
import org.panda_lang.panda.Panda;
import org.panda_lang.panda.PandaFactory;
import org.panda_lang.panda.language.architecture.PandaEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

final class Run {

    private final ModuleManager manager;
    private final ModuleDocument document;

    Run(ModuleManager manager, ModuleDocument document) {
        this.manager = manager;
        this.document = document;
    }

    protected void run() throws IOException {
        File mainScript = new File(document.getDocument().getParentFile(), Objects.requireNonNull(document.getMainScript()));

        PandaFactory factory = new PandaFactory();
        Panda panda = factory.createPanda(manager.getMessenger().getLogger());

        PandaEnvironment environment = new PandaEnvironment(panda, manager.getWorkingDirectory());
        environment.initialize();

        File modulesDirectory = document.getModulesDirectory();
        File[] scopes = modulesDirectory.listFiles();

        if (scopes != null) {
            for (File scope : scopes) {
                load(environment, scope);
            }
        }

        environment.getInterpreter()
                .interpret(PandaURLSource.fromFile(mainScript))
                .ifPresent(application -> application.launch());
    }

    private void load(PandaEnvironment environment, File scopeDirectory) throws IOException {
        for (File moduleDirectory : Objects.requireNonNull(scopeDirectory.listFiles())) {
            ModuleDocument moduleInfo = new ModuleDocumentFile(new File(moduleDirectory, "panda.hjson")).getContent();

            environment.getModulePath().include(moduleDirectory.getName(), () -> {
                Source source = PandaURLSource.fromFile(new File(moduleDirectory, Objects.requireNonNull(moduleInfo.getMainScript())));
                environment.getInterpreter().interpret(source);
            });
        }
    }

}
