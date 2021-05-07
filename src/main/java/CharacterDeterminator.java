import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CharacterDeterminator {
    private static final List<String> keywords = Arrays.asList(
            "abstract", "continue", "for", "new", "switch",
            "assert", "default", "goto", "package", "synchronized",
            "boolean", "do", "if", "private", "this",
            "break", "double", "implements", "protected", "throw",
            "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void",
            "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while"
    );

    public static boolean isSeparator(Character c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' || c == ',';
    }

    public static boolean isOperator(Character c) {
        return c == '=' || c == '>' || c == '<' || c == '!' || c == '~' || c == ':' || c == '?' || c == '&' || c == '|' || c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%';
    }

    public static boolean isSpecial(String str) {
        return "\\b".equals(str) || "\\t".equals(str) || "\\n".equals(str) || "\\".equals(str) || "'".equals(str) || "\"".equals(str) || "\\r".equals(str) || "\\f".equals(str);
    }

    public static boolean isOctal(Character c) {
        return Pattern.matches("[0-7]", c.toString());
    }

    public static boolean isBinary(Character c) {
        return c == '0' || c == '1';
    }

    public static boolean isHex(Character c) {
        return Pattern.matches("\\d|[a-fA-F]", c.toString());
    }

    public static boolean isDoubleOrFloat(Character c) {
        return c == 'f' || c == 'F' || c == 'd' || c == 'D';
    }

    public static boolean isBooleanLiteral(String str) {
        return "true".equals(str) || "false".equals(str);
    }

    public static boolean isNullLiteral(String str) {
        return "null".equals(str);
    }

    public static boolean isKeyword(String str) {
        return keywords.contains(str);
    }
}
