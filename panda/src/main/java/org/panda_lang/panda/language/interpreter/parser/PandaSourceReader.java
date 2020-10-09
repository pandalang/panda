package org.panda_lang.panda.language.interpreter.parser;

import org.panda_lang.language.interpreter.parser.Context;
import org.panda_lang.language.interpreter.parser.PandaParserFailure;
import org.panda_lang.language.interpreter.parser.SourceReader;
import org.panda_lang.language.interpreter.parser.expression.ExpressionParser;
import org.panda_lang.language.interpreter.parser.expression.ExpressionTransaction;
import org.panda_lang.language.interpreter.token.PandaSnippet;
import org.panda_lang.language.interpreter.token.PandaSourceStream;
import org.panda_lang.language.interpreter.token.Snippet;
import org.panda_lang.language.interpreter.token.SourceStream;
import org.panda_lang.language.interpreter.token.TokenInfo;
import org.panda_lang.language.resource.syntax.TokenTypes;
import org.panda_lang.language.resource.syntax.auxiliary.Section;
import org.panda_lang.language.resource.syntax.operator.Operators;
import org.panda_lang.language.resource.syntax.separator.Separators;
import org.panda_lang.panda.language.resource.syntax.type.SignatureSource;
import org.panda_lang.utilities.commons.function.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PandaSourceReader extends SourceReader {

    public PandaSourceReader(SourceStream stream) {
        super(stream);
    }

    public Option<Snippet> readBody() {
        return readSection(Separators.BRACE_LEFT)
                .map(token -> token.toToken(Section.class).getContent());
    }

    public Option<Snippet> readArguments() {
        return readSection(Separators.PARENTHESIS_LEFT)
                .map(token -> token.toToken(Section.class).getContent());
    }

    public Option<SignatureSource> readSignature() {
        return read(TokenTypes.UNKNOWN)
                .flatMap(name -> readGenerics()
                    .map(section -> readSignatures(section.getContent()))
                    .orElse(Collections.emptyList())
                    .map(generics -> new SignatureSource(name, generics))
                );
    }

    private List<SignatureSource> readSignatures(Snippet source) {
        Snippet[] sources = source.split(Separators.COMMA);
        List<SignatureSource> signatures = new ArrayList<>((source.size() / 3) + 1);

        for (Snippet signatureSource : sources) {
            SourceStream signatureSourceStream = new PandaSourceStream(signatureSource);
            PandaSourceReader signatureReader = new PandaSourceReader(signatureSourceStream);

            signatures.add(signatureReader.readSignature().orThrow(() -> {
                throw new PandaParserFailure(source, signatureSource, "Invalid signature");
            }));
        }

        return signatures;
    }

    public Option<Section> readGenerics() {
        return readBetween(Operators.ANGLE_LEFT, Operators.ANGLE_RIGHT);
    }

    public Option<Snippet> readPandaQualifier() {
        List<TokenInfo> tokens = new ArrayList<>();

        while (super.stream.hasUnreadSource()) {
            Option<TokenInfo> result = optionalRead(() ->
                    read(read -> read.getType() == TokenTypes.UNKNOWN
                        || read.contentEquals(Separators.COMMA)
                        || read.contentEquals(Operators.SUBTRACTION)
                        || read.contentEquals(Operators.COLON))
                    .peek(tokens::add));

            if (result.isEmpty()) {
                break;
            }
        }

        if (tokens.isEmpty()) {
            return Option.none();
        }

        return Option.of(PandaSnippet.ofImmutable(tokens));
    }

    public List<ExpressionTransaction> readExpressions(Context<?> context) {
        ExpressionParser parser = context.getExpressionParser();
        List<ExpressionTransaction> transactions = new ArrayList<>();

        while (super.stream.hasUnreadSource()) {
            Option<ExpressionTransaction> argument = optionalRead(() -> parser.parseSilently(context, super.stream));

            if (argument.isEmpty()) {
                break;
            }

            transactions.add(argument.get());

            if (super.stream.hasUnreadSource()) {
                if (!super.stream.getNext().equals(Separators.COMMA)) {
                    break;
                }

                super.stream.read();
            }
        }

        return transactions;
    }

}