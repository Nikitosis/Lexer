import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class Lexer {
    public static List<Token> getTokens(Reader reader) throws IOException {
        int r;
        while((r = reader.read()) != -1) {
            char c = (char) r;
            System.out.print(c);
        }
    }
}
