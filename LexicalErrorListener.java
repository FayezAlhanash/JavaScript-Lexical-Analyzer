import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import java.util.ArrayList;
import java.util.List;
public class LexicalErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {
        errorCount++;
        errors.add(
                "Error at Line: " + line
                        + ", Column: " + charPositionInLine
                        + " | " + msg
        );

    }
    private int errorCount = 0;
    public int getErrorCount() {
        return errorCount;
    }
    public List<String> getErrors() {
        return errors;
    }
    private List<String> errors = new ArrayList<>();
}
