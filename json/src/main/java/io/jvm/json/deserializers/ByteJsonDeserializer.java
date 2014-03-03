package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class ByteJsonDeserializer implements JsonDeserializer<Byte> {
    @Override
    public Byte fromJson(final JsonReader jsonReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        jsonReader.readRawNumber(sb);
        return (byte) Double.parseDouble(sb.toString());
    }

    private static final Byte[] ZERO_ARRAY = new Byte[0];

    @Override
    public Byte[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
