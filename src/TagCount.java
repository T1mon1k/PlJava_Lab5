import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.*;

public final class TagCount {
    public static String fetchUrl(String url, Charset cs) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream(), cs))) {
            StringBuilder sb = new StringBuilder(64_000);
            char[] buf = new char[8192]; int n; while ((n = br.read(buf)) != -1) sb.append(buf, 0, n);
            return sb.toString();
        }
    }

    public static Map<String, Integer> countTags(String html) {
        Pattern p = Pattern.compile("<\\s*(?![/!?])([a-zA-Z][a-zA-Z0-9:-]*)\\b");
        Matcher m = p.matcher(html);
        Map<String, Integer> freq = new HashMap<>();
        while (m.find()) {
            String tag = m.group(1).toLowerCase(Locale.ROOT);
            freq.put(tag, freq.getOrDefault(tag, 0) + 1);
        }
        return freq;
    }

    public static void printByAlf(Map<String, Integer> freq) {
        System.out.println("=== Теги за алфавітом (зростання) ===");
        new TreeMap<>(freq).forEach((k,v)-> System.out.println(k+" : "+v));
    }

    public static void printByCount(Map<String, Integer> freq) {
        System.out.println("=== Теги за частотою (зростання) ===");
        var list = new ArrayList<>(freq.entrySet());
        list.sort(Comparator.comparingInt((Map.Entry<String, Integer> a) -> a.getValue()).thenComparing(Map.Entry::getKey));
        for (var e : list)
            System.out.println(e.getKey()+" : "+e.getValue());
    }
}
