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
//	Created By :			Paul Grace
//	Created for Project :		XIFI (http://www.fi-xifi.eu)
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InvalidPatternException;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.InvalidStateMachineException;
import uk.ac.soton.itinnovation.xifiinteroperability.utilities.FileUtils;

/**
 * Set of tests for ensuring that the code for managing state behaviour in
 * the state machine remains correct.
 *
 * @author pjg
 */
public class LoopTest {

    /**
     * A standard HTML page URL - IT Innovation homepage (can be changed).
     */
    private static final String TESTURL = "http://www.it-innovation.soton.ac.uk";

    /**
     * REST API URL - Use Flickr API (can be changed).
     */
    private static final String FLICKRURL = "https://api.flickr.com/services/rest";

    /**
     * String of 200 - the HTTP Success code.
     */
    private static final String HTTPOK = "200";

    /**
     * Test that the framework constructs a usable rest message i.e. the
     * get, post, put, delete operations all do as should.
     */
    public static final void main(String[] args) {
        try {
            final String sMachine = FileUtils.readFile("examples/loop2.xml", Charset.defaultCharset());

            Architecture stateMachine = null;

            try {
                 stateMachine = new Architecture(sMachine, null);
            } catch (InvalidStateMachineException e) {
                System.err.println("Invalid input. File does not contain a valid pattern:" + e.getMessage());
                System.exit(-1);
            } catch (InvalidPatternException ex){
                System.err.println("There are more than 2 start nodes in the xml graph.");
                System.exit(-1);
            }

            /*
             * Start the cradle monitoring
             */
            final InteroperabilityReport interopReport = stateMachine.executePattern();
            System.out.println(interopReport.getReport());

        } catch (MalformedURLException ex) {
            ServiceLogger.LOG.error("Check the url field -- " + ex.getLocalizedMessage());
        } catch (IOException ex) {
            ServiceLogger.LOG.error(LoopTest.class.getName(), ex);
        }
    }


}
