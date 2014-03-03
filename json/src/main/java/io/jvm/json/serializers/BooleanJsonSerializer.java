package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class BooleanJsonSerializer implements JsonSerializer<Boolean> {
    @Override
    public boolean isDefault(final Boolean value) {
        return value == Boolean.FALSE;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Boolean value) throws IOException {
        jsonWriter.writeRaw(value.toString());
    }
}
