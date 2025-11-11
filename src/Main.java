import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        Charset cs = StandardCharsets.UTF_8;

        while (true) {
            System.out.println("\n=== Лабораторна робота 5: ===");
            System.out.println("(1) Рядок з максимальною кількістю слів у файлі");
            System.out.println("(2) Серіалізація об’єктів (збереження/читання набору об’єктів)");
            System.out.println("(3) Шифрування/дешифрування файлу (FilterReader/FilterWriter)");
            System.out.println("(4) Частота HTML-тегів за URL");
            System.out.println("(0) Вихід");
            System.out.print("Ваш вибір: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Вкажіть шлях до файлу (Наприклад: src/files/input1.txt): ");
                        Path p = Path.of(sc.nextLine().trim());
                        List<WordLineCount.LineInfo> lines = WordLineCount.findLinesWithMaxWords(p, cs);
                        if (lines.isEmpty()) {
                            System.out.println("Файл порожній або не містить слів.");
                        } else {
                            int maxWords = lines.getFirst().words;
                            System.out.println("Рядки з максимальною кількістю слів (" + maxWords + "):");
                            for (var l : lines) System.out.println("Рядок " + l.lineNumber + ": " + l.line);
                        }
                    }
                    case "2" -> {
                        System.out.println("(a) Створити список і зберегти у JSON");
                        System.out.println("(b) Прочитати з JSON і виконати пошук");
                        System.out.print("Ваш вибір (a/b): ");
                        String m = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                        if ("a".equals(m)) {
                            var items = new java.util.ArrayList<DataItem>();
                            System.out.println("Вводьте пари <id name>. Порожній рядок — завершити.");
                            while (true) {
                                System.out.print(">> ");
                                String line = sc.nextLine().trim();
                                if (line.isEmpty()) break;
                                String[] p = line.split("\\s+", 2);
                                if (p.length < 2) { System.out.println("Формат: <id> <name>"); continue; }
                                try {
                                    int id = Integer.parseInt(p[0]);
                                    items.add(new DataItem(id, p[1].trim()));
                                } catch (NumberFormatException e) {
                                    System.out.println("id має бути числом.");
                                }
                            }
                            System.out.print("Куди зберегти (напр., src/files/test.json): ");
                            Path file = Path.of(sc.nextLine().trim());
                            JsonRepo.save(items, file);
                            System.out.println("JSON збережено у: " + file.toAbsolutePath());
                        }
                        else if ("b".equals(m)) {
                            System.out.print("Звідки читати (напр., src/files/test.json): ");
                            Path file = Path.of(sc.nextLine().trim());
                            var items = JsonRepo.load(file);
                            if (items.isEmpty()) {
                                System.out.println("Файл порожній або дані відсутні.");
                            } else {
                                System.out.println("Прочитано " + items.size() + " об’єктів:");
                                items.forEach(System.out::println);
                            }
                            System.out.print("Введіть рядок для пошуку в name: ");
                            String q = sc.nextLine().toLowerCase();
                            if (!q.isEmpty()) {
                                var found = items.stream()
                                        .filter(it -> it.getName().toLowerCase().contains(q))
                                        .toList();
                                if (found.isEmpty()) System.out.println("Нічого не знайдено.");
                                else {
                                    System.out.println("Знайдено:");
                                    found.forEach(System.out::println);
                                }
                            }
                        } else {
                            System.out.println("Невірний режим.");
                        }
                    }
                    case "3" -> {
                        System.out.println("(a) Зашифрувати файл");
                        System.out.println("(b) Розшифрувати файл");
                        System.out.print("Ваш вибір (a/b): ");
                        String mode = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                        System.out.print("Вкажіть шлях ВХІДНОГО файлу (Наприклад: src/files/input1.txt): ");
                        Path in = Path.of(sc.nextLine().trim());
                        System.out.print("Вкажіть шлях ВИХІДНОГО файлу (Наприклад: src/files/output1.txt): ");
                        Path out = Path.of(sc.nextLine().trim());
                        System.out.print("Вкажіть КЛЮЧ (один символ): ");
                        String keyStr = sc.nextLine();
                        if (keyStr.isEmpty()) throw new IllegalArgumentException("Ключ не може бути порожнім.");
                        char key = keyStr.charAt(0);
                        if ("a".equals(mode)) {
                            Cipher.encryptFile(in, out, key, cs);
                            System.out.println("Готово! Файл зашифровано у: " + out.toAbsolutePath());
                        } else if ("b".equals(mode)) {
                            Cipher.decryptFile(in, out, key, cs);
                            System.out.println("Готово! Файл розшифровано у: " + out.toAbsolutePath());
                        } else {
                            System.out.println("Невірний режим.");
                        }
                    }
                    case "4" -> {
                        System.out.print("Введіть URL (http/https) (Наприклад: https://www.java.com/): ");
                        String url = sc.nextLine().trim();
                        String html = TagCount.fetchUrl(url, cs);
                        var freq = TagCount.countTags(html);
                        if (freq.isEmpty()) System.out.println("Теги не знайдено.");
                        else {
                            TagCount.printByAlf(freq);
                            System.out.println();
                            TagCount.printByCount(freq);
                        }
                    }
                    case "0" -> {
                        System.out.println("Вихід. Гарного дня!");
                        return;
                    }
                    default -> System.out.println("Невірний вибір.");
                }
            } catch (Exception ex) {
                System.out.println("Помилка: " + ex.getMessage());
            }
        }
    }
}
