package lexer;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Takes an input ABC string and converts it into a list of tokens.
 */
public class Lexer {
    public static List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<Token>();
        while (!input.isEmpty()) {
            input = input.trim();
            Matcher commentMatcher = commentPattern.matcher(input);
            if (commentMatcher.find()) {
                // If a comment is encountered, remove from string and ignore data
                input = input.substring(commentMatcher.end(0));
                continue;
            }

            boolean matchFound = false;

            // Iterate over patterns that don't have values
            for (Map.Entry<Pattern, Token.Type> entry : patternsNoArgs.entrySet()) {
                Pattern pattern = entry.getKey();
                Token.Type type = entry.getValue();

                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    input = input.substring(matcher.end(0));
                    tokens.add(new Token(type));
                    matchFound = true;
                    break;
                }
            }

            // Return to beginning of loop if a withArgs pattern was matched
            if (matchFound) {
                continue;
            }
            matchFound = false;

            // Iterate over patterns that have values
            for (Map.Entry<Pattern, Token.Type> entry : patternsWithArgs.entrySet()) {
                Pattern pattern = entry.getKey();
                Token.Type type = entry.getValue();

                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    matchFound = true;
                    input = input.substring(matcher.end(0));
                    tokens.add(new Token(type, matcher.group(1)));
                    break;
                }
            }

            if (!matchFound) {
                throw new RuntimeException(String.format("Invalid token encountered: %s\nLexed so far: %s", input, tokens));
            }
        }
        return tokens;
    }

    /**
     * Wraps the given regex pattern in whitespace character classes, so that the pattern is matched even when surrounded by whitespace.
     * @param pattern The pattern to wrap.
     * @return The pattern wrapped with whitespace patterns.
     */
    private static String wrapWhitespace(String pattern) {
        return "\\s*" + pattern + "\\s*";
    }

    // Using Pattern.MULTILINE causes the $ character to match newline characters.
    private static Pattern commentPattern = Pattern.compile("\\A%.*$", Pattern.MULTILINE);

    // maps token types that have no value to their corresponding regex patterns
    private static Map<Pattern, Token.Type> patternsNoArgs
        = new LinkedHashMap<Pattern, Token.Type>();

    // maps token types that have a value to their corresponding regex patterns
    // By using a LinkedHashMap, regex patterns will be checked in the order they are added
    private static Map<Pattern, Token.Type> patternsWithArgs
        = new LinkedHashMap<Pattern, Token.Type>();

    /**
     * Maps a token type that takes no value to a regex string.
     *
     * @param regex The regular expression string.
     * @param type The token type
     */
    private static void noArgs(String regex, Token.Type type) {
        // Using Pattern.MULTILINE causes the $ character to match newline characters.
        // Using \\A causes regex to only match at the begining of input
        patternsNoArgs.put(Pattern.compile("\\A" + regex, Pattern.MULTILINE),
                           type);
    }

    /**
     * Maps a token type that takes a value to a regex string.
     *
     * @param regex The regular expression string.
     * @param type The token type
     */
    private static void withArgs(String regex, Token.Type type) {
        // Using Pattern.MULTILINE causes the $ character to match newline characters.
        // Using \\A causes regex to only match at beginning of input
        patternsWithArgs.put(Pattern.compile("\\A" + regex, Pattern.MULTILINE),
                             type);
    }

    // initialize the regex -> token type maps.
    // regexes listed first have a higher priority
    static {
        withArgs("X:\\s*(\\d+)\\s*$",
                 Token.Type.NumberField);
        
        withArgs("T:\\s*(.*)\\s*$",
                 Token.Type.TitleField);

        withArgs("C:\\s*(.*)\\s*$",
                 Token.Type.ComposerField);

        withArgs("M:\\s*(C)\\S*$",
                 Token.Type.MeterField);

        withArgs("M:\\s*(C\\|)\\S*$",
                 Token.Type.MeterField);

        withArgs("M:\\s*(\\d+/\\d+)\\S*$",
                 Token.Type.MeterField);

        withArgs("K:\\s*([a-gA-G][#b]?m?)\\s*$",
                 Token.Type.KeyField);

        withArgs("L:\\s*(\\d+/\\d+)\\s*$",
                 Token.Type.DefaultLengthField);

        withArgs("Q:\\s*(\\d+)\\s*$",
                 Token.Type.TempoField);
        
        withArgs("V:\\s*(.*?)\\s*$",
                 Token.Type.VoiceField);
        
        withArgs(wrapWhitespace("([a-gA-G])"),
                 Token.Type.BaseNote);

        // a / b
        withArgs(wrapWhitespace("(\\d+/\\d+)"),
                 Token.Type.NoteLength);
        
        //a /
        withArgs(wrapWhitespace("(\\d+/)"),
                Token.Type.NoteLength);

        // a
        withArgs(wrapWhitespace("(\\d+)"),
                 Token.Type.NoteLength);

        // / b
        withArgs(wrapWhitespace("(/\\d+)"),
                 Token.Type.NoteLength);

        // /
        withArgs(wrapWhitespace("(/)"),
                 Token.Type.NoteLength);
        
        noArgs(wrapWhitespace("\\^"),
               Token.Type.Sharp);

        noArgs(wrapWhitespace("\\^\\^"),
               Token.Type.DoubleSharp);

        noArgs(wrapWhitespace("_"),
               Token.Type.Flat);

        noArgs(wrapWhitespace("__"),
               Token.Type.DoubleFlat);

        noArgs(wrapWhitespace("="),
               Token.Type.Natural);

        noArgs(wrapWhitespace("z"),
               Token.Type.Rest);

        noArgs(wrapWhitespace("'"),
               Token.Type.OctaveUp);
        
        noArgs(wrapWhitespace(","),
               Token.Type.OctaveDown);

        noArgs(wrapWhitespace("\\(2"),
               Token.Type.BeginDoublet);

        noArgs(wrapWhitespace("\\(3"),
               Token.Type.BeginTriplet);

        noArgs(wrapWhitespace("\\(4"),
               Token.Type.BeginQuadruplet);

        noArgs(wrapWhitespace("\\[1"),
               Token.Type.FirstRepeat);

        noArgs(wrapWhitespace("\\[2"),
               Token.Type.SecondRepeat);    

        noArgs(wrapWhitespace("\\[\\|"),
               Token.Type.BeginMajorSection);

        noArgs(wrapWhitespace("\\["),
               Token.Type.BeginChord);

        noArgs(wrapWhitespace("\\]"),
               Token.Type.EndChord);

        noArgs(wrapWhitespace("\\|\\]"),
               Token.Type.EndMajorSection);

        noArgs(wrapWhitespace("\\|:"),
               Token.Type.BeginRepeat);

        noArgs(wrapWhitespace(":\\|"),
               Token.Type.EndRepeat);
                
        noArgs(wrapWhitespace("\\|"),
               Token.Type.BarLine);

        noArgs(wrapWhitespace("\\|\\|"),
               Token.Type.BarLine);
    }
}
