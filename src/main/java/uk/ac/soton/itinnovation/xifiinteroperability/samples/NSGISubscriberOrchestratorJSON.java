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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import org.restlet.resource.ServerResource;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;

/**
 * Sample testing application that uses the JSON interfaces of the context
 * broker.
 * @author pjg
 */
public final class NSGISubscriberOrchestratorJSON extends ServerResource {

    /**
     * Fixed URL string of the broker NSGI10 interface op.
     */
    private static final String URLSTRING = "http://orion.lab.fi-ware.eu:1026/NGSI10/queryContext";

    /**
     * Only main method to be used in this class; hence add private
     * constructor.
     */
    private NSGISubscriberOrchestratorJSON() {
        super();
    }

    /**
     * Main method to execute the test app.
     * @param args Command line parameters.
     */
    public static void main(final String[] args) {

        final String xml = "{\n"
          +  "    \"entities\": [\n"
          +  "        {\n"
          +  "            \"type\": \"Room\",\n"
          +  "            \"isPattern\": \"false\",\n"
          +  "            \"id\": \"Room1\"\n"
          +  "        }\n"
          +  "    ]\n"
          +  "}";

        try {
           final Client client = Client.create();

      final WebResource webResource = client.resource(URLSTRING);
      final ClientResponse response = webResource.type("application/json").header("accept", "application/json").header("X-Auth-Token", OAUTH2.getToken()).post(ClientResponse.class, xml);

      if (response.getStatus() != 200) {
         ServiceLogger.LOG.error("Failed : HTTP error code : " + response.getStatus() + " Error Message: " + response.getEntity(String.class));
      }

      ServiceLogger.LOG.info("Output from Server .... \n");
      final String output = response.getEntity(String.class);
      ServiceLogger.LOG.info(output);

        } catch (UniformInterfaceException e) {
           ServiceLogger.LOG.error("Failed. Reason is " + e.getMessage());
        }
    }
}
