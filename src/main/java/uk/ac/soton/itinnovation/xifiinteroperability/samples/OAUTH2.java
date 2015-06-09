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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.BasicConfigurator;

import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.JSON;

/**
 * This is a simple OAUTH application that requests a token via
 * the client credentials. This token is then used to find
 *
 * @author pjg
 */
public final class OAUTH2 {

    /**
     * FI-LAB IDM token endpoint.
     */

    private static final String URLTOKEN = "https://account.lab.fiware.org/oauth2/token";

    /**
    * Registered details of the application. This is a registered Client ID.
    */
    private static final String CLIENTID = "83";

    /**
    * Registered details of the application. This is a registered Client Secret.
    */
    private static final String CLIENTSECRET = "2558be3a96755fbf5854a9c9630e5155fa1af0ecc8a4bc06c7685c6134cf298b7d26ee310a4ec741ced512d7282524058831ac0a50236647c6c54550f48f3787";

    /**
     * Only main method to be used in this class; hence add private
     * constructor.
     */
    private OAUTH2() {
    }

    /**
     * Use basic credentials to get an access token from the server.
     * @return The IdM token.
     */
    public static String getToken() {
        try {
           final Client client = Client.create();

           final WebResource webResource = client.resource(URLTOKEN);
           final MultivaluedMap queryParams = new MultivaluedMapImpl();
           queryParams.add("grant_type", "client_credentials");
           queryParams.add("client_id", CLIENTID);
           queryParams.add("client_secret", CLIENTSECRET);
           final ClientResponse response = webResource.queryParams(queryParams).post(ClientResponse.class);

           if (response.getStatus() != 200) {
              ServiceLogger.LOG.error("Failed : HTTP error code : " + response.getStatus() + " Error Message: " + response.getEntity(String.class));
           }

           final String tokenData = response.getEntity(String.class);
           ServiceLogger.LOG.info("Token response from the Server .... \n");
           ServiceLogger.LOG.info(tokenData);

           return JSON.readValue(tokenData, "$.access_token");
        } catch (UniformInterfaceException ex) {
            ServiceLogger.LOG.error("Invalid URL error " + ex.getMessage());
            return null;
        } catch (ClientHandlerException e) {
           ServiceLogger.LOG.error("Remote operation Failed. Reason is " + e.getMessage());
           return null;
        }
    }

    /**
     * Run an OAUTH exchange message using client credentials style.
     * @param args Command line arguments (not used).
     */
    public static void main(final String[] args) {

       try {
           BasicConfigurator.configure();
           getToken();
        } catch (UniformInterfaceException ex) {
            ServiceLogger.LOG.error("Invalid URL error " + ex.getMessage());
        } catch (ClientHandlerException e) {
           ServiceLogger.LOG.error("Remote operation Failed. Reason is " + e.getMessage());
        }
    }
}

