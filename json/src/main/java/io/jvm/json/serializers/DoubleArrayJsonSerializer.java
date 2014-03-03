package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class DoubleArrayJsonSerializer implements JsonSerializer<double[]> {
    @Override
    public boolean isDefault(final double[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final double[] values) throws IOException {
        jsonWriter.writeOpenArray();
        boolean needComma = false;
        for (final double value : values) {
            if (needComma) {
                jsonWriter.writeComma();
            }
            jsonWriter.writeRaw(Double.toString(value));
            needComma = true;
        }
        jsonWriter.writeCloseArray();
    }
}
