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
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.BasicConfigurator;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.SystemProperties;

/**
 * Subscriber application that listens for messages once subscribed for
 * certain events.
 * @author pjg
 */
public class NSGISubscriber extends ServerResource {

    /**
     * The URL of the broker.
     */
    private static final String BROKERIP = "orion.lab.fi-ware.eu:1026";

    /**
     * The operation called when the client performs a GET operation.
     * @return A simple text notification response.
     */
    @Get
	public final String present() {
            ServiceLogger.LOG.info("EVENT RECEIVED!!!!");
		return "hello, world";
	}

    /**
     * The operation called when the client performs a POST operation.
     * @return A simple text notification response.
     */
    @Post
    public final String accumulate() {
        ServiceLogger.LOG.info("EVENT RECEIVED!!!!");
            return "event received";
    }

    /**
     * Run the simple subscriber operation that performs a subscribe operation.
     * @param args The command line
     */
    public static void main(final String[] args) {

        try {
            BasicConfigurator.configure();
            new Server(Protocol.HTTP, 1028, NSGISubscriber.class).start();

            final String myIP = SystemProperties.getIP() + ":1028";

            final String apiString = "http://" + BROKERIP + "/NGSI10/subscribeContext";
            final String callbackURL = "http://" + myIP;

            final String xml = "<?xml version=\"1.0\"?>\n"
                +    "<subscribeContextRequest>\n"
                +    "  <entityIdList>\n"
                +    "    <entityId type=\"HospitalFilter\" isPattern=\"false\">\n"
                +    "      <id>HospitalFilterA34</id>\n"
                +    "    </entityId>\n"
                +    "  </entityIdList>\n"
                +    "  <attributeList>\n"
                +    "    <attribute>temperature</attribute>\n"
                +    "  </attributeList>\n"
                +    "  <reference>" + callbackURL + "</reference>\n"
                +    "  <duration>PT3M</duration>\n"
                +    "  <notifyConditions>\n"
                +    "   <notifyCondition>\n"
                +    "      <type>ONCHANGE</type>\n"
                +    "      <condValueList>\n"
                +    "        <condValue>pressure</condValue>\n"
                +    "      </condValueList>\n"
                +    "    </notifyCondition>    "
                +    "  </notifyConditions>\n"
                +    "</subscribeContextRequest>";

                final Client client = Client.create();
                ServiceLogger.LOG.info(xml);
                final WebResource webResource = client.resource(apiString);
                final ClientResponse response = webResource.type("application/xml").header("X-Auth-Token",
                        OAUTH2.getToken()).post(ClientResponse.class, xml);

                if (response.getStatus() != 200) {
                    ServiceLogger.LOG.error("Failed : HTTP error code : " + response.getStatus() + "Error Msg: " + response.getEntity(String.class));
                }

                ServiceLogger.LOG.info("Output from Server .... \n");
                final String output = response.getEntity(String.class);
                ServiceLogger.LOG.info(output);
        } catch (Exception ex) {
            ServiceLogger.LOG.error(ex);
        }
    }
}
