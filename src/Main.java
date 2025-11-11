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
            System.out.println("\n=== Лабораторна робота 5: (Пункти 1, 3, 4) ===");
            System.out.println("(1) Рядок з максимальною кількістю слів у файлі");
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
                    case "3" -> {
                        System.out.println("a) Зашифрувати файл");
                        System.out.println("b) Розшифрувати файл");
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
