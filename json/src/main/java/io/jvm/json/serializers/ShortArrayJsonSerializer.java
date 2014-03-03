package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class ShortArrayJsonSerializer implements JsonSerializer<short[]> {
    @Override
    public boolean isDefault(final short[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final short[] values) throws IOException {
        jsonWriter.writeOpenArray();
        boolean needComma = false;
        for (final short value : values) {
            if (needComma) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeRaw(Short.toString(value));
            needComma = true;
        }
        jsonWriter.writeCloseArray();
    }
}
