package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

import org.joda.time.DateTime;

public class DateTimeJsonDeserializer implements JsonDeserializer<DateTime> {
    @Override
    public DateTime fromJson(final JsonReader jsonReader) throws IOException {
        return new DateTime(jsonReader.readString());
    }

    private static final DateTime[] ZERO_ARRAY = new DateTime[0];

    @Override
    public DateTime[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
