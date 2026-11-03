import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String input = "let hello 12345 if else ddddd";

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