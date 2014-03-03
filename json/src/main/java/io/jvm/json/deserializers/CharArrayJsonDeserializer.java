package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class CharArrayJsonDeserializer implements JsonDeserializer<char[]> {
    @Override
    public char[] fromJson(final JsonReader jsonReader) throws IOException {
        return jsonReader.readString().toCharArray();
    }

    private static final char[][] ZERO_ARRAY = new char[0][];

    @Override
    public char[][] getZeroArray() {
        return ZERO_ARRAY;
    }
}
