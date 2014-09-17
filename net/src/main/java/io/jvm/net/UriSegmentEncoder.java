package io.jvm.net;

import java.io.UnsupportedEncodingException;

public class UriSegmentEncoder {
    private static final char[] VALID_CHARS = (
            "?????????????????????????????????"+
            "!??$?&'()*+,-.?0123456789:;?=??@" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ????_?" +
            "abcdefghijklmnopqrstuvwxyz???~").toCharArray();

    public static String encode(final String segment, final String encoding) {
        if (segment == null) return null;

        final int length = segment.length();
        if (length == 0) return segment;

        final int maxSafe = VALID_CHARS.length;
        final char[] chars = segment.toCharArray();

        int index = 0;
        while (index < length) {
            final char ch = chars[index];
            if (ch >= maxSafe || VALID_CHARS[ch] == '?') break;
            index ++;
        }

        if (index == length) return segment;

        final char[] buffer = new char[length << 5];
        System.arraycopy(chars, 0, buffer, 0, index);
        int counter = index;

        do {
            final char ch = chars[index];
            if (ch < maxSafe && VALID_CHARS[ch] != '?') {
                buffer[counter++] = ch;
            }
            else {
                try {
                    for (final byte b : String.valueOf(ch).getBytes(encoding)) {
                        final int h = ((b >>> 4) & 0xf);
                        final int l = b & 0xf;

                        buffer[counter] = '%';
                        buffer[counter+1] = (char) (h > 9 ? h + '7' : h + '0');
                        buffer[counter+2] = (char) (l > 9 ? l + '7' : l + '0');
                        counter += 3;
                    }
                }
                catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException("Could not decode segment", e);
                }
            }
            index ++;
        }
        while (index < length);

        return new String(buffer, 0, counter);
    }
}
