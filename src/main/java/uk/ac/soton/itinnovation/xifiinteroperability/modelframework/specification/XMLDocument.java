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

import java.io.IOException;
import java.io.InputStream;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;

/**
 * Basic methods to read XML documents from files so that they can be
 * used to create Java objects from the JDOM content read.
 *
 * @author pjg
 */
public final class XMLDocument {

      /**
     * Only static method to be used in this class; hence add private
     * constructor.
     */
    private XMLDocument() {
    }

    /**
     * Create an XML document from a given input stream (e.g. an XML File)
     *
     * @param inStream The File input stream to read
     * @return The JDOM XML document. No document is returned if an exception
     * occurs.
     * @see org.jdom.Document;
     */
    public static Document jDomReadXmlStream(final InputStream inStream) {
	try {
	    return new SAXBuilder().build(inStream);
	} catch (JDOMException e) {
	    ServiceLogger.LOG.error("Unable to read XML: " + e.getMessage());
	} catch (IOException e) {
	    ServiceLogger.LOG.error("Unable to read file: " + e.getMessage());
	}
	return null;
    }

    /**
     * Create an XML document from a string representation of XML.
     * @param inStream The XML string
     * @return The JDOM XML document. No document is returned if an exception
     * occurs.
     * @see org.jdom.Document;
     */
    public static Document jDomReadXml(final String inStream) {
	try {
	    return new SAXBuilder().build(inStream);
	} catch (JDOMException e) {
	    ServiceLogger.LOG.error("Unable to read XML: " + e.getMessage());
	} catch (IOException e) {
	    ServiceLogger.LOG.error("Unable to read string: " + e.getMessage());
	}
	return null;
    }

}
