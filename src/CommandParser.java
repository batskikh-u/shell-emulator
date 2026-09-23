import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    public List<String> parse(String input) {
        List<String> tockens = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        char quote = 0;


        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (quote == 0) {
                if (c == '"' || c == '\'') {
                    quote = c;
                } else if (Character.isWhitespace(c)) {
                    tockens.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            } else {
                if (c == quote) {
                    quote = 0;
                } else if (c == '"' || c == '\'') {
                    throw new IllegalArgumentException(
                            "unclosed quote: expected " + quote + " but got " + c
                    );
                } else {
                    current.append(c);
                }
            }
        }

        if (quote != 0) {
            throw new IllegalArgumentException("unclosed quote: " + quote);
        }
        if (current.length() > 0) {
            tockens.add(current.toString());
        }
        return tockens;
    }
}