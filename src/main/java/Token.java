import lombok.Getter;

@Getter
public class Token {
    public enum TokenType {
        COMMENT, WHITESPACE, IDENTIFIER, OPERATOR, SEPARATOR, INT_LITERAL, FLOAT_LITERAL, CHAR_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL, NULL_LITERAL, KEYWORD, ERROR
    }

    private TokenType type;
    private String data;

    public Token(TokenType type, String data) {
        this.type = type;
        this.data = data;
    }
}
