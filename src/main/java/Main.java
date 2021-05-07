import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) throws IOException {
        File f = new File("code.java");
        InputStream inputStream = new FileInputStream(f);
        Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
        Reader bufferedReader = new BufferedReader(reader);

        int r;
        while((r = bufferedReader.read()) != -1) {
            char c = (char) r;
            System.out.print(c);
        }
    }
}
