package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

public class CharacterJsonDeserializer implements JsonDeserializer<Character> {
    @Override
    public Character fromJson(final JsonReader jsonReader) throws IOException {
        final String charString = jsonReader.readString();
        if (charString.isEmpty()) throw new IOException("Could not parse char, got empty string!");
        if (charString.length() > 1) throw new IOException("Could not parse char, expected a single character, got '" + charString + "'");
        return charString.charAt(0);
    }

    private static final Character[] ZERO_ARRAY = new Character[0];

    @Override
    public Character[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
