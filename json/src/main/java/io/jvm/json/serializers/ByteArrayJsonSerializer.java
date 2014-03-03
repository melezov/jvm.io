package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

public class ByteArrayJsonSerializer implements JsonSerializer<byte[]> {
    @Override
    public boolean isDefault(final byte[] value) {
        return value.length == 0;
    }

    @Override
    public void toJson(final JsonWriter jsonWriter, final byte[] values) throws IOException {
        jsonWriter.writeRaw('"');
        jsonWriter.writeRaw(DatatypeConverter.printBase64Binary(values));
        jsonWriter.writeRaw('"');
    }
}
