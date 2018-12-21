package org.panda_lang.panda.framework.design.interpreter.pattern.token.extractor;

import org.panda_lang.panda.framework.design.interpreter.token.stream.SourceStream;
import org.panda_lang.panda.framework.design.interpreter.pattern.lexical.elements.LexicalPatternElement;
import org.panda_lang.panda.framework.design.interpreter.pattern.token.TokenPattern;
import org.panda_lang.panda.framework.language.interpreter.token.distributors.TokenDistributor;

class ExtractorWorker {

    protected final TokenPattern pattern;
    protected final UnitExtractor unitExtractor;
    protected final WildcardExtractor wildcardExtractor;
    protected final VariantExtractor variantExtractor;
    protected final NodeExtractor nodeExtractor;

    ExtractorWorker(TokenPattern pattern) {
        this.pattern = pattern;
        this.unitExtractor = new UnitExtractor(this);
        this.wildcardExtractor = new WildcardExtractor(this);
        this.variantExtractor = new VariantExtractor(this);
        this.nodeExtractor = new NodeExtractor(this);
    }

    protected ExtractorResult extract(SourceStream source) {
        TokenDistributor distributor = new TokenDistributor(source.toTokenizedSource());
        ExtractorResult result = extract(distributor, pattern.getPatternContent());

        if (result.isMatched()) {
            source.read(distributor.getIndex());
        }

        return result;
    }

    protected ExtractorResult extract(TokenDistributor distributor, LexicalPatternElement element) {
        if (element.isUnit()) {
            return unitExtractor.extract(element.toUnit(), distributor);
        }

        if (element.isWildcard()) {
            return wildcardExtractor.extract(element.toWildcard(), distributor);
        }

        if (element.isVariant()) {
            return variantExtractor.extract(element.toNode(), distributor);
        }

        if (element.isNode()) {
            return nodeExtractor.extract(element.toNode(), distributor);
        }

        return new ExtractorResult("Unknown element: " + element);
    }

}