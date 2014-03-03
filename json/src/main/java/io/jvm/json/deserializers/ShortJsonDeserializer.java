package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class ShortJsonDeserializer implements JsonDeserializer<Short> {
    @Override
    public Short fromJson(final JsonReader jsonReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        jsonReader.readRawNumber(sb);
        return (short) Double.parseDouble(sb.toString());
    }

    private static final Short[] ZERO_ARRAY = new Short[0];

    @Override
    public Short[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
