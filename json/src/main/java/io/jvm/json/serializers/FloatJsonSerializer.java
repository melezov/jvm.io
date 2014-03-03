package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class FloatJsonSerializer implements JsonSerializer<Float> {
    @Override
    public boolean isDefault(final Float value) {
        return value == 0.0f;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Float value) throws IOException {
        if (value.isInfinite() || value.isNaN()) {
            jsonWriter.writeNull();
        } else {
            jsonWriter.writeRaw(value.toString());
        }
    }
}
