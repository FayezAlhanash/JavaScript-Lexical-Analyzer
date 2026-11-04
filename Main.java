import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String input = "let x 42 1_000 3.14 5. .5 1e5 0xFF 0o17 0b101 123n 0xFFn 12px 1e if 123abc 3.12as";

        JavaScriptLexer lexer = new JavaScriptLexer(
                CharStreams.fromString(input)
        );

        List<? extends Token> tokens = lexer.getAllTokens();

        System.out.println("---- Tokens Found ----");

        for (Token token : tokens) {

            String tokenName = lexer.getVocabulary()
                    .getSymbolicName(token.getType());

            System.out.println(
                    "Text: " + token.getText()
                            + " -> Type: " + tokenName
            );
        }
    }
}