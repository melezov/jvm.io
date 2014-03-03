package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class LongArrayJsonSerializer implements JsonSerializer<long[]> {
    @Override
    public boolean isDefault(final long[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final long[] values) throws IOException {
        jsonWriter.writeOpenArray();
        boolean needComma = false;
        for (final long value : values) {
            if (needComma) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeRaw(Long.toString(value));
            needComma = true;
        }
        jsonWriter.writeCloseArray();
    }
}
