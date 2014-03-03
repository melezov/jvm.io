package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.util.UUID;

public class UUIDJsonDeserializer implements JsonDeserializer<UUID> {
    @Override
    public UUID fromJson(final JsonReader jsonReader) throws IOException {
        return UUID.fromString(jsonReader.readString());
    }

    private static final UUID[] ZERO_ARRAY = new UUID[0];

    @Override
    public UUID[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
