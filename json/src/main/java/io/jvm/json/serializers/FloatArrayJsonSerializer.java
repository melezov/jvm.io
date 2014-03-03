package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class FloatArrayJsonSerializer implements JsonSerializer<float[]> {
    @Override
    public boolean isDefault(final float[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final float[] values) throws IOException {
        jsonWriter.writeOpenArray();
        boolean needComma = false;
        for (final float value : values) {
            if (needComma) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeRaw(Float.toString(value));
            needComma = true;
        }
        jsonWriter.writeCloseArray();
    }
}
