package io.jvm.json.deserializers;

import io.jvm.json.JsonDeserializer;
import io.jvm.json.JsonReader;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XmlJsonDeserializer implements JsonDeserializer<Document> {

  private static final Document[] ZERO_ARRAY = new Document[0];

  /* Implementation */
  /* Special tags */
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
   * Constructs an {@link org.w3c.dom.Document} object using the given
   * {@link io.jvm.json.JsonReader}
   *
   * @return The constructed {@link org.w3c.dom.Document} object
   * @throws IOException
   */
  @Override
  public Document fromJson(final JsonReader reader) throws IOException {

    Document document;

    try {
      document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

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
    } catch (ParserConfigurationException ex) {
      document = null; // TODO: Handle the exception
    }

    return document;
  }

  private boolean valueIsObject(JsonReader reader) throws IOException {
    return reader.readToken() == '{';
  }

  private boolean valueIsArray(JsonReader reader) throws IOException {
    return reader.readToken() == '[';
  }

  private boolean isTextContentNode(Node node) {
    return (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.COMMENT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE);
  }

  private void buildTextValue(Document document, Node node, JsonReader reader) throws IOException {
    switch (reader.readToken()) {
    case '"':
      /* String */
      if (isTextContentNode(node)) {
        node.setNodeValue(reader.readString());
      } else
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
   * Recursively builds an XML Element node's subtree from the content of the
   * given JsonReader. The node needs to exist, and the JsonReader needs to be
   * positioned at the Json element's value.
   */
  private void buildNodeValueSubtree(Document document, Node parent, JsonReader reader) throws IOException {
    if (valueIsObject(reader)) {
      buildObjectValueSubtree(document, parent, reader);
    } else if (valueIsArray(reader)) {
      String arrayNodesName = parent.getNodeName();
      Element grandParent = (Element) parent.getParentNode();
      grandParent.removeChild(parent);
      buildArrayValueSubtree(document, grandParent, arrayNodesName, reader);
    } else {
      /* Otherwise the value is string/true/false/null/number or invalid */
      // Note: JSON converted from XML should never have non-string
      // true/false
      buildTextValue(document, parent, reader);
    }
  }

  /**
   * Builds an XML subtree from a JSON object element
   *
   * @param document
   *          the {@link Document} this tree belongs too
   * @param parent
   *          the parent {@link Element} node of the subtree
   * @param reader
   *          the {@link JsonReader} we are currently using to read the Json
   *          stream
   */
  private void buildObjectValueSubtree(Document document, Node parentNode, JsonReader reader) throws IOException {

    reader.assertReadToken('{');

    Element parent = (Element) parentNode;

    boolean needsComma = false;
    while (reader.readToken() != '}') {
      if (needsComma) {
        reader.assertLastToken(',');
        reader.next();
      }

      String childNodeName = reader.readString();
      reader.assertReadToken(':');

      /* If it's an attribute node */
      if (childNodeName.startsWith("@")) {
        String nodeValue = reader.readString();
        parent.setAttribute(childNodeName.substring(1), nodeValue);
      }
      /* If it's a special node */
      else if (childNodeName.startsWith("#")) {
        if (childNodeName.equals(commentNodeTag)) {
          Comment childElement = document.createComment("");
          parent.appendChild(childElement);
          buildNodeValueSubtree(document, childElement, reader);
        } else if (childNodeName.equals(cDataNodeTag)) {
          CDATASection childElement = document.createCDATASection("");
          parent.appendChild(childElement);
          buildNodeValueSubtree(document, childElement, reader);
        } else if (childNodeName.equals(textNodeTag)) {
          Text childElement = document.createTextNode("");
          parent.appendChild(childElement);
          buildNodeValueSubtree(document, childElement, reader);
        } else if (childNodeName.equals(whitespaceNodeTag) || childNodeName.equals(significantWhitespaceNodeTag)) {
          // Ignore
        } else {
          /*
           * All other nodes whose name starts with a '#' are invalid XML nodes,
           * and thus ignored:
           */
        }

      }
      /* If it's a processing instruction */
      else if (childNodeName.startsWith("?")) {
        String processingNodeData = reader.readString();
        parent.appendChild(document.createProcessingInstruction(childNodeName.substring(1), processingNodeData));
      }
      /* If it's a normal node */
      else {
        Element childElement = document.createElement(childNodeName);
        parent.appendChild(childElement);
        buildNodeValueSubtree(document, childElement, reader);
      }

      needsComma = true;
    }
    reader.assertReadToken('}');
  }

  private Node createChildNodeAccordingToName(Document document, String nodeName) {
    Node childNode;

    if (nodeName.equals(textNodeTag))
      childNode = document.createTextNode("");
    else if (nodeName.equals(cDataNodeTag))
      childNode = document.createCDATASection("");
    else if (nodeName.equals(commentNodeTag))
      childNode = document.createComment("");
    else
      childNode = document.createElement(nodeName);

    return childNode;
  }

  /**
   * Builds a sequence of XML elements from a Json array. The first element of
   * the sequence must exist before calling this method
   *
   * E.g. {@code "array":["1","2","3"]} translates to
   * {@code <array>1</array><array>2</array><array>3</array>}
   *
   * @param document
   * @param parent
   * @param reader
   * @throws IOException
   */
  private void buildArrayValueSubtree(Document document, Node parent, String arrayNodesName, JsonReader reader)
      throws IOException {
    reader.assertReadToken('[');

    boolean needsComma = false;
    while (reader.readToken() != ']') {
      if (needsComma) {
        reader.assertLastToken(',');
        reader.next();
      }

      Node childNode = createChildNodeAccordingToName(document, arrayNodesName);

      parent.appendChild(childNode);
      buildNodeValueSubtree(document, childNode, reader);

      needsComma = true;
    }

    reader.assertReadToken(']');
  }
}
