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

package uk.ac.soton.itinnovation.xifiinteroperability.modelframework.specification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.utilities.FileUtils;

/**
 * Simple class with static method to validate a given pattern specified in
 * XML to be compared to the schema documentation.
 *
 * No pattern should be executed that does not validate.
 *
 * @author pjg
 */
public final class PatternValidation {

    /**
     * Utility class, therefore use a private constructor.
     */
    private PatternValidation() {
        // empty implementation.
    }
    /**
     * Method to validate a pattern against the tool's schema and rules.
     * @param xmlcontent The pattern to check.
     * @return True if it is valid, false otherwise.
     * @throws SAXException Where there is an error in the xml content.
     */
    public static boolean validatePattern(final String xmlcontent) throws SAXException {
        return validatePattern(xmlcontent, FileUtils.getURL("Pattern.xsd"));
    }

    /**
     * Validate a given XML specification of a pattern against the schema. The
     * REST service only allows valid specifications to be uploaded and
     * input to the tools.
     *
     * @param xmlcontent The xml to validate as a string input
     * @param schemaUrl The url of schema to compare against.
     * @return true if the doc validates against the schema, false where it
     * is unable to complete the error (i.e. an internal server error).
     *
     * @throws SAXException where there is a mismatch
     */
    public static boolean validatePattern(final String xmlcontent, final URL schemaUrl) throws SAXException {
        try {
            final InputStream inStream = new ByteArrayInputStream(xmlcontent.getBytes(StandardCharsets.UTF_8));

            final DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setValidating(false);
            dfactory.setNamespaceAware(true);

            final DocumentBuilder parser = dfactory.newDocumentBuilder();
            final org.w3c.dom.Document document = parser.parse(inStream);

            final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load a WXS schema, represented by a Schema instance
            final Source schemaFile = new StreamSource(new File(schemaUrl.getFile()));
            final Schema schema = factory.newSchema(schemaFile);

            // create a Validator instance, which can be used to validate an instance document
            final Validator validator = schema.newValidator();

            // validate the DOM tree
            validator.validate(new DOMSource(document));
            return true;
        } catch (ParserConfigurationException ex) {
            ServiceLogger.LOG.error("Unable to parse xml content", ex);
            return false;
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Unable to read files", ex);
            return false;
        }
    }
}
