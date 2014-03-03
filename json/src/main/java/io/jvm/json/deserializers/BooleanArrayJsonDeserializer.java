package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

public class BooleanArrayJsonDeserializer implements JsonDeserializer<boolean[]> {
    @Override
    public boolean[] fromJson(final JsonReader jsonReader) throws IOException {
        jsonReader.assertRead('[');

        final ArrayList<Boolean> values = new ArrayList<Boolean>();
        boolean needComma = false;
        while (jsonReader.read() != ']') {
            if (needComma) {
                jsonReader.assertLast(',');
                jsonReader.next();
            }

            final char ch = jsonReader.read();
            if (ch == 't') values.add(jsonReader.readTrue()); else
            if (ch == 'f') values.add(jsonReader.readFalse()); else
            throw new IOException("Could not parse boolean, expected 'true' or 'false', but token begins with '" + ch + "'");

            needComma = true;
        }
        jsonReader.invalidate();

        final boolean[] result = new boolean[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static final boolean[][] ZERO_ARRAY = new boolean[0][];

    @Override
    public boolean[][] getZeroArray() {
        return ZERO_ARRAY;
    }
}
