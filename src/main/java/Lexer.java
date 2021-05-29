import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private StringBuilder buffer = new StringBuilder();
    private State state = State.INITIAL_STATE;
    private List<Token> tokens = new ArrayList<Token>();

    public List<Token> getTokens(Reader reader) throws IOException {

        int r;
        while((r = reader.read()) != -1) {
            Character c = (char) r;
            processChar(c);
        }

        processChar(' ');

        return tokens;
    }

    private void processChar(Character c) {
        buffer.append(c);
        switch (state) {
            case INVALID_STATE:
                invalidState(c);
                break;
            case INITIAL_STATE:
                initialState(c);
                break;
            case SLASH:
                slashState(c);
                break;
            case JAVA_IDENTIFIER:
                javaIdentifierState(c);
                break;
            case ZERO_FIRST:
                zeroFirstState(c);
                break;
            case NON_ZERO_DIGIT:
                nonZeroDigitState(c);
                break;
            case CHAR_LITERAL:
                charLiteralState(c);
                break;
            case STRING_LITERAL:
                stringLiteralState(c);
                break;
            case DOT:
                dotState(c);
                break;
            case GREATER:
                greaterState(c);
                break;
            case LESS:
                lessState(c);
                break;
            case AMPERSAND:
                ampersandState(c);
                break;
            case SINGLE_OPERATOR:
                singleOperatorState(c);
                break;
            case COLON:
                colonState(c);
                break;
            case PLUS:
                plusState(c);
                break;
            case MINUS:
                minusState(c);
                break;
            case PIPE:
                pipeState(c);
                break;
            case SINGLE_LINE_COMMENT:
                singleLineCommentState(c);
                break;
            case MULTI_LINE_COMMENT:
                multilineCommentState(c);
                break;
            case OCTAL_DIGITS:
                octalDigitState(c);
                break;
            case BINARY_DIGITS:
                binaryDigitState(c);
                break;
            case HEX_DIGITS:
                hexDigitState(c);
                break;
            case INTEGER_SUFIX:
                integerSufixState(c);
                break;
            case POINT_IN_DIGIT:
                pointInDigitState(c);
                break;
            case POSSIBLE_ESCAPE_SEQUENCE_CHAR:
                possibleEscapeSequenceCharState(c);
                break;
            case EXPECT_END_OF_CHAR:
                expectEndOfCharState(c);
                break;
            case POSSIBLE_ESCAPE_SEQUENCE:
                possibleEscapeSequenceState(c);
                break;
            case DOUBLE_GREATER:
                doubleGreaterState(c);
                break;
            case OPERATOR_AND_EQUAL:
                operatorAndEqualState(c);
                break;
            case STAR_IN_MULTI_LINE_COMMENT:
                starInMultilineCommentState(c);
                break;
            case FLOAT_SUFIX:
                floatSufixState(c);
                break;
            case DOUBLE_DOT:
                doubleDotState(c);
                break;
            default:
                System.out.println("ERROR! NO SUCH STATE");
        }
    }


    /**
     * State: INVALID_STATE
     */
    private void invalidState(Character c) {
        createPreviousDataToken(Token.TokenType.ERROR);
        state = State.INITIAL_STATE;
        initialState(c);
    }

    /**
     * State: INITIAL_STATE
     * Buffer: empty
     * Outbound states: ...
     */
    private void initialState(Character c) {
        if(c == '/') {
            state = State.SLASH;
        } else if (Character.isWhitespace(c)) {
            createToken(Token.TokenType.WHITESPACE);
            state = State.INITIAL_STATE;
        } else if (Character.isJavaIdentifierStart(c)) {
            state = State.JAVA_IDENTIFIER;
        } else if (c=='0') {
            state = State.ZERO_FIRST;
        } else if (Character.isDigit(c)) {
            state = State.NON_ZERO_DIGIT;
        } else if (c=='\'') {
            state = State.CHAR_LITERAL;
        } else if (c=='\"') {
            state = State.STRING_LITERAL;
        } else if (c=='.') {
            state = State.DOT;
        } else if (CharacterDeterminator.isSeparator(c)) {
            createToken(Token.TokenType.SEPARATOR);
            state = State.INITIAL_STATE;
        } else if (c=='>') {
            state = State.GREATER;
        } else if (c=='<') {
            state = State.LESS;
        } else if (c=='&') {
            state = State.AMPERSAND;
        } else if (c == '^' || c == '!' || c == '*' || c == '=' || c == '%') {
            state = State.SINGLE_OPERATOR;
        } else if (c==':') {
            state = State.COLON;
        } else if (c=='+') {
            state = State.PLUS;
        } else if (c=='-') {
            state = State.MINUS;
        } else if (c=='?' || c=='~') {
            createToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (c=='#') {
            state = State.INVALID_STATE;
        } else if (c=='|') {
            state = State.PIPE;
        }
    }

    /**
     * State: SLASH
     * Buffer: /
     * Outbound states: /*, //, /= ...
     */
    private void slashState(Character c) {
        if(c=='/') {
            state = State.SINGLE_LINE_COMMENT;
        } else if (c=='*') {
            state = State.MULTI_LINE_COMMENT;
        } else if (c == '=') {
            state = State.OPERATOR_AND_EQUAL;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: JAVA_IDENTIFIER
     * Buffer: identifier part( c, d3, var, ...)
     */
    private void javaIdentifierState(Character c) {
        if(Character.isJavaIdentifierPart(c)) {
            //nothing changes
        } else if (c=='#') {
            state = State.INVALID_STATE;
        } else if (Character.isWhitespace(c) || CharacterDeterminator.isOperator(c) || CharacterDeterminator.isSeparator(c) || c=='/') {
            buffer.deleteCharAt(buffer.length()-1);
            //when identifier ended
            if(CharacterDeterminator.isNullLiteral(buffer.toString())) {
                createToken(Token.TokenType.NULL_LITERAL);
            } else if (CharacterDeterminator.isBooleanLiteral(buffer.toString())) {
                createToken(Token.TokenType.BOOLEAN_LITERAL);
            } else if (CharacterDeterminator.isKeyword(buffer.toString())) {
                createToken(Token.TokenType.KEYWORD);
            } else {
                createToken(Token.TokenType.IDENTIFIER);
            }

            buffer.append(c);
            state = State.INITIAL_STATE;
            initialState(c);
        } else {
            state = State.INVALID_STATE;
        }
    }

    /**
     * State: ZERO_FIRST
     * Buffer: 0
     */
    private void zeroFirstState(Character c) {
        if(CharacterDeterminator.isOctal(c)) {
            state = State.OCTAL_DIGITS;
        } else if (c=='b' || c=='B') {
            state = State.BINARY_DIGITS;
        } else if (c=='x' || c=='X') {
            state = State.HEX_DIGITS;
        } else if (c=='.') {
            state = State.POINT_IN_DIGIT;
        } else if (c=='l' || c=='L') {
            state = State.INTEGER_SUFIX;
        } else if (Character.isJavaIdentifierPart(c) || c=='8' || c=='9') {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.INT_LITERAL);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }


    /**
     * State: NON_ZERO_DIGIT
     * Buffer: 1..9
     */
    private void nonZeroDigitState(Character c) {
        if (Character.isDigit(c)) {
            //nothing changes
        } else if (c=='.') {
            state = State.POINT_IN_DIGIT;
        } else if (c=='l' || c=='L') {
            state = State.INTEGER_SUFIX;
        } else if (c=='f' || c=='F') {
            state = State.FLOAT_SUFIX;
        } else if (Character.isJavaIdentifierPart(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.INT_LITERAL);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }


    /**
     * State: CHAR_LITERAL
     * Buffer: '
     */
    private void charLiteralState(Character c) {
        if(c=='\\') {
            state = State.POSSIBLE_ESCAPE_SEQUENCE_CHAR;
        } else if (Character.isWhitespace(c) && c!=' ' && c!='\t') {
            createPreviousDataToken(Token.TokenType.ERROR);
            state = State.INITIAL_STATE;
            initialState(c);
        } else {
            state = State.EXPECT_END_OF_CHAR;
        }
    }

    /**
     * State: STRING_LITERAL
     * Buffer: "
     */
    private void stringLiteralState(Character c) {
        if(c=='\\') {
            state = State.POSSIBLE_ESCAPE_SEQUENCE;
        } else if (c=='\"') {
            createToken(Token.TokenType.STRING_LITERAL);
            state = State.INITIAL_STATE;
        } else if (Character.isWhitespace(c) && c!=' ' && c!='\t') {
            createPreviousDataToken(Token.TokenType.ERROR);
            state = State.INITIAL_STATE;
            initialState(c);
        } else {
            //nothing changes
        }
    }

    /**
     * State: DOT
     * Buffer: .
     */
    private void dotState(Character c) {
        if(Character.isDigit(c)) {
            state = State.POINT_IN_DIGIT;
        } else if (c=='.') {
            state = State.DOUBLE_DOT;
        } else {
            createPreviousDataToken(Token.TokenType.SEPARATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: DOUBLE_DOT
     * Buffer: ..
     */
    private void doubleDotState(Character c) {
        if(c=='.') {
            createToken(Token.TokenType.SEPARATOR);
            state = State.INITIAL_STATE;
        } else {
            buffer = new StringBuilder();
            buffer.append(".");
            createToken(Token.TokenType.SEPARATOR);
            buffer.append(".");
            createToken(Token.TokenType.SEPARATOR);

            state = State.INITIAL_STATE;
        }
    }

    /**
     * State: GREATER
     * Buffer: >
     */
    private void greaterState(Character c) {
        if(c=='=') {
            createToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (c=='>') {
            state = State.DOUBLE_GREATER;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: DOUBLE_GREATER
     * Buffer: >>
     */
    private void doubleGreaterState(Character c) {
        if(c=='>') {
            createToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: LESS
     * Buffer: <
     */
    private void lessState(Character c) {
        if(c=='=') {
            createToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (c=='>') {
            createToken( Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (c=='<') {
            createToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: AMPERSAND
     * Buffer: &
     */
    private void ampersandState(Character c) {
        if (c == '&') {
            createToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
        } else if (c=='=') {
            state = State.OPERATOR_AND_EQUAL;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: SINGLE_OPERATOR
     * Buffer: <Operator>
     */
    private void singleOperatorState(Character c) {
        if(c=='=') {
            state = State.OPERATOR_AND_EQUAL;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: COLON
     * Buffer: :
     */
    private void colonState(Character c) {
        if(c==':') {
            createToken(Token.TokenType.SEPARATOR);
            state = State.INITIAL_STATE;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: PLUS
     * Buffer: +
     */
    private void plusState(Character c) {
        if(c=='+') {
            state = State.SINGLE_OPERATOR;
        } else if (c=='=') {
            state = State.OPERATOR_AND_EQUAL;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INITIAL_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: PLUS
     * Buffer: +
     */
    private void minusState(Character c) {
        if(c=='-') {
            state = State.SINGLE_OPERATOR;
        } else if (c=='=') {
            state = State.OPERATOR_AND_EQUAL;
        } else if (CharacterDeterminator.isOperator(c)) {
            state = State.INITIAL_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: SINGLE_LINE_COMMENT
     * Buffer: //...
     */
    private void singleLineCommentState(Character c) {
        if(Character.isWhitespace(c) && c!= '\t' && c!=' ') {
            createPreviousDataToken(Token.TokenType.COMMENT);
            state = State.INITIAL_STATE;
            initialState(c);
        } else {
            //nothing changes
        }
    }

    /**
     * State: MULTI_LINE_COMMENT
     * Buffer: /*...
     */
    private void multilineCommentState(Character c) {
        if(c=='*') {
            state = State.STAR_IN_MULTI_LINE_COMMENT;
        } else {
            //nothing changes
        }
    }

    /**
     * State: STAR_IN_MULTI_LINE_COMMENT
     * Buffer: /*...*
     */
    private void starInMultilineCommentState(Character c) {
        if(c=='/') {
            createToken(Token.TokenType.COMMENT);
        } else {
            state = State.MULTI_LINE_COMMENT;
        }
    }

    /**
     * State: OPERATOR_AND_EQUAL
     * Buffer: <operator>=
     */
    private void operatorAndEqualState(Character c) {
         if(CharacterDeterminator.isOperator(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }


    /**
     * State: OPERATOR_AND_EQUAL
     * Buffer: |
     */
    private void pipeState(Character c) {
        if (c=='|') {
           createToken(Token.TokenType.OPERATOR);
           state = State.INITIAL_STATE;
        } else if (c=='=') {
            state = State.OPERATOR_AND_EQUAL;
        } else {
            createPreviousDataToken(Token.TokenType.OPERATOR);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: POINT_IN_DIGIT
     * Buffer: number.number
     */
    private void pointInDigitState(Character c) {
        if (Character.isDigit(c)) {
            //nothing
        } else if(CharacterDeterminator.isDoubleOrFloat(c)) {
            state = State.FLOAT_SUFIX;
        } else if (Character.isJavaIdentifierPart(c) || c=='.') {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.FLOAT_LITERAL);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: POSSIBLE_ESCAPE_SEQUENCE
     * Buffer: <string>\
     */
    private void possibleEscapeSequenceState(Character c) {
        if(CharacterDeterminator.isSpecial("\\" + c)) {
            state = State.STRING_LITERAL;
        } else {
            state = State.INVALID_STATE;
        }
    }

    /**
     * State: POSSIBLE_ESCAPE_SEQUENCE_CHAR
     * Buffer: <char>\
     */
    private void possibleEscapeSequenceCharState(Character c) {
        if (CharacterDeterminator.isSpecial("\\" + c)) {
            state = State.EXPECT_END_OF_CHAR;
        } else {
            state = State.INVALID_STATE;
        }
    }

    /**
     * State: EXPECT_END_OF_CHAR
     * Buffer: '<char>
     */
    private void expectEndOfCharState(Character c) {
        if(c=='\'') {
            createToken(Token.TokenType.CHAR_LITERAL);
            state = State.INITIAL_STATE;
        } else {
            state = State.INVALID_STATE;
        }
    }

    /**
     * State: BINARY_DIGIT
     * Buffer: 0..1+
     */
    private void binaryDigitState(Character c) {
        if(CharacterDeterminator.isBinary(c)) {
            //nothing
        } else if (c=='_') {
            //nothing
        } else if (c=='l' || c=='L') {
            state = State.INTEGER_SUFIX;
        } else if (c=='f' || c=='F') {
            state = State.FLOAT_SUFIX;
        } else if (Character.isJavaIdentifierPart(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.INT_LITERAL);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: HEX_DIGIT
     * Buffer: 0..F+
     */
    private void hexDigitState(Character c) {
        if(CharacterDeterminator.isHex(c)) {
            //nothing
        } else if(c=='_') {
            //nothing
        } else if(c=='l' || c=='L') {
            state = State.INTEGER_SUFIX;
        } else if (c=='f' || c=='F') {
            state = State.FLOAT_SUFIX;
        } else if (Character.isJavaIdentifierPart(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.INT_LITERAL);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: OCTAL_DIGIT
     * Buffer: 0..7+
     */
    private void octalDigitState(Character c) {
        if(CharacterDeterminator.isOctal(c)) {
            //nothing
        } else if (c=='_') {
            //nothing
        } else if(c=='l' || c=='L') {
            state = State.INTEGER_SUFIX;
        } else if (c=='f' || c=='F') {
            state = State.FLOAT_SUFIX;
        } else if (Character.isJavaIdentifierPart(c) || c=='8' || c=='9') {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.INT_LITERAL);
            state = State.INITIAL_STATE;
            initialState(c);
        }
    }

    /**
     * State: INTEGER_SUFIX
     * Buffer: <number>L
     */
    private void integerSufixState(Character c) {
        if(Character.isJavaIdentifierPart(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.INT_LITERAL);
            state=State.INITIAL_STATE;
            initialState(c);
        }
    }

    private void floatSufixState(Character c) {
        if(Character.isJavaIdentifierPart(c)) {
            state = State.INVALID_STATE;
        } else {
            createPreviousDataToken(Token.TokenType.FLOAT_LITERAL);
            state=State.INITIAL_STATE;
            initialState(c);
        }
    }


    private void createToken(Token.TokenType tokenType) {
        tokens.add(new Token(tokenType, buffer.toString()));
        buffer = new StringBuilder();
    }

    private void createPreviousDataToken(Token.TokenType tokenType) {
        String previousData = buffer.substring(0, buffer.length() - 1);
        Character lastSymbol = buffer.charAt(buffer.length()-1);

        tokens.add(new Token(tokenType, previousData));
        buffer = new StringBuilder();

        buffer.append(lastSymbol);
    }
}
