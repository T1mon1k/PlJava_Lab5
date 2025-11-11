import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonRepo {

    public static void save(List<DataItem> items, Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < items.size(); i++) {
            DataItem it = items.get(i);
            sb.append("  { \"id\": ").append(it.getId()).append(", \"name\": \"").append(escape(it.getName())).append("\" }");
            if (i < items.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
    }

    public static List<DataItem> load(Path file) throws IOException {
        if (!Files.exists(file)) throw new IOException("Файл не знайдено.");
        String json = Files.readString(file, StandardCharsets.UTF_8);
        List<DataItem> result = new ArrayList<>();
        Pattern p = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*(\\d+)\\s*,\\s*\"name\"\\s*:\\s*\"(.*?)\"\\s*}");
        Matcher m = p.matcher(json);
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            String name = unescape(m.group(2));
            result.add(new DataItem(id, name));
        }
        return result;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
