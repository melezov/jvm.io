package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class IntegerJsonDeserializer implements JsonDeserializer<Integer> {
    @Override
    public Integer fromJson(final JsonReader jsonReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        jsonReader.readRawNumber(sb);
        return (int) Double.parseDouble(sb.toString());
    }

    private static final Integer[] ZERO_ARRAY = new Integer[0];

    @Override
    public Integer[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
