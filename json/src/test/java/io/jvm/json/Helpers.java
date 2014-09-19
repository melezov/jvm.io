package io.jvm.json;

import io.jvm.json.deserializers.XmlJsonDeserializer;
import io.jvm.json.serializers.XmlJsonSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Helpers {

    public static File getFileForResource(String resourcePath) throws URISyntaxException {
	final URL resourceURL = Xml2JsonRoundTripTest.class.getResource(resourcePath);

	if (resourceURL == null)
	    return null;
	else
	    return new File(resourceURL.toURI());

    }

    public static Document parseXmlFile(final File file) throws SAXException, IOException, ParserConfigurationException {

	final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

	doc.normalizeDocument();

	return doc;
    }

    public static String stringFromFile(File file) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(file));
	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
		sb.append(line);
		sb.append("\n");
		line = br.readLine();
	    }
	    return sb.toString();
	} finally {
	    br.close();
	}
    }

    public static void printDocumentTree(Node el) {
	System.out.println(el.toString());
	for (int i = 0; i < el.getChildNodes().getLength(); i++)
	    printDocumentTree(el.getChildNodes().item(i));
    }

    public static void printXmlDocument(Document doc) {
	System.out.println(xmlDocumentToString(doc));
    }

    public static String xmlDocumentToString(Document doc) {
	try {
	    DOMSource domSource = new DOMSource(doc);
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.transform(domSource, result);
	    return writer.toString();
	} catch (TransformerException ex) {
	    ex.printStackTrace();
	    return null;
	}
    }

    public static Document xmlDocumentFromJson(String json) throws IOException {
	JsonReader jr = new JsonReader(new StringReader(json));

	System.out.println("Deserializing json: ");
	System.out.println(json);
	
	return new XmlJsonDeserializer().fromJson(jr);
    }

    public static String jsonStringFromXml(final Document source_xml) throws IOException {
	final StringWriter sw = new StringWriter();
	new XmlJsonSerializer().toJson(new JsonWriter(sw), source_xml.getDocumentElement());
	return sw.toString();
    }

}
