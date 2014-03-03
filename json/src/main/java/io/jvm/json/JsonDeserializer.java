package io.jvm.json;

import java.io.IOException;

public interface JsonDeserializer<T> {
    public T[] getZeroArray();

    public T fromJson(final JsonReader jsonReader) throws IOException;
}
