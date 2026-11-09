
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
                    isErrorToken(tokenName)) {

                errors.add(
                        "Line: " + token.getLine()
                                + ", Column: " + (token.getCharPositionInLine() + 1)
                                + " | " + tokenName
                                + " | " + getErrorMessage(tokenName, token.getText())
                );

            } else {

                System.out.println(
                        "Text: " + printable(token.getText())
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

    private static boolean isErrorToken(String name) {
        return name.startsWith("INVALID_")
                || name.startsWith("UNTERMINATED_");
    }

    private static String getErrorMessage(String name, String text) {

        switch (name) {

            case "UNTERMINATED_STRING":
                return "Unterminated string";

            case "INVALID_ESCAPE":
                return "Invalid escape sequence: " + printable(text);

            case "INVALID_TEMPLATE":
                return "Invalid template: " + printable(text);

            case "UNTERMINATED_TEMPLATE":
                return "Unterminated template";

            case "INVALID_NUMBER":
                return "Invalid number: " + printable(text);

            default:
                return "Invalid or unexpected token: " + printable(text);
        }
    }

    private static String printable(String text) {
        return text.replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
