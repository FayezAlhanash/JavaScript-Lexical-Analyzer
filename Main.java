import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Path path = Path.of("test1.js");
        String input = Files.readString(path);

        JavaScriptLexer lexer = new JavaScriptLexer(
                CharStreams.fromString(input)
        );
        lexer.removeErrorListeners();
        LexicalErrorListener errorListener = new LexicalErrorListener();

        lexer.addErrorListener(errorListener);
        List<? extends Token> tokens = lexer.getAllTokens();

        System.out.println("--- Tokens Found ---");
        for (Token token : tokens) {

            String tokenName = lexer.getVocabulary()
                    .getSymbolicName(token.getType());
            System.out.println(
                    "Text: " + token.getText()
                            + " -> Type: " + tokenName
                            + " | Line: " + token.getLine()
                            + " | Column: " + token.getCharPositionInLine()
            );


        }
        System.out.println("\n--- Lexical Errors: "
                + errorListener.getErrorCount() + " ---");
        for (String error : errorListener.getErrors()) {
            System.out.println(error);
        }
    }
}