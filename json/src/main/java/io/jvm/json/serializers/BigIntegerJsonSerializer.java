package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerJsonSerializer implements JsonSerializer<BigInteger> {
    @Override
    public boolean isDefault(final BigInteger value) {
        return value.equals(BigInteger.ZERO);
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final BigInteger value) throws IOException {
        jsonWriter.writeString(value.toString());
    }
}
