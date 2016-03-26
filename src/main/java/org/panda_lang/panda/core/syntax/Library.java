package org.panda_lang.panda.core.syntax;

import org.panda_lang.panda.core.Alice;

import java.io.File;

public class Library implements NamedExecutable {

    private final String libraryPath;

    public Library(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    @Override
    public Essence run(Alice alice) {
        return null;
    }

    public File getFile() {
        return new File(libraryPath.substring(1, libraryPath.length() - 1));
    }

    @Override
    public String getName() {
        return libraryPath;
    }

}
