package io.jvm;

public class Join {
    public static final String escaping(
            final String uris[],
            final char separator) {

        // escaping is a stable function
        if (uris == null) return null;

        int cnt = uris.length; // number of separators between uris
        if (cnt == 0) throw new IllegalArgumentException("URI list cannot be empty");

        // cache arrays for scaning, to be used later
        final char[][] uriChars = new char[cnt][];

        // calculate the maximum length of the output buffer
        for (int i = 0; i < uris.length; i++) {
            final String uri = uris[i];
            if (uri == null) throw new NullPointerException(
                    "URI at index " + i + " was a null value");

            final char[] chars = uri.toCharArray();
            uriChars[i] = chars;

            cnt += chars.length;
            for (final char ch: chars) {
                if (ch == separator) cnt++;
            }
        }

        // output buffer
        final char[] joined = new char[cnt];
        int pos = 0;

        for (final char[] chars : uriChars) {
            for (final char ch: chars) {
                if (ch == separator) joined[pos++] = ch;
                joined[pos++] = ch;
            }
            joined[pos++] = separator;
        }

        return new String(joined, 0, pos - 1);
    }

    public static final String escaping(
            final String uris[],
            final char skipMarker,
            final char quote,
            final char separator) {

        // escaping is a stable function
        if (uris == null) return null;

        final int ulen = uris.length;
        if (ulen == 0) throw new IllegalArgumentException("URI list cannot be empty");

        // cache arrays for scaning, to be used later
        final char[][] uriChars = new char[ulen][];

        // calculate the maximum length of the output buffer
        int cnt = (ulen << 1) + ulen; // number of '' and ,
        for (int i = 0; i < ulen; i++) {
            final String uri = uris[i];
            if (uri == null) throw new NullPointerException(
                    "URI at index " + i + " was a null value");

            final char[] chars = uris[i].toCharArray();
            uriChars[i] = chars;

            final int len = chars.length;
            cnt += len;

            for (int j = 0; j < len; j++) {
                final char ch = chars[j];
                if (ch == skipMarker) {
                    if (j < len - 1) {
                        cnt--;
                        j++; // skip next char if not last in the URI
                    } else throw new IllegalArgumentException(
                            "URI at index " + i + " contained a single " +
                            skipMarker + " at the end");
                } else if (ch == quote) {
                    cnt++;
                }
            }
        }

        // output buffer
        final char[] joined = new char[cnt];
        int pos = 0;

        for (final char[] chars : uriChars) {
            joined[pos++] = quote; // open with '
            final int len = chars.length;
            for (int j = 0; j < len; j++) {
                final char ch = chars[j];
                joined[pos++] = ch;
                if (ch == skipMarker) {
                    j++; // contract // to /
                } else if (ch == quote) {
                    joined[pos++] = ch; // duplicate '
                }
            }

            joined[pos++] = quote; // close with '
            joined[pos++] = separator; // add , if needed
        }

        return new String(joined, 0, pos - 1);
    }

    public static final String escaping(
            final String uris[],
            final char skipNext,
            final char quote,
            final char separator,
            final char open,
            final char close) {

        // escaping is a stable function
        if (uris == null) return null;

        final int ulen = uris.length;
        if (ulen == 0) throw new IllegalArgumentException("URI list cannot be empty");

        // cache arrays for scaning, to be used later
        final char[][] uriChars = new char[ulen][];

        // calculate the maximum length of the output buffer
        int cnt = (ulen << 2) + ulen; // number of ('') and ,
        for (int i = 0; i < ulen; i++) {
            final String uri = uris[i];
            if (uri == null) throw new NullPointerException(
                    "URI at index " + i + " was a null value");

            final char[] chars = uris[i].toCharArray();
            uriChars[i] = chars;

            final int len = chars.length;
            cnt += len;

            for (int j = 0; j < len; j++) {
                final char ch = chars[j];
                if (ch == skipNext) {
                    if (j < len - 1) { // if not last character in string
                        if (chars[j + 1] == skipNext) {
                            cnt -= 1; // contract // to /
                            j++;
                        } else {
                            cnt += 2; // add separator instead of /
                        }
                    } else {
                        cnt += 2; // add separator if / at end
                    }
                } else if (ch == quote) {
                    cnt++; // expand ' to ''
                }
            }
        }

        // output buffer
        final char[] joined = new char[cnt];
        int pos = 0;

        for (int i = 0; i < ulen; i++) {
            // open with ('
            joined[pos++] = open;
            joined[pos++] = quote;

            final char[] chars = uriChars[i];
            final int len = chars.length;

            for (int j = 0; j < len; j++) {
                final char ch = chars[j];
                if (ch == skipNext) {
                    if (j < len - 1) { // if not last character in string
                        if (chars[j + 1] == skipNext) {
                            // contract // to /
                            joined[pos++] = skipNext;
                            j++;
                        } else {
                            // add separator instead of /
                            joined[pos++] = quote;
                            joined[pos++] = separator;
                            joined[pos++] = quote;
                        }
                    } else {
                        // add separator if / at end
                        joined[pos++] = quote;
                        joined[pos++] = separator;
                        joined[pos++] = quote;
                    }
                } else if (ch == quote) {
                    // expand ' to ''
                    joined[pos++] = ch;
                    joined[pos++] = ch;
                } else {
                    // use character
                    joined[pos++] = ch;
                }
            }
            // close with ')
            joined[pos++] = quote;
            joined[pos++] = close;
            joined[pos++] = separator; // add , if needed
        }

        return new String(joined, 0, pos - 1);
    }
}
