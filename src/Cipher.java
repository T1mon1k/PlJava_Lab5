import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Cipher {
    private static final Logger logger = LogManager.getLogger("cipher");
    public static final class ShiftWriter extends FilterWriter {
        private final int shift;
        public ShiftWriter(Writer out, char key) {
            super(out);
            this.shift = key;
        }
        @Override
        public void write(int c) throws IOException {
            super.write(c + shift); }
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            char[] tmp = java.util.Arrays.copyOfRange(cbuf, off, off + len);
            for (int i = 0; i < tmp.length; i++)
                tmp[i] = (char) (tmp[i] + shift);
            super.write(tmp, 0, tmp.length);
        }
        @Override
        public void write(String str, int off, int len) throws IOException {
            char[] buf = str.substring(off, off + len).toCharArray();
            for (int i = 0; i < buf.length; i++)
                buf[i] = (char) (buf[i] + shift);
            super.write(buf, 0, buf.length);
        }
    }

    public static final class ShiftReader extends FilterReader {
        private final int shift;
        public ShiftReader(Reader in, char key) {
            super(in);
            this.shift = key;
        }
        @Override
        public int read() throws IOException {
            int r = super.read();
            return (r == -1) ? -1 : (r - shift);
        }
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int n = super.read(cbuf, off, len); if (n == -1) return -1;
            for (int i = off; i < off + n; i++)
                cbuf[i] = (char) (cbuf[i] - shift);
            return n;
        }
    }

    public static void encryptFile(Path in, Path out, char key, Charset cs, ResourceBundle bundle) throws IOException {
        logger.info(bundle.getString("log.encrypt.start"), in);
        if (key == '!') {
            logger.error(bundle.getString("log.encrypt.error.artificial"));
            throw new IOException(bundle.getString("ex.encrypt.artificial"));
        }
        try (BufferedReader r = Files.newBufferedReader(in, cs);
             ShiftWriter w = new ShiftWriter(Files.newBufferedWriter(out, cs), key)) {
            char[] buf = new char[4096];
            int n;
            while ((n = r.read(buf)) != -1)
                w.write(buf, 0, n);
            logger.info(bundle.getString("log.encrypt.finish"), out);
        } catch (IOException e) {
            logger.error(bundle.getString("log.encrypt.error"), e.getMessage());
            throw e;
        }
    }

    public static void decryptFile(Path in, Path out, char key, Charset cs, ResourceBundle bundle) throws IOException {
        logger.info(bundle.getString("log.decrypt.start"), in);
        try (ShiftReader r = new ShiftReader(Files.newBufferedReader(in, cs), key);
             BufferedWriter w = Files.newBufferedWriter(out, cs)) {
            char[] buf = new char[4096];
            int n;
            while ((n = r.read(buf)) != -1)
                w.write(buf, 0, n);
            logger.info(bundle.getString("log.decrypt.finish"), out);
        } catch (IOException e) {
            logger.error(bundle.getString("log.decrypt.error"), e.getMessage());
            throw e;
        }
    }
}
