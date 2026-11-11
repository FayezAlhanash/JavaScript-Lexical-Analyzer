
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;

import java.util.ArrayList;
import java.util.List;

public class LexicalErrorListener extends BaseErrorListener {

    private List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        errors.add(
                "Error at Line: " + line
                        + ", Column: " + (charPositionInLine + 1)
                        + " | " + msg
        );
    }

    public List<String> getErrors() {
        return errors;
    }
}
