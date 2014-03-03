package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class ShortJsonSerializer implements JsonSerializer<Short> {
    @Override
    public boolean isDefault(final Short value) {
        return value == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Short value) throws IOException {
        jsonWriter.writeRaw(value.toString());
    }
}
