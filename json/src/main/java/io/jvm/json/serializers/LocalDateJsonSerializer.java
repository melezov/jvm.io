package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

import org.joda.time.LocalDate;

public class LocalDateJsonSerializer implements JsonSerializer<LocalDate> {
    @Override
    public boolean isDefault(final LocalDate value) {
        return false;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final LocalDate value) throws IOException {
        jsonWriter.writeString(value.toString());
    }
}
