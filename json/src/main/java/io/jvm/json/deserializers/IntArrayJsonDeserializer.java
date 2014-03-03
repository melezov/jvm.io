package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

public class IntArrayJsonDeserializer implements JsonDeserializer<int[]> {
    @Override
    public int[] fromJson(final JsonReader jsonReader) throws IOException {
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

        final int[] result = new int[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) Double.parseDouble(values.get(i));
        }
        return result;
    }

    private static final int[][] ZERO_ARRAY = new int[0][];

    @Override
    public int[][] getZeroArray() {
        return ZERO_ARRAY;
    }
}
