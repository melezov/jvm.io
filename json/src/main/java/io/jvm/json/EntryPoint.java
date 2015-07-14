public class EntryPoint {
    public static String toString(org.w3c.dom.Element doc)    {
        try
        {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           writer.flush();
           return writer.toString();
        }
        catch(TransformerException ex)
        {
           ex.printStackTrace();
           return null;
        }
    }
    public static org.w3c.dom.Document toXml(String xml)
        throws org.xml.sax.SAXException, java.io.IOException {
        return toXml(new java.io.ByteArrayInputStream(xml.getBytes()));
    }

    public static org.w3c.dom.Document toXml(java.io.InputStream is)
        throws org.xml.sax.SAXException, java.io.IOException {
        javax.xml.parsers.DocumentBuilderFactory factory =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex) {
        }
        org.w3c.dom.Document doc = builder.parse(is);
        is.close();
        return doc;
    }

    public static void main(final String[] args) throws Exception {

        final String input = "<person><name>John</name></person>";

        final Element elem = toXml(input).getDocumentElement();
        System.out.println("INPUT: " + toString(elem));

        final JsonSerializer<Element> xmlSerialization = new XMLJsonSerializer();

        final StringWriter sw = new StringWriter();
        xmlSerialization.toJson(new JsonWriter(sw), elem);
        System.out.println("OUTPUT: " + sw);
    }
}
