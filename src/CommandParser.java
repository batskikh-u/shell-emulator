import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private final List<String> tokens = new ArrayList<>();
    private final StringBuilder current = new StringBuilder();
    private char quote = 0;
    private boolean hasContent = false;

    public List<String> parse(String input) {
        reset();
        for (int i = 0; i < input.length(); i++) {
            step(input.charAt(i));
        }
        return finish();
    }

    private void reset() {
        tokens.clear();
        current.setLength(0);
        quote = 0;
        hasContent = false;
    }

    private void step(char c) {
        if (quote == 0) {
            outsideQuote(c);
        } else {
            insideQuote(c);
        }
    }

    private List<String> finish() {
        if (quote != 0) {
            throw new IllegalArgumentException("unclosed quote: " + quote);
        }
        flushToken();
        return tokens;
    }

    private void outsideQuote(char c) {
        if (isQuote(c)) {
            quote = c;
            hasContent = true;
        } else if (Character.isWhitespace(c)) {
            flushToken();
        } else {
            current.append(c);
            hasContent = true;
        }
    }

    private void insideQuote(char c) {
        if (c == quote) {
            quote = 0;
            hasContent = true;
        } else if (isQuote(c)) {
            throw new IllegalArgumentException(
                    "unclosed quote: expected " + quote + " but got " + c
            );
        } else {
            current.append(c);
            hasContent = true;
        }
    }

    private void flushToken() {
        if (hasContent) {
            tokens.add(current.toString());
            current.setLength(0);
            hasContent = false;
        }
    }

    private boolean isQuote(char c) {
        return c == '"' || c == '\'';
    }
}