package io.jvm.json;

import static org.junit.Assert.assertTrue;
import io.jvm.json.deserializers.XmlJsonDeserializer;
import io.jvm.json.serializers.XmlJsonSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.Diff;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Xml2JsonRoundTripTest {

	/**
	 * For each of the XML files in 'resources/roundtripTests/source/xml'
	 * generates the entire roundtrip conversion (xml -> json -> xml) using
	 * io.jvm.json.serializers.XmlJsonSerializer, and asserts the generated XML
	 * equivalence with the reference conversions (obtained by using Json.NET)
	 */
	@Test
	public void assertRoundTripEquivalenceWithReferenceConversion()
			throws URISyntaxException, JSONException, SAXException,
			IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {

		final File xmlSources_dir = getFileForResource("/roundtripTests/source/");				
		
		/* Iterate through the sources directory */
		for (final File xmlSourceFile : xmlSources_dir.listFiles()) {						
			
			/* If perchance this is a directory, skip */
			if (xmlSourceFile.isFile()) {				
				/*
				 * In short, we deal with five files: 
				 * 		- source/source.xml 
				 * 		- converted/source.xml.json 
				 * 		- converted/source.xml.json.xml 
				 * 		- reference/source.xml.json 
				 * 		- reference/source.xml.json
				 */

				final String sourceFilename_xml = xmlSourceFile.getName();
				final String convertedFilename_json = sourceFilename_xml	+ ".json";
				final String roundtripFilename_xml = sourceFilename_xml	+ ".json.xml";
				
				final File referenceFile_json = getFileForResource("/roundtripTests/reference/"+ convertedFilename_json);			
				assertTrue("The reference JSON file does not exist for: " + sourceFilename_xml, (referenceFile_json != null && referenceFile_json.exists()));
				
				final File referenceRoundtripFile_xml = getFileForResource("/roundtripTests/reference/"+ roundtripFilename_xml);								
				assertTrue("The reference XML->JSON roundtrip file does not exist for: " + sourceFilename_xml, (referenceRoundtripFile_xml != null && referenceRoundtripFile_xml.exists()));
				
				final Document source_xml = parseXmlFile(xmlSourceFile);
				final String referenceJson = stringFromFile(referenceFile_json);
				final Document referenceRoundTrip_xml = parseXmlFile(referenceRoundtripFile_xml);															
				
				final String convertedJson = jsonStringFromXml(source_xml);
				
				System.out.println("Converted JSon: ");
				System.out.println(convertedJson);
				
//				saveToFile("roundtripTests/converted/"+convertedFilename_json, convertedJson);
				
				assertJsonEquivalence(convertedJson, referenceJson);				

				//final Document roundtripXmlDocument = xmlDocumentfromJson(convertedJson);																			
				// For when the implementation of the deserializer is complete
				//assertXmlEquivalence("The reference roundtrip XML does not match the converted roundtrip XML",roundtripXmlDocument, referenceRoundTrip_xml);
				//assertXmlEquivalence("The roundtrip XML does not match the source XML",roundtripXmlDocument,source_xml);
				//assertXmlEquivalence("The reference roundtrip XML does not match the source XML",roundtripXmlDocument, source_xml);
				
				/* Save the newly generated files for future reference */
				// TODO: 				
//				saveXmlToFile("roundtripTests/converted/"+roundtripFilename_xml, roundtripXmlDocument);
			}
		}
	}
	
	/**
	 * Saves an XML file to a given resource path  
	 */
	private static void saveXmlToFile(String fileResourcePath, Document xmlDocument) throws URISyntaxException, IOException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError{		
		StringWriter sw = new StringWriter();
		
		TransformerFactory
			.newInstance()
			.newTransformer()
			.transform(new DOMSource(xmlDocument), new StreamResult(sw));
		
		String xmlDocument_string=sw.toString();
		
		saveToFile(fileResourcePath, xmlDocument_string);
	}
	
	/**
	 * Saves a text file to a given resource path 
	 */
	private static void saveToFile(String fileResourcePath, String text) throws URISyntaxException, IOException{
		File targetFile = new File(Xml2JsonRoundTripTest.class.getResource(fileResourcePath).toURI());
		
		if (targetFile.exists() == false)
			targetFile.createNewFile();
				
		BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile.getAbsoluteFile()));
		
		bw.write(text);
		bw.close();		
	}
	
	private static Document xmlDocumentfromJson(String json) throws IOException
	{
		JsonReader jr = new JsonReader(new StringReader(json));
		
		return new XmlJsonDeserializer().fromJson(jr);
	}
	
	private static String jsonStringFromXml(final Document source_xml) throws IOException{
		
		System.out.println("Konvertiramo: "+ source_xml.getDocumentURI());
		
		final StringWriter sw = new StringWriter();
		new XmlJsonSerializer().toJson(new JsonWriter(sw), source_xml.getDocumentElement());
		return sw.toString();
	}
	
	
	/* XXX: Does not care for encoding */
	private static String stringFromFile(File file) throws IOException
	{				
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
	
	private static void assertJsonEquivalence(String lhs, String rhs) throws JSONException
	{			
		JSONAssert.assertEquals(lhs, rhs, false);
	}
	
	private static void assertXmlEquivalence(String message, Document lhs, Document rhs)
	{
		final Diff diff = new Diff(lhs, rhs);
		assertTrue(message, diff.similar());
	}
	
	private static File getFileForResource(String resourcePath) throws URISyntaxException
	{		
		final URL resourceURL = Xml2JsonRoundTripTest.class.getResource(resourcePath);				
		
		if (resourceURL==null) 
			return null;
		else
			return new File(resourceURL.toURI());
		
	}
	
	private static Document parseXmlFile(final File file)
			throws SAXException, IOException, ParserConfigurationException {				
		
		final Document doc =	DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(file);
		doc.getDocumentElement().normalize();
		
		return doc;
	}		
}
