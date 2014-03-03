package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class StringJsonDeserializer implements JsonDeserializer<String> {
    @Override
    public String fromJson(final JsonReader jsonReader) throws IOException {
        return jsonReader.readString();
    }

    private static final String[] ZERO_ARRAY = new String[0];

    @Override
    public String[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
