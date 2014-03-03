package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class LongJsonDeserializer implements JsonDeserializer<Long> {
    @Override
    public Long fromJson(final JsonReader jsonReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        jsonReader.readRawNumber(sb);
        return (long) Double.parseDouble(sb.toString());
    }

    private static final Long[] ZERO_ARRAY = new Long[0];

    @Override
    public Long[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
