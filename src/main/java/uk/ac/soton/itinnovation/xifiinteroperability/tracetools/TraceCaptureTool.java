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

package uk.ac.soton.itinnovation.xifiinteroperability.tracetools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.jdom.Document;
import uk.ac.soton.itinnovation.xifiinteroperability.modelcomponent.WrapperDeploymentException;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.RESTComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.RESTInterface;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.InvalidArchitectureException;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.specification.XMLDocument;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.TraceCapture;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.utilities.FileUtils;

/**
 * The trace capture tool is a simple command line tool to capture events
 * relevant to a particular state machine.
 *
 * While the application and the state machine operates - the events are
 * captured and stored in the user defined file. This is a capture trace.
 *
 * The capture trace can then be used to execute the tool instead of running
 * the applications/services repeatedly.
 *
 * This tool can be used for benchmarking purposes and also for rule engine
 * purposes. For example, a reasoner can execute the tool and use the results
 * to build knowledge.
 *
 * @author pjg
 */
public final class TraceCaptureTool {

    /**
     * Private constructor for static utility class to run the tool.
     */
    private TraceCaptureTool() {
        // No implementation.
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            BasicConfigurator.configure();
            /*
             * Client command mut contain one parameter: the pattern's filename
             */

            if (args.length != 1) {
                ServiceLogger.LOG.error("Invalid parameters - correct usage: TraceCaptureTool <pattern_filename>");
            } else {
                // read the full pattern from the user specified file
                final String sMach = FileUtils.readFile(args[0], Charset.defaultCharset());
                final Document pattern = XMLDocument.jDomReadXmlStream(new ByteArrayInputStream(sMach.getBytes(StandardCharsets.UTF_8)));

                //  prompt the user to enter their file name
                ServiceLogger.LOG.info("Enter filename for trace to be stored in: ");

                //  open up standard input
                final BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
                String traceFile = null;

                try {
                    traceFile = buffRead.readLine();
                } catch (IOException ioe) {
                    ServiceLogger.LOG.error("IO error trying to read file name " + ioe.getLocalizedMessage());
                }

                if (traceFile != null) {
                    final TraceCapture capture = new TraceCapture(traceFile);
                    final Map<String, RESTComponent> services = Architecture.createServices(pattern.getRootElement().getChild("architecture"), capture);
                    try {
                        ServiceLogger.LOG.info("Capture data trace Y/N: ");
                        final String readYN = buffRead.readLine();
                        if (readYN == null) {
                            System.exit(-1);
                        }
                        if (!readYN.equalsIgnoreCase("y")) {
                            ServiceLogger.LOG.info("Trace not requested; shutting down");
                            System.exit(1);
                        }
                    } catch (IOException e) {
                        ServiceLogger.LOG.error("IO error trying to read user input");
                        System.exit(1);
                    }
                    // Store the trace and cleanup resources before exiting
                    capture.storeTrace();
                    cleanup(services);
                }
            }

        } catch (IOException ex) {
            ServiceLogger.LOG.error("Unable to execute tool: couldn't read input file ");
        } catch (InvalidArchitectureException ex) {
            ServiceLogger.LOG.error("Unable to execute tool: invalid statemachine input, check " + args[0]);
        }

    }

    /**
     * Clean up the resources used in the trace tool.
     * @param services The set of components in the pattern being traced.
     */
    private static void cleanup(final Map<String, RESTComponent> services) {
        final Collection<RESTComponent> entrySet = services.values();
        for (RESTComponent rc : entrySet) {
            for (RESTInterface ri : rc.getInterfaces()) {
                try {
                    ri.release();
                } catch (WrapperDeploymentException ex) {
                    ServiceLogger.LOG.error("Error releasing REST Intf", ex);
                }
            }
        }
        services.clear();
    }
}
