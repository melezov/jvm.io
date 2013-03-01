package io.jvm.util;

public class Utils {
    public static final String quote(final String text, final char quote) {
        final int len = text.length();
        if (len == 0) {
            return new String(new char[]{ quote, quote });
        }

        final int total; {
            int cnt = 0;
            int start = 0;
            while (true) {
                final int ind = text.indexOf(quote, start);
                if (ind == -1) {
                    break;
                }

                start = ind + 1;
                cnt++;
            }
            total = cnt;
        }

        final int newLen = len + 2 + total;

        final char[] quoted = new char[newLen]; {
            quoted[0] = quoted[newLen - 1] = quote;

            if (total == 0) {
                for (int i = 0; i < len; i++) {
                    quoted[i + 1] = text.charAt(i);
                }
            } else {
                int cnt = 1;
                for (int i = 0; i < len; i++) {
                    final char ch = text.charAt(i);
                    quoted[cnt + i] = ch;

                    if (ch == quote) {
                        quoted[++cnt + i] = ch;
                    }
                }
            }
        }

        return new String(quoted);
    }
}
