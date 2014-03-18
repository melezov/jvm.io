package io.jvm.json.serializers;

import io.jvm.json.JsonSerializer;
import io.jvm.json.JsonWriter;
import io.jvm.xml.XmlNamespaceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Converts an org.w3c.dom.Element to JSON, matching the scheme used in the
 * serialization library Json.Net, Version 6.0.1 (@see documentation at <a
 * href="http://james.newtonking.com/json">Json.NET</a>)
 */
public class XmlJsonSerializer implements JsonSerializer<Element> {

    /* Interface methods */

    @Override
    public boolean isDefault(Element value) {
	return false;
    }

    @Override
    public void toJson(JsonWriter jsonWriter, Element value) throws IOException {
	if (value != null)
	    writeJson(jsonWriter, value);
    }

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

    // This is only a partial reimplementation, so the following fields are
    // fixed and made private:
    private final String deserializeRootElementName = null;
    private final boolean writeArrayAttribute = false;
    private final boolean omitRootObject = false;

    /* State info */
    private boolean needsComma = false;
    private boolean writingObject = false;
    private boolean writingArray = false;

    /**
     * Write a Json representation of a given org.w3c.dom.Element.
     * 
     * @param writer
     *            The writer to write to.
     * @param element
     *            The element to be converted.
     * @throws IOException
     */
    private void writeJson(final JsonWriter writer, final Element element)
	    throws IOException {

	trimWhitespaceTextNodes(element.getOwnerDocument());

	final XmlNamespaceManager manager = new XmlNamespaceManager();
	pushParentNamespaces(element, manager);

	if (!omitRootObject)
	    writeOpenObjectWithStateInfo(writer);

	// writeXmlDeclaration(writer,element.getOwnerDocument(),manager);

	serializeNode(writer, element, manager, !omitRootObject);

	if (!omitRootObject)
	    writeCloseObjectWithStateInfo(writer);
    }

    private static void trimWhitespaceTextNodes(final org.w3c.dom.Node node) {
	if (node != null && node.getChildNodes() != null)
	    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
		final org.w3c.dom.Node child = node.getChildNodes().item(i);
		if (child.getNodeType() == org.w3c.dom.Node.TEXT_NODE
			&& child.getNodeValue().trim().length() == 0)
		    node.removeChild(child);
		trimWhitespaceTextNodes(node.getChildNodes().item(i));
	    }
    }

    private void pushParentNamespaces(final Element element,
	    final XmlNamespaceManager manager) {
	final List<Element> parentElements = new ArrayList<Element>();

	/* Collect all ancestors from the document tree for the current element */
	Node parent = element;
	while (parent.getParentNode() != null) {
	    parent = parent.getParentNode();
	    if (parent.getNodeType() == Node.ELEMENT_NODE)
		parentElements.add((Element) parent);
	}

	/* If the current element has ancestors */
	if (parentElements.isEmpty() == false) {

	    /*
	     * Add a namespace scope for each ancestor of the current node, top
	     * to bottom
	     */
	    Collections.reverse(parentElements);
	    for (final Element parentElement : parentElements) {

		manager.pushScope();

		/*
		 * Find if there are any namespace attributes, and add them to
		 * the current namespace scope
		 */
		final NamedNodeMap attributes = parentElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
		    final Attr attribute = (Attr) attributes.item(i);
		    if (equalsWithNull(attribute.getNamespaceURI(), xmlnsURL)
			    && (equalsWithNull(resolveLocalName(attribute),
				    xmlnsPrefix) == false)) {
			manager.addNamespace(resolveLocalName(attribute),
				attribute.getValue());
		    }
		}
	    }
	}
    }

    private String resolveLocalName(Node node) {

	if (node.getLocalName() == null && node.getPrefix() == null)
	    return node.getNodeName();
	else
	    return node.getLocalName();
    }

    /**
     * Returns the full name of the given node, along with the prefix resolved
     * from the namespace manager
     */
    private String resolveFullName(final Node node,
	    final XmlNamespaceManager manager) {

	String prefix;
	String fullName;

	/*
	 * If it's an xmlns node with the default xmlnsURL namespace, prefix is
	 * null, otherwise we pull it out from the namespace manager
	 */
	if (node.getNamespaceURI() == null
		|| (equalsWithNull(resolveLocalName(node), xmlnsPrefix) && equalsWithNull(
			node.getNamespaceURI(), xmlnsURL)))
	    prefix = null;
	else
	    prefix = manager.lookupPrefix(node.getNamespaceURI());

	/*
	 * If the prefix is null or empty we return just the local name, without
	 * any prepended prefix
	 */
	if (prefix == null || equalsWithNull(prefix, ""))
	    fullName = resolveLocalName(node);
	/* Otherwise we return the full prefix:localName string */
	else
	    fullName = prefix + ":" + resolveLocalName(node);

	return fullName;
    }

    /**
     * Returns one of the string constants names for the type of the given node
     * type. throws IOException if type is unknown.
     * 
     * @throws IOException
     */
    private String getPropertyName(final Node node,
	    final XmlNamespaceManager manager) throws IOException {
	switch (node.getNodeType()) {
	case Node.ATTRIBUTE_NODE:
	    if (equalsWithNull(node.getNamespaceURI(), jsonNamespaceUri))
		return "$" + resolveLocalName(node);
	    else
		return "@" + resolveFullName(node, manager);
	case Node.CDATA_SECTION_NODE:
	    return cDataNodeTag;
	case Node.COMMENT_NODE:
	    return commentNodeTag;
	case Node.ELEMENT_NODE:
	    return resolveFullName(node, manager);
	case Node.PROCESSING_INSTRUCTION_NODE:
	    return "?" + resolveFullName(node, manager);
	    // XXX: The .NET System.Xml.XmlNodeType and org.w3c.dom.Node
	    // enumerations are not mapped 1:1
	    // case Node.XmlDeclaration:
	    // return DeclarationName;
	    // case Node.SignificantWhitespace:
	    // return SignificantWhitespaceName;
	case Node.TEXT_NODE:
	    return textNodeTag;
	    // case Node.Whitespace:
	    // return WhitespaceName;
	default:
	    throw new IOException("Unexpected Node when getting node name: "
		    + node.getNodeType());
	}
    }

    /**
     * Returns true if this node has the property json:Array from
     * jsonNamespaceUri set to 'true', false otherwise
     */
    private boolean isArray(final Node node) {
	final NamedNodeMap attributes = node.getAttributes();

	if (attributes == null || attributes.getLength() == 0)
	    return false;
	else {
	    /*
	     * Return true the attribute json:Array from the jsonNamespaceUri is
	     * 'true':
	     */
	    for (int i = 0; i < attributes.getLength(); i++) {
		final Attr attribute = (Attr) attributes.item(i);
		if (equalsWithNull(attribute.getNamespaceURI(),
			jsonNamespaceUri)
			&& equalsWithNull(resolveLocalName(attribute),
				jsonArrayAttribute)
			&& Boolean.parseBoolean(attribute.getValue()))
		    return true;
	    }
	}

	return false;
    }

    /**
     * For a given root node returns a map grouping all it's children by node
     * name; Map<nodename, nodes_having_nodename> If the node has no children,
     * an empty map is returned.
     * 
     * @throws IOException
     */
    private Map<String, List<Node>> getNodesGroupedByName(final Node root,
	    final XmlNamespaceManager manager) throws IOException {

	final Map<String, List<Node>> nodesGroupedByName = new HashMap<String, List<Node>>();

	final NodeList children = root.getChildNodes();

	for (int i = 0; i < children.getLength(); i++) {
	    /* Get the i-th child */
	    final Node child = children.item(i);

	    /* Get the name of the current child */
	    String nodeName = getPropertyName(child, manager);

	    /*
	     * If the node list is not instantiated for this nodeName, make an
	     * instance
	     */
	    if (nodesGroupedByName.get(nodeName) == null)
		nodesGroupedByName.put(nodeName, new ArrayList<Node>());

	    /* Add the node to the list for this nodeName */
	    nodesGroupedByName.get(nodeName).add(child);
	}

	return nodesGroupedByName;
    }

    private void serializeArray(final JsonWriter writer,
	    final String nameOfTheElements, final List<Node> nodes,
	    final XmlNamespaceManager manager, boolean writePropertyName)
	    throws IOException {

	if (writePropertyName) {
	    writePropertyNameAndColon(writer, nameOfTheElements);
	}

	writeOpenArrayWithStateInfo(writer);

	for (int i = 0; i < nodes.size(); i++) {
	    serializeNode(writer, nodes.get(i), manager, false);
	}

	writeCloseArrayWithStateInfo(writer);
    }

    /**
     * True if all children's local name is equal to node's local name.
     */
    private boolean allChildrenHaveSameLocalName(final Node node) {

	final NodeList children = node.getChildNodes();

	for (int i = 0; i < children.getLength(); i++) {
	    final Node child = children.item(i);
	    if (equalsWithNull(resolveLocalName(child), resolveLocalName(node)) == false)
		return false;
	}

	return true;
    }

    @Deprecated
    // /XXX: incomplete, does not add a comma after closing tag
    private void writeXmlDeclaration(final JsonWriter writer,
	    final Document document, final XmlNamespaceManager manager)
	    throws IOException {
	/* Deserializirat deklaraciju, iako ovo zvuči krivo staviti ovdje */
	writePropertyNameAndColon(writer, declarationNodeTag);
	writeOpenObjectWithStateInfo(writer);
	if (document.getXmlVersion() != null) {
	    writePropertyNameAndColon(writer, "@version");
	    writePropertyValue(writer, document.getXmlVersion());
	}
	writer.writeComma();
	if (document.getXmlVersion() != null) {
	    writePropertyNameAndColon(writer, "@encoding");
	    writePropertyValue(writer, document.getXmlEncoding());
	}
	writer.writeComma();
	if (document.getXmlVersion() != null) {
	    writePropertyNameAndColon(writer, "@standalone");
	    writePropertyValue(writer, document.getXmlStandalone() ? "yes"
		    : "no");
	}
	writeCloseObjectWithStateInfo(writer);
    }

    private void serializeNode(final JsonWriter writer, final Node node,
	    final XmlNamespaceManager manager, final boolean writePropertyName)
	    throws IOException {
	switch (node.getNodeType()) {
	case Node.DOCUMENT_NODE:
	case Node.DOCUMENT_FRAGMENT_NODE:
	    serializeGroupedNodes(writer, node, manager, writePropertyName);
	    break;
	case Node.ELEMENT_NODE:
	    if (node.hasChildNodes() && isArray(node)
		    && allChildrenHaveSameLocalName(node))
		serializeGroupedNodes(writer, node, manager, false);
	    else {

		/*
		 * Add a new namespace scope and look for namespace attributes
		 * to add
		 */
		manager.pushScope();

		final NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {

		    final Attr attribute = (Attr) attributes.item(i);

		    if (equalsWithNull(attribute.getNamespaceURI(), xmlnsURL)) {

			if (equalsWithNull(resolveLocalName(attribute),
				xmlnsPrefix))
			    manager.addNamespace("", attribute.getValue());
			else
			    manager.addNamespace(resolveLocalName(attribute),
				    attribute.getValue());

		    }
		}

		if (writePropertyName) {
		    writePropertyNameAndColon(writer,
			    getPropertyName(node, manager));
		}

		if (hasSingleTextChild(node))
		    writePropertyValue(writer, node.getChildNodes().item(0).getNodeValue());
		else if (nodeIsEmpty(node)) {
		    writer.writeNull();
		} else {
		    writeOpenObjectWithStateInfo(writer);

		    /* First serialize the attributes */

		    for (int i = 0; i < node.getAttributes().getLength(); i++)
			serializeNode(writer, node.getAttributes().item(i),
				manager, true);

		    /* Then serialize the nodes by groups */
		    serializeGroupedNodes(writer, node, manager, true);

		    writeCloseObjectWithStateInfo(writer);
		}

		manager.popScope();
	    }
	    break;
	case Node.COMMENT_NODE: // Different than in Json.NET, since there
				// comments are serialized as JavaScript
				// comments '/*...*/'
	case Node.ATTRIBUTE_NODE:
	case Node.TEXT_NODE:
	case Node.CDATA_SECTION_NODE:
	case Node.PROCESSING_INSTRUCTION_NODE:
	    if (equalsWithNull(node.getNamespaceURI(), xmlnsURL)
		    && equalsWithNull(node.getNodeValue(), jsonNamespaceUri))
		return;

	    if (equalsWithNull(node.getNamespaceURI(), jsonNamespaceUri)) {
		if (equalsWithNull(resolveLocalName(node), jsonArrayAttribute))
		    return;
	    }

	    if (writePropertyName) {
		writePropertyNameAndColon(writer,
			getPropertyName(node, manager));
	    }
	    writePropertyValue(writer, node.getNodeValue());

	    break;
	// Not covered by org.w2c.dom specs:
	// case XmlNodeType.Whitespace: <-- handled by TEXT_NODE
	// case XmlNodeType.SignificantWhitespace: <-- handled by TEXT_NODE
	// XmlDeclaration is skipped, since we are converting only xml nodes,
	// never entire documents
	default:
	    throw new IOException(
		    "Unexpected XmlNodeType when serializing nodes: "
			    + node.getNodeType());
	}

    }

    private boolean hasSingleTextChild(final Node node) {
	return hasNoValueAttributes(node)
		&& node.getChildNodes().getLength() == 1
		&& node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE;
    }

    private boolean hasNoValueAttributes(final Node node) {

	final NamedNodeMap attributes = node.getAttributes();

	for (int i = 0; i < attributes.getLength(); i++) {
	    final Attr attr = (Attr) attributes.item(i);

	    if (attr.getNamespaceURI() != jsonNamespaceUri)
		return false;
	}

	return true;
    }

    private boolean nodeIsEmpty(final Node node) {
	return (!node.hasChildNodes()) && (!node.hasAttributes());
    }

    private void serializeGroupedNodes(final JsonWriter writer,
	    final Node node, final XmlNamespaceManager manager,
	    final boolean writePropertyName) throws IOException {

	final Map<String, List<Node>> nodesGroupedByName = getNodesGroupedByName(
		node, manager);

	/*
	 * Loop through grouped nodes. write single name instances as normal,
	 * write multiple names together in an array
	 */
	for (Map.Entry<String, List<Node>> nameNodesMapping : nodesGroupedByName
		.entrySet()) {

	    final String name = nameNodesMapping.getKey();
	    final List<Node> nodesHavingName = nameNodesMapping.getValue();

	    /* By default we don't write nodes as arrays */
	    boolean writeAsArray = false;

	    /*
	     * All groups of nodes are written as arrays, while single nodes are
	     * written as an array only if they have the Array attribute set
	     */
	    if (nodesHavingName.size() == 1)
		writeAsArray = isArray(nodesHavingName.get(0));
	    else
		writeAsArray = true;

	    if (!writeAsArray)
		serializeNode(writer, nodesHavingName.get(0), manager,
			writePropertyName);
	    else
		serializeArray(writer, name, nodesHavingName, manager,
			writePropertyName);

	}
    }

    private boolean equalsWithNull(Object lhs, Object rhs) {
	if (lhs == null && rhs == null)
	    return true;
	else if (lhs == null || rhs == null)
	    return false;
	else
	    return lhs.equals(rhs);
    }

    private void writeCommaIfNeedsComma(JsonWriter writer) throws IOException {
	if (needsComma) {
	    writer.writeComma();
	    needsComma = false;
	}
    }

    private void writePropertyNameAndColon(JsonWriter writer,
	    String propertyName) throws IOException {
	writeStringWithStateInfo(writer, propertyName);
	writer.writeColon();
    }

    private void writePropertyValue(JsonWriter writer, String propertyValue)
	    throws IOException {	
	if(writingArray)
	    writeCommaIfNeedsComma(writer);
	writer.writeString(propertyValue);
	needsComma = true;
    }

    private void writeStringWithStateInfo(JsonWriter writer, String text)
	    throws IOException {
	writeCommaIfNeedsComma(writer);
	writer.writeString(text);
    }

    private void writeOpenObjectWithStateInfo(JsonWriter writer) throws IOException {
	writingObject=true;
	writeCommaIfNeedsComma(writer);
	writer.writeOpenObject();
    }

    private void writeOpenArrayWithStateInfo(JsonWriter writer) throws IOException {
	writingArray=true;
	writeCommaIfNeedsComma(writer);
	writer.writeOpenArray();
    }

    private void writeCloseObjectWithStateInfo(JsonWriter writer)
	    throws IOException {
	writingObject=false;
	needsComma = true;	
	writer.writeCloseObject();
    }

    private void writeCloseArrayWithStateInfo(JsonWriter writer) throws IOException {
	writingArray=false;
	needsComma = true;	
	writer.writeCloseArray();
    }
}
