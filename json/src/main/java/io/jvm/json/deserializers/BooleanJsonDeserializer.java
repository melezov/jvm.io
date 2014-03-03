package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class BooleanJsonDeserializer implements JsonDeserializer<Boolean> {
    @Override
    public Boolean fromJson(final JsonReader jsonReader) throws IOException {
        final char ch = jsonReader.read();
        if (ch == 't') return jsonReader.readTrue();
        if (ch == 'f') return jsonReader.readFalse();
        throw new IOException("Could not parse boolean, expected 'true' or 'false', but token begins with '" + ch + "'");
    }

    private static final Boolean[] ZERO_ARRAY = new Boolean[0];

    @Override
    public Boolean[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
