/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2017
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
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability.tracetools;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.log4j.BasicConfigurator;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InvalidPatternException;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.TraceGenerator;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.InvalidStateMachineException;
import uk.ac.soton.itinnovation.xifiinteroperability.utilities.FileUtils;

/**
 * The Trace execute tool is a command line tool that runs the interoperability
 * test directly with two inputs:
 * 1) the pattern of the architecture and state machine
 * 2) the trace of events to replay.
 *
 * usage: TraceExecute filename<pattern> filename<trace>
 *
 * Project acknowledgements - developed in FIESTA (http://www.fiesta-iot.eu)
 * & XIFI (http://www.fi-xifi.eu)
 *
 * @author Paul Grace
 */
public final class TraceExecuteTool {

    /**
     * Only main method to be used in this class; hence add private
     * constructor.
     */
    private TraceExecuteTool() {
    }

    /**
     * @param args the command line arguments
     * @throws InvalidStateMachineException Error during state machine traversal.
     */
    public static void main(final String[] args) throws InvalidStateMachineException {
        try {
            BasicConfigurator.configure();
            /*
             * Client command mut contain one parameter: the pattern's filename
             */
            if (args.length != 2) {
                ServiceLogger.LOG.error("Invalid parameters - correct usage: TraceExecuteTool <pattern_filename> ");
                throw new InvalidStateMachineException("Invalid state machine input values");
            }

            // Create an architecture to test from the input pattern
            final String stateMachineSpec = FileUtils.readFile(args[0], Charset.defaultCharset());
            final Architecture stateMachine = new Architecture(stateMachineSpec, null, false);

            //Generate events from the trace file and send them to the state machine
            TraceGenerator.generateEvents(args[1], stateMachine.getStateMachine());

            // Output the result of the test to the command line
            ServiceLogger.LOG.info("Interoperability Report");
            ServiceLogger.LOG.info("-----------------------");
            ServiceLogger.LOG.info(stateMachine.executePattern());

            // cleanup the resources and exit
            stateMachine.cleanup();

        } catch (IOException ex) {
           ServiceLogger.LOG.error("Unable to execute tool: invalid statemachine input, check " + args[0]);
        } catch (InvalidPatternException ex) {
            ServiceLogger.LOG.error("Unable to execute tool: invalid statemachine input, check " + args[0]);
        }

    }

}
