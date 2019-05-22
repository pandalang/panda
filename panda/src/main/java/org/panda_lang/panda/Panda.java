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

package org.panda_lang.panda;

import org.panda_lang.panda.cli.PandaCLI;
import org.panda_lang.panda.framework.language.resource.PandaLanguage;
import org.panda_lang.panda.util.embed.PandaEngineFactoryConstants;

public final class Panda {

    private final PandaCLI pandaCLI;
    private final PandaLoader pandaLoader;
    private final PandaLanguage pandaLanguage;

    protected Panda() {
        this.pandaCLI = new PandaCLI(this);
        this.pandaLoader = new PandaLoader(this);
        this.pandaLanguage = new PandaLanguage();
    }

    public String getVersion() {
        return PandaEngineFactoryConstants.VERSION;
    }

    public PandaLanguage getPandaLanguage() {
        return pandaLanguage;
    }

    public PandaLoader getPandaLoader() {
        return pandaLoader;
    }

    public PandaCLI getPandaCLI() {
        return pandaCLI;
    }

}
