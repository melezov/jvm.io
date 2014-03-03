package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

public class ShortArrayJsonDeserializer implements JsonDeserializer<short[]> {
    @Override
    public short[] fromJson(final JsonReader jsonReader) throws IOException {
        jsonReader.assertRead('[');

        final StringBuilder sb = new StringBuilder();
        final ArrayList<String> values = new ArrayList<String>();
        boolean needComma = false;
        while (jsonReader.read() != ']') {
            if (needComma) {
                jsonReader.assertLast(',');
                sb.setLength(0);
                jsonReader.next();
            }

            values.add(jsonReader.readRawNumber(sb).toString());
            needComma = true;
        }
        jsonReader.invalidate();

        final short[] result = new short[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (short) Double.parseDouble(values.get(i));
        }
        return result;
    }

    private static final short[][] ZERO_ARRAY = new short[0][];

    @Override
    public short[][] getZeroArray() {
        return ZERO_ARRAY;
    }
}
