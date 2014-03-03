package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

public class LongArrayJsonDeserializer implements JsonDeserializer<long[]> {
    @Override
    public long[] fromJson(final JsonReader jsonReader) throws IOException {
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

        final long[] result = new long[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (long) Double.parseDouble(values.get(i));
        }
        return result;
    }

    private static final long[][] ZERO_ARRAY = new long[0][];

    @Override
    public long[][] getZeroArray() {
        return ZERO_ARRAY;
    }
}
