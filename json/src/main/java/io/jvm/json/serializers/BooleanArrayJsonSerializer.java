package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class BooleanArrayJsonSerializer implements JsonSerializer<boolean[]> {
    @Override
    public boolean isDefault(final boolean[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final boolean[] values) throws IOException {
        jsonWriter.writeOpenArray();
        boolean needComma = false;
        for (final boolean value : values) {
            if (needComma) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeRaw(Boolean.toString(value));
            needComma = true;
        }
        jsonWriter.writeCloseArray();
    }
}
