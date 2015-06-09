/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2015
//
// Copyright in this library belongs to the University of Southampton
// University Road, Highfield, Southampton, UK, SO17 1BJ
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
// Created By : Paul Grace
// Created for Project : XIFI (http://www.fi-xifi.eu)
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;

/**
 * Operations for evaluating XML data elements. These are typically applied
 * to assess <Guard> statements of patterns for HTTP responses with XML
 * message bodies.
 *
 * @author pjg
 */
public final class XML {

    /**
     * Utility class, hence use a private constructor.
     */
    private XML() {
        // no implementation required.
    }
    /**
     * XPATH based method to assert that particular expressions in an
     * XML data structure e.g. /Resp/Address/Street == Main St. Given an XML
     * doc and an expression, does this match the given value?
     *
     * @param xmlDoc The xml content to apply an XPATH expression to
     * @param reference The XPATH reference expression to evaluate
     * @param value The value to compare against
     * @return true or false as a result of the test
     */
    public static boolean xmlAssert(final String xmlDoc, final String reference, final Object value) {
        try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
            domFactory.setNamespaceAware(true);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final InputSource source = new InputSource(new StringReader(xmlDoc.toLowerCase(Locale.ENGLISH)));
            final Document doc = builder.parse(source);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final XPathExpression expr = xpath.compile(reference.toLowerCase(Locale.ENGLISH));
            final Object result = expr.evaluate(doc);
            return result.equals(value.toString().toLowerCase());
        } catch (SAXException ex) {
            ServiceLogger.LOG.error("Error parsing the xml document", ex);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Error buffering the xml string data", ex);
        } catch (ParserConfigurationException ex) {
            ServiceLogger.LOG.error("Error configuring the xml parser", ex);
        } catch (XPathExpressionException ex) {
            ServiceLogger.LOG.error("Error with invalid xml xpath expression", ex);
        }
        return false;
    }

    /**
     * Validate and xml document against the xml schema; throw exceptions
     * when the schema doesn't match.
     * @param xml The xml document to check.
     * @param schema The schema to test against.
     * @throws SAXException Error stating that it doesn't match.
     * @throws IOException Error during the processing of the operation.
     */
    private static void localValidate(final String xml, final Schema schema)
            throws SAXException, IOException {
        final StreamSource source = new StreamSource(new StringReader(xml));
        final Validator validator = schema.newValidator();
        validator.validate(source);
    }

    /**
     * Public operation to check if an xml document (as a string) matches
     * a schema given at a URL.
     * @param xmlDoc The xml document to test.
     * @param schemaFile The URL location of the schema.
     * @return true if the doc conforms to the schema, false otherwise.
     */
    public static boolean xmlValidate(final String xmlDoc, final URL schemaFile) {
        final SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            final Schema schema = schemaFactory.newSchema(schemaFile);
            localValidate(xmlDoc, schema);
            return true;
        } catch (SAXException e) {
            ServiceLogger.LOG.error("Invalid Schema", e);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Cannot read schema", ex);
        }
        return false;
    }

    /**
     * Public operation to check if an xml document (as a string) matches
     * a schema given as a string.
     * @param xmlDoc The xml document to test.
     * @param schemaIn The complete schema in a string.
     * @return true if the doc conforms to the schema, false otherwise.
     */
    public static boolean xmlValidate(final String xmlDoc, final String schemaIn) {
        final StreamSource schemaFile = new StreamSource(new StringReader(schemaIn));
        final SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            final Schema schema = schemaFactory.newSchema(schemaFile);
            localValidate(xmlDoc, schema);
            return true;
        } catch (SAXException e) {
           ServiceLogger.LOG.error("Invalid Schema", e);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Unable to read schema", ex);
        }
        return false;
    }

    /**
     * Given an xpath expression: read that value from an xml string.
     * @param xmlDoc The xml string to read from.
     * @param pathexpr The xpath expression.
     * @return The xml value read.
     */
    public static String readValue(final String xmlDoc, final String pathexpr) {
        try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                    .newInstance();
            domFactory.setNamespaceAware(true);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final InputSource source = new InputSource(new StringReader(xmlDoc.toLowerCase(Locale.ENGLISH)));
            final Document doc = builder.parse(source);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final XPathExpression expr = xpath.compile(pathexpr.toLowerCase(Locale.ENGLISH));
            return expr.evaluate(doc);
        } catch (SAXException ex) {
            ServiceLogger.LOG.error("Error parsing the xml string", ex);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Error reading the xml into buffer", ex);
        } catch (ParserConfigurationException ex) {
            ServiceLogger.LOG.error("Error configuring the parser", ex);
        } catch (XPathExpressionException ex) {
            ServiceLogger.LOG.error("Invalid XML XPATH check", ex);
        }
        return null;
    }

    /**
     * Write a value in the xml string at a given xpath expression.
     * @param xmlDoc The document to write in to.
     * @param pathexpr The xpath expression.
     * @param val The value to write.
     * @return The edited xml document.
     */
    public static String writeValue(final String xmlDoc, final String pathexpr, final String val) {
         try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                    .newInstance();
            domFactory.setNamespaceAware(true);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final InputSource source = new InputSource(new StringReader(xmlDoc.toLowerCase(Locale.ENGLISH)));
            final Document doc = builder.parse(source);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final XPathExpression expr = xpath.compile(pathexpr);
            final Node param =  (Node) expr.evaluate(doc, XPathConstants.NODESET);
            param.setNodeValue(val);

            final TransformerFactory tFact = TransformerFactory.newInstance();
            final Transformer transformer = tFact.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            final StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (SAXException ex) {
            ServiceLogger.LOG.error("Error parsing xml string", ex);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Error buffering xml string", ex);
        } catch (ParserConfigurationException ex) {
            ServiceLogger.LOG.error("Error configuring xml parser", ex);
        } catch (XPathExpressionException ex) {
            ServiceLogger.LOG.error("Invalid XML XPATH check", ex);
        } catch (TransformerException ex) {
            ServiceLogger.LOG.error("Invalid option to write information", ex);
        }
        return null;
    }
}
