package io.jvm.json.deserializers;

import java.io.IOException;
import java.util.UUID;

import org.w3c.dom.Document;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

public class XmlJsonDeserializer implements JsonDeserializer<Document>{

	private static final Document[] ZERO_ARRAY = new Document[0];
	
	@Override
	public Document[] getZeroArray() {
		return ZERO_ARRAY;
	}

	@Override
	public Document fromJson(JsonReader jsonReader) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
