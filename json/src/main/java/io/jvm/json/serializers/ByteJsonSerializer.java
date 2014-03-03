package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class ByteJsonSerializer implements JsonSerializer<Byte> {
    @Override
    public boolean isDefault(final Byte value) {
        return value == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Byte value) throws IOException {
        jsonWriter.writeRaw(value.toString());
    }
}
