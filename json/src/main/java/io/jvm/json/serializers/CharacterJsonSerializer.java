package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

public class CharacterJsonSerializer implements JsonSerializer<Character> {
    @Override
    public boolean isDefault(final Character value) {
        return false;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final Character value) throws IOException {
        jsonWriter.writeCharArray(new char[] { value.charValue() });
    }
}
