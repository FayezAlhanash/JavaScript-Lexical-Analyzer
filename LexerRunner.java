import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LexerRunner {

    public static void run(String fileName) throws IOException {

        String input = Files.readString(Path.of(fileName));

        JavaScriptLexer lexer = new JavaScriptLexer(
                CharStreams.fromString(input)
        );

        lexer.removeErrorListeners();

        LexicalErrorListener errorListener =
                new LexicalErrorListener();

        lexer.addErrorListener(errorListener);

        List<? extends Token> tokens = lexer.getAllTokens();

        List<String> errors = new ArrayList<>();

        System.out.println("--- Valid Tokens ---");

        for (Token token : tokens) {

            String tokenName = lexer.getVocabulary()
                    .getSymbolicName(token.getType());

            if (tokenName != null &&
                    (tokenName.startsWith("INVALID_") ||
                            tokenName.startsWith("UNTERMINATED_"))) {

                errors.add(
                        "Line: " + token.getLine()
                                + ", Column: " + token.getCharPositionInLine()
                                + " | " + tokenName
                                + " | Text: " + token.getText()
                );

            } else {

                System.out.println(
                        "Text: " + token.getText()
                                + " -> Type: " + tokenName
                                + " | Line: " + token.getLine()
                                + " | Column: " + token.getCharPositionInLine()
                );
            }
        }

        errors.addAll(errorListener.getErrors());

        System.out.println("\n--- Lexical Errors: "
                + errors.size() + " ---");

        for (String error : errors) {
            System.out.println(error);
        }
    }
}