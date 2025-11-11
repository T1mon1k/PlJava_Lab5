import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.*;

public final class WordLineCount {
    private static final Pattern WORD_UK = Pattern.compile("\\p{L}+(?:['’-]\\p{L}+)*");

    private static int countWordsUk(String s) {
        Matcher m = WORD_UK.matcher(s);
        int c = 0; while (m.find()) c++; return c;
    }

    public static final class LineInfo {
        public final String line;
        public final int words;
        public final int lineNumber;
        public LineInfo(String line, int words, int lineNumber) {
            this.line = line;
            this.words = words;
            this.lineNumber = lineNumber;
        }
    }

    public static List<LineInfo> findLinesWithMaxWords(Path path, Charset cs) throws IOException {
        List<LineInfo> result = new ArrayList<>();
        int max = 0, lineNo = 0;
        try (BufferedReader br = Files.newBufferedReader(path, cs)) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                int cnt = countWordsUk(line);
                if (cnt > max) { max = cnt; result.clear(); result.add(new LineInfo(line, cnt, lineNo)); }
                else if (cnt == max && max > 0) { result.add(new LineInfo(line, cnt, lineNo)); }
            }
        }
        return result;
    }
}
