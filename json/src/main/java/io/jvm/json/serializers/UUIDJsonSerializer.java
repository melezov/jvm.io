package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;
import java.util.UUID;

public class UUIDJsonSerializer implements JsonSerializer<UUID> {
    @Override
    public boolean isDefault(final UUID value) {
        return false;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final UUID value) throws IOException {
        jsonWriter.writeString(value.toString());
    }
}
