package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class FloatJsonDeserializer implements JsonDeserializer<Float> {
    @Override
    public Float fromJson(final JsonReader jsonReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        jsonReader.readRawNumber(sb);
        return Float.parseFloat(sb.toString());
    }

    private static final Float[] ZERO_ARRAY = new Float[0];

    @Override
    public Float[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
