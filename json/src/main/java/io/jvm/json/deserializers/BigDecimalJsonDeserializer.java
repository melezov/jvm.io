package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalJsonDeserializer implements JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal fromJson(final JsonReader jsonReader) throws IOException {
        return new BigDecimal(jsonReader.readString());
    }

    private static final BigDecimal[] ZERO_ARRAY = new BigDecimal[0];

    @Override
    public BigDecimal[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
