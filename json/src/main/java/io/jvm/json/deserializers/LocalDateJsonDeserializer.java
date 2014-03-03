package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class LocalDateJsonDeserializer implements JsonDeserializer<LocalDate> {
    @Override
    public LocalDate fromJson(final JsonReader jsonReader) throws IOException {
        return new DateTime(jsonReader.readString()).toLocalDate();
    }

    private static final LocalDate[] ZERO_ARRAY = new LocalDate[0];

    @Override
    public LocalDate[] getZeroArray() {
        return ZERO_ARRAY;
    }
}
