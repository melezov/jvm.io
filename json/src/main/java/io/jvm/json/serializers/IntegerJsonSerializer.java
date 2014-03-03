package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class IntegerJsonSerializer implements JsonSerializer<Integer> {
    @Override
    public boolean isDefault(final Integer value) {
        return value == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Integer value) throws IOException {
        jsonWriter.writeRaw(value.toString());
    }
}
