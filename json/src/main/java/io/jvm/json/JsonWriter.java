package io.jvm.json;

import java.io.IOException;
import java.io.Writer;

public class JsonWriter {
    private final Writer writer;

    public JsonWriter(final Writer writer) {
        this.writer = writer;
    }

    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final char[] ASCII = new char[0x100 << 3];

    static {
        for (char c = 0; c < 0x100; c++) {
            final int c8 = c << 3;
            final char special;

            switch (c) {
                case 0x08: special = 'b'; break;
                case 0x09: special = 't'; break;
                case 0x0a: special = 'n'; break;
                case 0x0c: special = 'f'; break;
                case 0x0d: special = 'r'; break;
                case 0x22: special = '"'; break;
                //case 0x2f: special = '/'; break;
                case 0x5c: special = '\\'; break;

                default:
                    if (c <= 0x1f || c >= 0x7f && c <= 0x9f || c == 0xad) {
                        ASCII[c8] = 6;
                        ASCII[c8 + 1] = '\\';
                        ASCII[c8 + 2] = 'u';
                        ASCII[c8 + 3] = '0';
                        ASCII[c8 + 4] = '0';
                        ASCII[c8 + 5] = HEX[(c >>> 4) & 0xf];
                        ASCII[c8 + 6] = HEX[c & 0xf];
                    }
//                    else {
//                        ASCII[c8] = 1;
//                        ASCII[c8 + 1] = c;
//                    }
                    continue;
            }

            ASCII[c8] = 2;
            ASCII[c8 + 1] = '\\';
            ASCII[c8 + 2] = special;
        }
    }

    public void writeNull() throws IOException {
        writer.write("null");
    }

    public void writeOpenObject(boolean needsComma) throws IOException {    		
        writer.write('{');        
    }
    
    public void writeOpenObject() throws IOException {
        writer.write('{');
    }

    public void writeCloseObject() throws IOException {
        writer.write('}');
    }

    public void writeOpenArray() throws IOException {
        writer.write('[');
    }

    public void writeCloseArray() throws IOException {
        writer.write(']');
    }

    public void writeComma() throws IOException {
        writer.write(',');
    }

    public void writeColon() throws IOException {
        writer.write(':');
    }

    public void writeRaw(final char value) throws IOException {
        writer.write(value);
    }

    public void writeRaw(final String value) throws IOException {
        writer.write(value);
    }

    public void writeCharArray(final char[] values) throws IOException {
        final int length = values.length;
        if (length == 0) {
            writer.write("\"\"");
            return;
        }

        writer.write('"');
        int last = 0;
        for (int index = 0; index < length; index++) {
            final char c = values[index];
            if (c < 0x100) {
                final int c8 = c << 3;
                final int cLen = ASCII[c8];
                if (cLen > 0) {
                    writer.write(values, last, index - last);
                    writer.write(ASCII, c8 + 1, cLen);
                    last = index + 1;
                }
            } else if (c >= 0x0600 && c <= 0x0604 ||
                    c == 0x070f || c == 0x17b4 || c == 0x17b5 ||
                    c >= 0x200c && c <= 0x200f ||
                    c >= 0x2028 && c <= 0x202f ||
                    c >= 0x2060 && c <= 0x206f ||
                    c == 0xfeff || c >= 0xfff0) {
                writer.write(values, last, index - last);
                writer.write(new char[] {
                    '\\',
                    'u',
                    HEX[(c >>> 12) & 0xf],
                    HEX[(c >>>  8) & 0xf],
                    HEX[(c >>>  4) & 0xf],
                    HEX[c & 0xf]});
                last = index + 1;
            }
        }

        writer.write(values, last, length - last);
        writer.write('"');
    }

    public void writeString(final String value) throws IOException {
    	
        writeCharArray(value.toCharArray());
    }

    // -------------------------------------------------------------------------

    public <T> void writeOpt(final T value, final JsonSerializer<T> serializer) throws IOException {
        if (value == null) writeNull(); else serializer.toJson(this, value);
    }

    public <T> void writeArray(final T[] values, final JsonSerializer<T> serializer) throws IOException {
        writeOpenArray();
        boolean needsComma = false;
        for (final T value : values) {
            if (needsComma) writeComma();
            serializer.toJson(this, value);
            needsComma = true;
        }
        writeCloseArray();
    }

    public <T> void writeOptArray(final T[] values, final JsonSerializer<T> serializer) throws IOException {
        if (values == null) writeNull(); else writeArray(values, serializer);
    }

    public <T> void writeArrayOpt(final T[] values, final JsonSerializer<T> serializer) throws IOException {
        writeOpenArray();
        boolean needsComma = false;
        for (final T value : values) {
            if (needsComma) writeComma();
            if (value == null) writeNull(); else serializer.toJson(this, value);
            needsComma = true;
        }
        writeCloseArray();
    }

    public <T> void writeOptArrayOpt(final T[] values, final JsonSerializer<T> serializer) throws IOException {
        if (values == null) writeNull(); else writeArrayOpt(values, serializer);
    }

    // -------------------------------------------------------------------------

    public <T> void writeIterable(final Iterable<T> values, final JsonSerializer<T> serializer) throws IOException {
        writeOpenArray();
        boolean needsComma = false;
        for (final T value : values) {
            if (needsComma) writeComma();
            serializer.toJson(this, value);
            needsComma = true;
        }
        writeCloseArray();
    }

    public <T> void writeOptIterable(final Iterable<T> values, final JsonSerializer<T> serializer) throws IOException {
        if (values == null) writeNull(); else writeIterable(values, serializer);
    }

    public <T> void writeIterableOpt(final Iterable<T> values, final JsonSerializer<T> serializer) throws IOException {
        writeOpenArray();
        boolean needsComma = false;
        for (final T value : values) {
            if (needsComma) writeComma();
            if (value == null) writeNull(); else serializer.toJson(this, value);
            needsComma = true;
        }
        writeCloseArray();
    }

    public <T> void writeOptIterableOpt(final Iterable<T> values, final JsonSerializer<T> serializer) throws IOException {
        if (values == null) writeNull(); else writeIterableOpt(values, serializer);
    }
}
