package io.jvm;

public class Quote {
    public static final String escape(final String text, final char quote) {
        if (text == null) return text;

        final int len = text.length();
        final char[] quoted;

        char[] chars = null; // no need
        int cnt = len;

        if (len != 0) {
            chars = text.toCharArray();
            for (int i = len - 1; i >= 0; i --) {
                if (chars[i] == quote) cnt++;
            }
        }

        quoted = new char[cnt + 2];
        quoted[0] = quoted[cnt + 1] = quote;

        if (len != 0) {
            if (len == cnt) {
                System.arraycopy(chars, 0, quoted, 1, len);
            } else {
                for (int i = len - 1; i >= 0; i --) {
                    final char ch = chars[i];
                    quoted[cnt--] = ch;
                    if (ch == quote)
                        quoted[cnt--] = quote;
                }
            }
        }

        return new String(quoted);
    }

    public static final String escape(final String text, final char quote, final char escape) {
        if (text == null) return text;

        final int len = text.length();
        final char[] quoted;

        char[] chars = null; // no need
        int cnt = len;

        if (len != 0) {
            chars = text.toCharArray();
            for (int i = len - 1; i >= 0; i --) {
                final char ch = chars[i];
                if (ch == quote || ch == escape) cnt++;
            }
        }

        quoted = new char[cnt + 2];
        quoted[0] = quoted[cnt + 1] = quote;

        if (len != 0) {
            if (len == cnt) {
                System.arraycopy(chars, 0, quoted, 1, len);
            } else {
                for (int i = len - 1; i >= 0; i --) {
                    final char ch = chars[i];
                    quoted[cnt--] = ch;
                    if (ch == quote || ch == escape)
                        quoted[cnt--] = escape;
                }
            }
        }

        return new String(quoted);
    }
}
