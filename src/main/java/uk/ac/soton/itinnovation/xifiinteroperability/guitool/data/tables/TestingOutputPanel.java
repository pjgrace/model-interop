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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.tables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;

/**
 * Panel of the UI for displaying the output of executed tests. The
 * result of the interoperability tests is displayed here.
 *
 * @author pjg
 */

public class TestingOutputPanel extends JPanel {

    /**
     * The test results are displayed via the interoperability report object.
     */
    private transient InteroperabilityReport interopReport;

    /**
     * Getter for access to the interoperability report output mechanism.
     * @return The output mechanism of the testing process.
     */
    public final InteroperabilityReport getInteroperabilityReport() {
        return interopReport;
    }

    /**
     * The text area where the test reports are displayed.
     */
    private final transient JTextArea consoleOutput;


    /**
     * Create the console output of test results in a panel that is
     * displayed in the main area of the UI on the right side.
     */
    public TestingOutputPanel() {
        super(new BorderLayout());

        consoleOutput  = new JTextArea("Report Panel");
        consoleOutput.setBorder(new CompoundBorder(new LineBorder(Color.GRAY),
                new EmptyBorder(1, 3, 1, 1)));
        consoleOutput.setLineWrap(true);
        consoleOutput.setWrapStyleWord(true);
        consoleOutput.setColumns(30);

        try {
            final TestingOutputStream outputReport = new TestingOutputStream(consoleOutput);
            interopReport = new InteroperabilityReport(new PrintStream(outputReport, true, StandardCharsets.UTF_8.name()));

            final JScrollPane areaScrollPane = new JScrollPane(consoleOutput);
            areaScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            areaScrollPane.setPreferredSize(new Dimension(1000, 1000));

            add(areaScrollPane, BorderLayout.CENTER);
        } catch (Exception ex) {
            ServiceLogger.LOG.error("Unable to create text area output on local device: " + ex.getMessage());
        }
    }

    /**
     * Clear all text from the testing panel.
     */
    public final void clearTestingPanel() {
        consoleOutput.setText("");
    }
}

