package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class LongJsonSerializer implements JsonSerializer<Long> {
    @Override
    public boolean isDefault(final Long value) {
        return value == 0L;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Long value) throws IOException {
        jsonWriter.writeRaw(value.toString());
    }
}
