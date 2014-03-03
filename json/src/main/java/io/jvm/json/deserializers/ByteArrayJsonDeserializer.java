package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

public class ByteArrayJsonDeserializer implements JsonDeserializer<byte[]> {
    @Override
    public byte[] fromJson(final JsonReader jsonReader) throws IOException {
        jsonReader.assertRead('"');
        // TODO: replace with streaming base64 deserialization, this is crap
        final StringBuilder sb = new StringBuilder();
        for (;;) {
            final char ch = jsonReader.next();
            if (ch == '"') break;

            if (!(ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' || ch == '+' || ch == '/' || ch == '='))
                throw new IOException("Invalid character found in base64 stream: " + ch);
            sb.append(ch);
        }
        jsonReader.invalidate();

        return DatatypeConverter.parseBase64Binary(sb.toString());
    }

    private static final byte[][] ZERO_ARRAY = new byte[0][];

    @Override
    public byte[][] getZeroArray() {
        return ZERO_ARRAY;
    }
}
