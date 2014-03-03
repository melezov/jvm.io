package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class CharArrayJsonSerializer implements JsonSerializer<char[]> {
    @Override
    public boolean isDefault(final char[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final char[] values) throws IOException {
        jsonWriter.writeCharArray(values);
    }
}
