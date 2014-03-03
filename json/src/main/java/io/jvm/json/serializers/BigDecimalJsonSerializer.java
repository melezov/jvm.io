package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalJsonSerializer implements JsonSerializer<BigDecimal> {
    @Override
    public boolean isDefault(final BigDecimal value) {
        return value.equals(BigDecimal.ZERO);
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final BigDecimal value) throws IOException {
        jsonWriter.writeString(value.toString());
    }
}
