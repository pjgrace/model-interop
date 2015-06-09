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

package uk.ac.soton.itinnovation.xifiinteroperability.samples;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.BasicConfigurator;

import org.restlet.resource.ServerResource;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;

/**
 * Sample pub-sub application that performs a context query on an
 * NSGI broker. Change to constants to change the service parameters. Note
 * the token will likely be outdated and must be changed.
 *
 * @author pjg
 */
public final class NSGISubscriberOrchestrator extends ServerResource {

    /**
     * The URL of the broker.
     */
    private static final String URLSTRING = "http://orion.lab.fi-ware.eu:1026/NGSI10/queryContext";


    /**
     * The message body of the query request.
     */
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                +     "<queryContextRequest>"
                +     "<entityIdList>"
                +     "<entityId type=\"Room\" isPattern=\"true\">"
                +     "<id>Room.*</id>"
                +     "</entityId>"
                +     "</entityIdList>"
                +     "<attributeList>"
                +     "<attribute>temperature</attribute>"
                +     "</attributeList>"
                +     "</queryContextRequest>";

    /**
     * Only main method to be used in this class; hence add private
     * constructor.
     */
    private NSGISubscriberOrchestrator() {
        super();
    }

    /**
     * The main method to execute the application to run the pub sub functionality.
     * There are no parameters
     * @param args The command line arguments (not used).
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();

        try {
            final Client client = Client.create();
            final WebResource webResource = client.resource(URLSTRING);
            final ClientResponse response = webResource.type("application/xml").header("X-Auth-Token", OAUTH2.getToken()).post(ClientResponse.class, XML);

            if (response.getStatus() != 200) {
                ServiceLogger.LOG.error("Failed : HTTP error code : " + response.getStatus() + " Error Message: " + response.getEntity(String.class));
            }

            ServiceLogger.LOG.info("Output from Server .... \n");
            final String output = response.getEntity(String.class);
            ServiceLogger.LOG.info(output);

        } catch (UniformInterfaceException ex) {
            ServiceLogger.LOG.error("Failed. Reason is " + ex.getMessage());
        } catch (ClientHandlerException e) {
            ServiceLogger.LOG.error("Failed. Reason is " + e.getMessage());
        }
    }
}
