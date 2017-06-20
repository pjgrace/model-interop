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

package uk.ac.soton.itinnovation.xifiinteroperability;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.log4j.BasicConfigurator;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.InvalidStateMachineException;
import uk.ac.soton.itinnovation.xifiinteroperability.utilities.FileUtils;

/**
 * Operate the tool as a stand alone simple offline client.
 * The developer simply runs interoperability tests from the command line.
 * The same behaviour is achieved; i.e. an interoperability report for
 * a monitored application is produced.
 *
 * N.b. as this is a client side tool errors are reported to the command line
 * here; however, the log file can be used to determine deeper exceptions.
 *
 * @author pjg
 */
public final class InteroperabilityTool {

    /**
     * Only main method to be used in this class; hence add private
     * constructor.
     */
    private InteroperabilityTool() {
    }

    /**
     * Run the interoperability tool from the command line.
     * @param args the command line arguments.
     * @throws InvalidStateMachineException Invalid pattern input to tool error.
     */
    public static void main(final String[] args) throws InvalidStateMachineException {
        try {
            BasicConfigurator.configure();
            /*
             * Client command mut contain one parameter: the pattern's filename
             */
            if (args.length != 1) {
                ServiceLogger.LOG.error("Invalid parameters - correct usage: InteroperabilityTool <pattern_filename>");
                throw new InvalidStateMachineException("Invalid input parameters");
            }

            /*
             * Read the contents of the pattern file and construct the cradle
             */
            final String statemachine = FileUtils.readFile(args[0], Charset.defaultCharset());
            if (statemachine == null) {
                ServiceLogger.LOG.error("Invalid input: File does not contain a valid pattern");
                throw new InvalidStateMachineException("Invalid input parameters");
            }

            final Architecture architect = new Architecture(statemachine, null);
            /*
            * Start the cradle monitoring
            */
            ServiceLogger.LOG.info(architect.executePattern());

            /*
            * Once the application has been tested and report produced we
            * clean up and exit.
            */
            architect.cleanup();

        } catch (IOException ex) {
           ServiceLogger.LOG.error("Unable to execute tool: invalid statemachine input, check " + args[0]);
        }

    }

}
