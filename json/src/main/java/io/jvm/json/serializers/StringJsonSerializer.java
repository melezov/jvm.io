package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class StringJsonSerializer implements JsonSerializer<String> {
    @Override
    public boolean isDefault(final String value) {
        return value.isEmpty();
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final String value) throws IOException {
        jsonWriter.writeString(value);
    }
}
