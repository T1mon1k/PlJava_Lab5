import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.text.MessageFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger("main");
    private static ResourceBundle bundle;
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        Charset cs = StandardCharsets.UTF_8;
        Locale currentLocale = new Locale("uk", "UA");
        loadBundle(currentLocale);
        while (true) {
            System.out.println(bundle.getString("menu.title"));
            System.out.println(bundle.getString("menu.1"));
            System.out.println(bundle.getString("menu.2"));
            System.out.println(bundle.getString("menu.3"));
            System.out.println(bundle.getString("menu.4"));
            System.out.println(bundle.getString("menu.lang"));
            System.out.println(bundle.getString("menu.0"));
            System.out.print(bundle.getString("menu.choice"));
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print(bundle.getString("msg.input_path"));
                        Path p = Path.of(sc.nextLine().trim());
                        List<WordLineCount.LineInfo> lines = WordLineCount.findLinesWithMaxWords(p, cs);
                        if (lines.isEmpty()) {
                            System.out.println(bundle.getString("msg.empty"));
                        } else {
                            int maxWords = lines.getFirst().words;
                            System.out.println(bundle.getString("msg.max_lines").replace("{0}", String.valueOf(maxWords)));
                            for (var l : lines)
                                System.out.println(MessageFormat.format(bundle.getString("msg.line_prefix"), l.lineNumber, l.line));
                        }
                    }
                    case "2" -> {
                        System.out.println(bundle.getString("menu.2.sub.a"));
                        System.out.println(bundle.getString("menu.2.sub.b"));
                        System.out.print(bundle.getString("msg.choice_ab"));
                        String m = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                        if ("a".equals(m)) {
                            var items = new java.util.ArrayList<DataItem>();
                            System.out.println(bundle.getString("msg.enter_pairs"));
                            while (true) {
                                System.out.print(">> ");
                                String line = sc.nextLine().trim();
                                if (line.isEmpty()) break;
                                String[] p = line.split("\\s+", 2);
                                if (p.length < 2) {
                                    System.out.println(bundle.getString("msg.format_error"));
                                    continue;
                                }
                                try {
                                    int id = Integer.parseInt(p[0]);
                                    items.add(new DataItem(id, p[1].trim()));
                                } catch (NumberFormatException e) {
                                    System.out.println(bundle.getString("msg.id_must_be_number"));
                                }
                            }
                            System.out.print(bundle.getString("msg.save_path"));
                            Path file = Path.of(sc.nextLine().trim());
                            JsonRepo.save(items, file);
                            System.out.println(MessageFormat.format(bundle.getString("msg.json_saved"), file.toAbsolutePath()));
                        }
                        else if ("b".equals(m)) {
                            System.out.print(bundle.getString("msg.read_path"));
                            Path file = Path.of(sc.nextLine().trim());
                            var items = JsonRepo.load(file);
                            if (items.isEmpty()) {
                                System.out.println(bundle.getString("msg.file_empty_or_missing"));
                            } else {
                                System.out.println(MessageFormat.format(bundle.getString("msg.objects_read"), items.size()));
                                items.forEach(System.out::println);
                            }
                            System.out.print(bundle.getString("msg.search_name"));
                            String q = sc.nextLine().toLowerCase();
                            if (!q.isEmpty()) {
                                var found = items.stream()
                                        .filter(it -> it.getName().toLowerCase().contains(q))
                                        .toList();
                                if (found.isEmpty())
                                    System.out.println(bundle.getString("msg.nothing_found"));
                                else {
                                    System.out.println(bundle.getString("msg.found_items"));
                                    found.forEach(System.out::println);
                                }
                            }
                        } else {
                            System.out.println(bundle.getString("msg.invalid_mode"));
                        }
                    }
                    case "3" -> {
                        System.out.println(bundle.getString("menu.3.sub.a"));
                        System.out.println(bundle.getString("menu.3.sub.b"));
                        System.out.print(bundle.getString("msg.choice_ab"));
                        String mode = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                        if (!mode.equals("a") && !mode.equals("b")) {
                            logger.warn(bundle.getString("log.warn.invalid_mode"), mode);
                            System.out.println(bundle.getString("msg.invalid_mode"));
                            continue;
                        }
                        System.out.print(bundle.getString("msg.input_file"));
                        String inputPath = sc.nextLine().trim();
                        if (inputPath.isEmpty()) {
                            logger.warn(bundle.getString("log.warn.empty_input"));
                            System.out.println(bundle.getString("msg.path_cannot_be_empty"));
                            continue;
                        }
                        Path in = Path.of(inputPath);
                        if (!Files.exists(in) || Files.isDirectory(in)) {
                            logger.warn(bundle.getString("log.warn.file_not_found"), in);
                            System.out.println(bundle.getString("msg.file_not_found"));
                            continue;
                        }
                        System.out.print(bundle.getString("msg.output_file"));
                        String outputPath = sc.nextLine().trim();
                        if (outputPath.isEmpty()) {
                            logger.warn(bundle.getString("log.warn.empty_output"));
                            System.out.println(bundle.getString("msg.path_cannot_be_empty"));
                            continue;
                        }
                        Path out = Path.of(outputPath);
                        if (Files.exists(out) && Files.isDirectory(out)) {
                            logger.warn(bundle.getString("log.warn.output_is_dir"), out);
                            System.out.println(bundle.getString("msg.dir_error"));
                            continue;
                        }
                        try {
                            Files.createDirectories(out.getParent());
                        } catch (Exception e) {
                            logger.warn(bundle.getString("log.warn.dir_creation_fail"), out);
                            System.out.println(bundle.getString("msg.dir_creation_error"));
                            continue;
                        }
                        System.out.print(bundle.getString("msg.enter_key"));
                        String keyStr = sc.nextLine();
                        if (keyStr.isEmpty()) {
                            logger.warn(bundle.getString("log.warn.empty_key"));
                            System.out.println(bundle.getString("msg.key_empty"));
                            continue;
                        }
                        char key = keyStr.charAt(0);
                        if ("a".equals(mode)) {
                            Cipher.encryptFile(in, out, key, cs, bundle);
                            System.out.println(MessageFormat.format(bundle.getString("msg.encrypted_success"), out.toAbsolutePath()));
                        } else if ("b".equals(mode)) {
                            Cipher.decryptFile(in, out, key, cs, bundle);
                            System.out.println(MessageFormat.format(bundle.getString("msg.decrypted_success"), out.toAbsolutePath()));
                        }
                    }
                    case "4" -> {
                        System.out.print(bundle.getString("msg.enter_url"));
                        String url = sc.nextLine().trim();
                        String html = TagCount.fetchUrl(url, cs);
                        var freq = TagCount.countTags(html);
                        if (freq.isEmpty())
                            System.out.println(bundle.getString("msg.tags_not_found"));
                        else {
                            TagCount.printByAlf(freq, bundle);
                            System.out.println();
                            TagCount.printByCount(freq, bundle);
                        }
                    }
                    case "5" -> {
                        System.out.print(bundle.getString("msg.lang_select"));
                        String langChoice = sc.nextLine().trim();
                        if ("1".equals(langChoice)) {
                            currentLocale = new Locale("uk", "UA");
                        } else if ("2".equals(langChoice)) {
                            currentLocale = new Locale("en", "US");
                        } else {
                            System.out.println(bundle.getString("msg.invalid_mode"));
                        }
                        loadBundle(currentLocale);
                    }
                    case "0" -> {
                        System.out.println(bundle.getString("msg.bye"));
                        return;
                    }
                    default -> System.out.println(bundle.getString("msg.error_choice"));
                }
            } catch (Exception ex) {
                logger.error(bundle.getString("log.error.main_menu"), ex.getMessage());
                System.out.println(bundle.getString("msg.error_prefix") + " " + ex.getMessage());
            }
        }
    }

    private static void loadBundle(Locale locale) {
        bundle = ResourceBundle.getBundle("resources.location.text", locale);
    }
}
