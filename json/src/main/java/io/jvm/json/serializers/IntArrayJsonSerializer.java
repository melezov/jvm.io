package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class IntArrayJsonSerializer implements JsonSerializer<int[]> {
    @Override
    public boolean isDefault(final int[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final int[] values) throws IOException {
        jsonWriter.writeOpenArray();
        boolean needComma = false;
        for (final int value : values) {
            if (needComma) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeRaw(Integer.toString(value));
            needComma = true;
        }
        jsonWriter.writeCloseArray();
    }
}
