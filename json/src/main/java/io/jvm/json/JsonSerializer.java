package io.jvm.json;

import java.io.IOException;

public interface JsonSerializer<T> {
    public boolean isDefault(final T value);

    public void toJson(final JsonWriter jsonWriter, final T value) throws IOException;
}
