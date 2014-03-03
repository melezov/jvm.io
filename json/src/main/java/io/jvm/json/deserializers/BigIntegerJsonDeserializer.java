package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerJsonDeserializer implements JsonDeserializer<BigInteger> {
    @Override
    public BigInteger fromJson(final JsonReader jsonReader) throws IOException {
        return new BigInteger(jsonReader.readString());
    }

    private static final BigInteger[] ZERO_ARRAY = new BigInteger[0];

    @Override
    public BigInteger[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
