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

package org.panda_lang.panda.bootstrap;

import org.panda_lang.panda.Panda;
import org.panda_lang.panda.framework.design.architecture.Application;

import java.io.File;
import java.util.Optional;

public class PandaApplicationBootstrap {

    protected final Panda panda;
    protected String main;
    protected File workingDirectory;

    public PandaApplicationBootstrap(Panda panda) {
        this.panda = panda;
    }

    public PandaApplicationBootstrap main(String sourcePath) {
        this.main = sourcePath;
        return this;
    }

    public PandaApplicationBootstrap workingDirectory(String workingDirectory) {
        return workingDirectory(new File(workingDirectory));
    }

    public PandaApplicationBootstrap workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public Optional<Application> createApplication() {
        return panda.getLoader().load(main, workingDirectory);
    }

}
