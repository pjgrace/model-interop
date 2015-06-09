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
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.InvalidStateMachineException;
import uk.ac.soton.itinnovation.xifiinteroperability.utilities.FileUtils;

/**
 *
 * @author pjg
 */
public class PatternExecutionTest {


    /**
     * Test a given patterns executes correctly. This is a compliance test.
     */
    @Test
    public final void testNGSI() {
        try {
            final String sMachine = FileUtils.readFile("NGSI9.xml", Charset.defaultCharset());
            final String editedsMachine = sMachine.replace("**TOKEN**", SystemProperties.readProperty("TEST_TOKEN"));

            Architecture stateMachine = null;

            try {
                 stateMachine = new Architecture(editedsMachine, null);
            } catch (InvalidStateMachineException e) {
                Assert.fail("Invalid input. File does not contain a valid pattern:" + e.getMessage());
            }

            /*
             * Start the cradle monitoring
             */
            final String interopReport = stateMachine.executePattern(null);
            Assert.assertNotNull(interopReport);

        } catch (IOException ex) {
            Assert.fail("Execution fail due to IO error:" + ex.getMessage());
        } catch (ConfigurationException ex) {
            Assert.fail("Execution fail due to configuration error:" + ex.getMessage());
        }
     }
}
