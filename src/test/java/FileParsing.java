import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin on 10/4/16.
 */
public class FileParsing {
    private List<String> lines = readLinesFromFile();
    private Object[][] params = toParams(lines);

    public Object[][] getParams()
    {
        return params;
    }

    private static List<String> readLinesFromFile()
    {
        try {
            return Files.readAllLines(Paths.get("D:/Coding/Lesson4/src/test/resources/test-data.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    private static Object[][] toParams(List<String> lines) {
        Object[][] params = new Object[lines.size()][];
        int i = 0;
        for (String line : lines)
        {
            String [] tokens = line.split(";");
            params[i++] = tokens;
        }
        return params;
    }
}
