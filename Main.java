import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Phase 1 driver: reads a JavaScript file, prints every token and every lexical error.
 *
 * Usage:  java Main test.js
 */
public class Main {

    /** true -> positions are printed as line:column, false -> column:line */
    private static final boolean LINE_FIRST = false;

    private static final String BS = "\\";

    private static final class LexError {
        final int line;
        final int col;
        final String message;

        LexError(int line, int col, String message) {
            this.line = line;
            this.col = col;
            this.message = message;
        }
    }

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");

        if (args.length < 1) {
            out.println("Usage: java Main <file.js>");
            return;
        }

        CharStream input;
        try {
            input = CharStreams.fromPath(Paths.get(args[0]), StandardCharsets.UTF_8);
        } catch (IOException e) {
            out.println("Cannot read file: " + args[0]);
            return;
        }

        JavaScriptLexer lexer = new JavaScriptLexer(input);
        final List<LexError> errors = new ArrayList<>();

        // Characters that no lexer rule can match (ANTLR's "token recognition error").
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                String bad = "?";
                if (e instanceof LexerNoViableAltException) {
                    int start = ((LexerNoViableAltException) e).getStartIndex();
                    CharStream cs = ((Lexer) recognizer).getInputStream();
                    int stop = Math.max(start, Math.min(cs.index(), cs.size() - 1));
                    bad = cs.getText(Interval.of(start, stop));
                }
                errors.add(new LexError(line, charPositionInLine + 1,
                        "Invalid or unexpected token '" + printable(bad) + "'"));
            }
        });

        List<Token> good = new ArrayList<>();
        for (Token t = lexer.nextToken(); t.getType() != Token.EOF; t = lexer.nextToken()) {
            String name = lexer.getVocabulary().getSymbolicName(t.getType());
            if (name == null) {
                name = "T" + t.getType();
            }
            if (!isErrorToken(name)) {
                good.add(t);
            } else {
                classifyError(name, t, errors);
            }
        }

        out.println("--- Tokens Found ---");
        for (Token t : good) {
            out.println("Text: [" + printable(t.getText()) + "] -> Type: "
                    + lexer.getVocabulary().getSymbolicName(t.getType())
                    + " (" + pos(t.getLine(), t.getCharPositionInLine() + 1) + ")");
        }

        errors.sort(Comparator.<LexError>comparingInt(e -> e.line).thenComparingInt(e -> e.col));

        out.println();
        out.println("--- Lexical Errors: " + errors.size() + " ---");
        for (LexError e : errors) {
            out.println("Error at " + pos(e.line, e.col) + " " + e.message);
        }

        out.println();
        if (errors.isEmpty()) {
            out.println("Result: the input is valid (no lexical errors).");
        } else {
            out.println("Result: the input is NOT valid - " + errors.size() + " lexical error(s) found.");
        }
    }

    private static boolean isErrorToken(String name) {
        return name.startsWith("INVALID_") || name.startsWith("UNTERMINATED_");
    }

    private static void classifyError(String name, Token t, List<LexError> errors) {
        int line = t.getLine();
        int col = t.getCharPositionInLine() + 1;
        String text = t.getText();

        switch (name) {
            case "UNTERMINATED_STRING":
                errors.add(new LexError(line, col, "Unterminated string"));
                break;
            case "INVALID_ESCAPE":
                checkEscapes(t, errors);
                break;
            case "INVALID_TEMPLATE":
                errors.add(new LexError(line, col, "Unclosed '${' in template"));
                break;
            case "INVALID_NUMBER":
                errors.add(new LexError(line, col, "Invalid number '" + printable(text) + "'"));
                break;
            default:
                errors.add(new LexError(line, col,
                        "Invalid or unexpected token '" + printable(text) + "'"));
        }
    }

    private static void checkEscapes(Token t, List<LexError> errors) {
        int[] cp = t.getText().codePoints().toArray();
        int n = cp.length;

        int[] lineAt = new int[n + 1];
        int[] colAt = new int[n + 1];
        int line = t.getLine();
        int col = t.getCharPositionInLine() + 1;
        for (int k = 0; k < n; k++) {
            lineAt[k] = line;
            colAt[k] = col;
            if (cp[k] == '\n') {
                line++;
                col = 1;} else { col++;}}
        int before = errors.size();
        int quote = cp[0];
        boolean closed = false;
        int k = 1;

        while (k < n) {
            int c = cp[k];
            if (c == quote) {
                closed = true;
                break;}
            if (c != '\\') {
                k++;
                continue;}
            if (k + 1 >= n) {
                break;}
            int e = cp[k + 1];

            if (e == 'x') {
                if (isHex(cp, k + 2) && isHex(cp, k + 3)) {
                    k += 4;} else {
                    errors.add(new LexError(lineAt[k], colAt[k],
                            "Invalid hex escape " + snippet(cp, k, 4)));
                    k += 2;}
                continue;}

            if (e == 'u') {
                if (k + 2 < n && cp[k + 2] == '{') {
                    int j = k + 3;
                    long value = 0;
                    int digits = 0;
                    while (isHex(cp, j)) {
                        value = Math.min(value * 16 + hexValue(cp[j]), 0xFFFFFFFFL);
                        digits++;
                        j++;}
                    if (digits == 0 || j >= n || cp[j] != '}') {
                        errors.add(new LexError(lineAt[k], colAt[k],
                                "Invalid unicode escape " + snippet(cp, k, Math.max(j - k + 1, 4))));
                        k += 2;
                    } else if (value > 0x10FFFF) {
                        errors.add(new LexError(lineAt[k], colAt[k],
                                "Unicode code point out of range " + snippet(cp, k, j - k + 1)));
                        k = j + 1;
                    } else {
                        k = j + 1;
                    }
                } else if (isHex(cp, k + 2) && isHex(cp, k + 3) && isHex(cp, k + 4) && isHex(cp, k + 5)) {
                    k += 6;
                } else {
                    errors.add(new LexError(lineAt[k], colAt[k],
                            "Invalid unicode escape " + snippet(cp, k, 6)));
                    k += 2;}
                continue;}
            k += 2;}

        if (errors.size() == before && closed) {
            // safety net: should not happen, but never lose an error silently
            errors.add(new LexError(t.getLine(), t.getCharPositionInLine() + 1,
                    "Invalid escape sequence"));
        }
        if (!closed) {
            errors.add(new LexError(t.getLine(), t.getCharPositionInLine() + 1, "Unterminated string"));
        }
    }

    private static boolean isHex(int[] cp, int i) {
        if (i < 0 || i >= cp.length) {
            return false;
        }
        int c = cp[i];
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static int hexValue(int c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        return c - 'A' + 10;
    }

    private static String snippet(int[] cp, int from, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < cp.length && i < from + len; i++) {
            int c = cp[i];
            if (i > from && (c == '"' || c == '\'' || Character.isWhitespace(c))) {
                break;
            }
            sb.appendCodePoint(c);
        }
        return sb.toString();
    }

    private static String pos(int line, int col) {
        return LINE_FIRST ? (line + ":" + col) : (col + ":" + line);
    }
    private static String printable(String s) {
        return s.replace("\r", BS + "r").replace("\n", BS + "n").replace("\t", BS + "t");
    }
}