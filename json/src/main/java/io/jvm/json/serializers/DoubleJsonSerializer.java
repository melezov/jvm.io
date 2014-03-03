package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class DoubleJsonSerializer implements JsonSerializer<Double> {
    @Override
    public boolean isDefault(final Double value) {
        return value == 0.0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Double value) throws IOException {
        if (value.isInfinite() || value.isNaN()) {
            jsonWriter.writeNull();
        } else {
            jsonWriter.writeRaw(value.toString());
        }
    }
}
