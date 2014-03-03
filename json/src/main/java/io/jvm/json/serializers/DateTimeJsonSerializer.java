package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

import org.joda.time.DateTime;

public class DateTimeJsonSerializer implements JsonSerializer<DateTime> {
    @Override
    public boolean isDefault(final DateTime value) {
        return false;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final DateTime value) throws IOException {
        jsonWriter.writeString(value.toString());
    }
}
