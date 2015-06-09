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
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;


/**
 * Simple publisher application. Registers and sends new events to the REST
 * NGSI-10 interface. This is presently directed to the orion broker filab
 * deployment, but the constant url can be changed to any instance.
 *
 * Simply execute the application using:
 * java uk.ac.soton.itinnovation.xifiinteroperability.samples.NGSIPublisher tokenvalue
 *
 * @author pjg
 */
public final class NSGIPublisher {

    /**
     * URL of the updateContext method of the NSGI-10 rest interface.
     */
    private static final String APISTRING = "http://orion.lab.fi-ware.eu:1026/NGSI10/updateContext";

    /**
     * Example xml event to publish to the broker.
     */
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<updateContextRequest>\n"
                    + "  <contextElementList>\n"
                    + "    <contextElement>\n"
                    + "      <entityId type=\"HospitalFilter\" isPattern=\"false\">\n"
                    + "        <id>HospitalFilterA34</id>\n"
                    + "      </entityId>\n"
                    + "      <contextAttributeList>\n"
                    + "        <contextAttribute>\n"
                    + "          <name>temperature</name>\n"
                    + "          <type>centigrade</type>\n"
                    + "          <contextValue>30.55</contextValue>\n"
                    + "        </contextAttribute>\n"
                    + "        <contextAttribute>\n"
                    + "          <name>pressure</name>\n"
                    + "          <type>mmHg</type>\n"
                    + "          <contextValue>763</contextValue>\n"
                    + "        </contextAttribute>\n"
                    + "      </contextAttributeList>\n"
                    + "    </contextElement>\n"
                    + "  </contextElementList>\n"
                    + "  <updateAction>UPDATE</updateAction>\n"
                    + "</updateContextRequest>";

    /**
     * Only main method to be used in this class; hence add private
     * constructor.
     */
    private NSGIPublisher() {
    }

    /**
     * Execute the publisher application using a valid token extracted from
     * the FI-lab idm.
     *
     * @param args command line input: one parameter - string token
     */
    public static void main(final String[] args) {

        try {
            // Configure to log message to standard output (e.g. console)
            BasicConfigurator.configure();

            final Client client = Client.create();
            final WebResource webResource = client.resource(APISTRING);
            final ClientResponse response = webResource.type("application/xml").header("X-Auth-Token",
                    OAUTH2.getToken()).post(ClientResponse.class, XML);

            if (response.getStatus() != 200) {
                ServiceLogger.LOG.error("Failed : HTTP error code :" + response.getStatus()
                        + " Error Message:" + response.getEntity(String.class));
            } else {
                ServiceLogger.LOG.info("Output from Server .... \n");
                final String output = response.getEntity(String.class);
                ServiceLogger.LOG.info(output);
            }

        } catch (UniformInterfaceException ex) {
            ServiceLogger.LOG.error("Invalid URL error " + ex.getMessage());
        } catch (ClientHandlerException e) {
           ServiceLogger.LOG.error("Remote operation Failed. Reason is " + e.getMessage());
        }
    }
}
