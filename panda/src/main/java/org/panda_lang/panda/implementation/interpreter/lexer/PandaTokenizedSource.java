package org.panda_lang.panda.implementation.interpreter.lexer;

import org.panda_lang.core.interpreter.token.Token;
import org.panda_lang.core.interpreter.lexer.TokenizedSource;

public class PandaTokenizedSource implements TokenizedSource {

    private final Token[][] tokenizedSource;
    private final int size;

    public PandaTokenizedSource(Token[][] tokenizedSource) {
        this.tokenizedSource = tokenizedSource;
        this.size = countTokens();
    }

    private int countTokens() {
        int size = 0;

        for (Token[] tokens : tokenizedSource) {
            size += tokens.length;
        }

        return size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Token[][] getSource() {
        return tokenizedSource;
    }

}
