package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlJsonDeserializer implements JsonDeserializer<Document>{
		
	private static final Document[] ZERO_ARRAY = new Document[0];
	
	/* Implementation */
	private final String textNodeTag = "#text";
	private final String commentNodeTag = "#comment";
	private final String cDataNodeTag = "#cdata-section";
	private final String whitespaceNodeTag = "#whitespace";
	private final String significantWhitespaceNodeTag = "#significant-whitespace";
	private final String declarationNodeTag = "?xml";	
	private final String xmlnsURL = "http://www.w3.org/2000/xmlns/";
	private final String xmlnsPrefix = "xmlns";
		
	private final String jsonNamespaceUri = "http://james.newtonking.com/projects/json";
	private final String jsonArrayAttribute = "Array";
	
	@Override
	public Document[] getZeroArray() {
		return ZERO_ARRAY;
	}
	
	/**
	 * Constructs an {@link org.w3c.dom.Document} 
	 * object using the given {@link io.jvm.json.JsonReader}
	 * @return The constructed {@link org.w3c.dom.Document} object
	 * @throws IOException
	 */
	@Override
	public Document fromJson(final JsonReader reader) throws IOException {
		
		Document document;
		
		try{
			document = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.newDocument();
			
			/* The document must have a single Json node */
			reader.assertReadToken('{');
			
			/* The first element is the root node */
			String rootElementName = reader.readString();
			
			Element root = document.createElement(rootElementName);			
			document.appendChild(root);
			
			reader.assertReadToken(':');
			
			buildNodeValueSubtree(document, root, reader);
			
			reader.assertReadToken('}');
			reader.consumeWhitespaces();
			reader.assertEndOfStream();
		}
		catch(ParserConfigurationException ex){
			document = null; // TODO: Handle the exception, this is just until we implement the deserializer
		}
		
		return document;
	}		
	
	private boolean valueIsObject(JsonReader reader) throws IOException{	    	
		return reader.readToken() == '{';		 
	}
	
	private boolean valueIsArray(JsonReader reader)  throws IOException{	    	
		return reader.readToken() == '['; 
	}
	
	private void buildTextValue(Document document, Element node, JsonReader reader) throws IOException{		
		switch(reader.readToken()){
		case '"':
			/* String */
			node.appendChild(document.createTextNode(reader.readString()));
			break;
		case 't':		
			/* true */
			node.setNodeValue(new Boolean(reader.readTrue()).toString());
			break;		
		case 'f':
			/* false */
			node.setNodeValue(new Boolean(reader.readFalse()).toString());
			break;
		case 'n':
			/* null */
			reader.readNull();
			node.setNodeValue(null);
			break;
		default:
			/* Number or invalid */
			node.setNodeValue(reader.readRawNumber(new StringBuilder()).toString());
			break;
		}
	}	
	
	/**
	 * Recursively builds an XML Element node's subtree from the content of the given JsonReader.
	 * The node needs to exist, and the JsonReader needs to be positioned at the Json element's value. 
	 */
	private void buildNodeValueSubtree(Document document, Element parent, JsonReader reader) throws IOException{					
		if(valueIsObject(reader)){
			buildObjectValueSubtree(document,parent,reader);
		}		
		else if(valueIsArray(reader)){
			String arrayNodesName=parent.getNodeName();
			Element grandParent = (Element)parent.getParentNode();			
			grandParent.removeChild(parent);
			buildArrayValueSubtree(document, grandParent, arrayNodesName, reader);
		}		
		else{
			/* Otherwise the value is string/true/false/null/number or invalid*/
			// Note: JSON converted from XML should never have non-string true/false/null
			buildTextValue(document, parent,reader);
		}
	}
	
	/**
	 * Builds an XML subtree from a JSON object element
	 * @param document the {@link Document} this tree belongs too
	 * @param parent the parent {@link Element} node of the subtree 
	 * @param reader the {@link JsonReader} we are currently using to read the Json stream 
	 */
	private void buildObjectValueSubtree(Document document, Element parent, JsonReader reader) throws IOException{
		reader.assertReadToken('{');
		
		boolean needsComma=false;
		while (reader.readToken() != '}') {
			if(needsComma){
				reader.assertLastToken(',');
				reader.next();
			}
			
			String childNodeName = reader.readString();			
			reader.assertReadToken(':');
			
			/* If it's an attribute node */
			if(childNodeName.startsWith("@")){
				String nodeValue = reader.readString();
				parent.setAttribute(childNodeName.substring(1), nodeValue);
			}
			/* If it's a special node */
			else if(childNodeName.startsWith("#")){
				if(childNodeName.equals(commentNodeTag)){
					String commentText = reader.readString();
					parent.appendChild(document.createComment(commentText));
				}
				else if(childNodeName.equals(cDataNodeTag)){
					String cDataText = reader.readString();
					parent.appendChild(document.createCDATASection(cDataText));
				}
				else if(childNodeName.equals(textNodeTag)){
					String textNodeData = reader.readString();
					parent.appendChild(document.createTextNode(textNodeData));
				}					
			}
			/* If it's a normal node */
			else{
				Element childElement = document.createElement(childNodeName);
				parent.appendChild(childElement);				
				buildNodeValueSubtree(document, childElement, reader);
			}
			
			needsComma=true;
		}
		reader.assertReadToken('}');
	}
	
	/**
	 * Builds a sequence of XML elements from a Json array. The first element of the sequence must
	 * exist before calling this method
	 * 
	 * E.g. {@code "array":["1","2","3"]} translates to {@code <array>1</array><array>2</array><array>3</array>}
	 * 
	 * @param document
	 * @param parent
	 * @param reader
	 * @throws IOException
	 */
	private void buildArrayValueSubtree(Document document, Element parent, String arrayNodesName, JsonReader reader) throws IOException{
		reader.assertReadToken('[');
		
		boolean needsComma = false;
		while(reader.readToken()!=']'){
			if(needsComma){
				reader.assertLastToken(',');
				reader.next();
			}
			
			Element childNode = document.createElement(arrayNodesName);
			parent.appendChild(childNode);
			buildNodeValueSubtree(document, childNode, reader);			
			
			needsComma=true;
		}
		
		reader.assertReadToken(']');
	}
}
