package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class DoubleJsonDeserializer implements JsonDeserializer<Double> {
    @Override
    public Double fromJson(final JsonReader jsonReader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        jsonReader.readRawNumber(sb);
        return Double.parseDouble(sb.toString());
    }

    private static final Double[] ZERO_ARRAY = new Double[0];

    @Override
    public Double[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
