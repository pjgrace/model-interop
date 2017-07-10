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

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.XMLEditorKit;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.DataModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * The XMLSpecificationPanel is the portion of the UI where the XML representation
 * of the graph views is displayed. It is a simple scrolling text panel displayed
 * in the main area to the right side of the tool.
 *
 * @author pjg
 */

public class XMLSpecificationPanel extends JPanel {

    /**
     * Reference to the tool's underlying data model, so we can generate and
     * display the specification.
     */
    private final transient DataModel dataModel;

    /**
     * The core element of the panel - the text area to display the xml.
     */
    private final transient JEditorPane xmlSpecification;

    /**
     * Create the UI panel element with the given data model.
     * @param dModel The tool data model to build specification from.
     */
    public XMLSpecificationPanel(final DataModel dModel) {
        super(new BorderLayout());
        this.dataModel = dModel;

        // Create the initial string content of the panel
        final StringBuilder content = new StringBuilder();
        if (dModel != null) {
            content.append(dModel.getGraphXML());
        }

        xmlSpecification = new JEditorPane("text/xml", content.toString());
        xmlSpecification.setEditable(false);
        xmlSpecification.setBorder(new CompoundBorder(new LineBorder(Color.GRAY),
                new EmptyBorder(1, 3, 1, 1)));
        xmlSpecification.setEditorKit(new XMLEditorKit(this));
        
//        xmlSpecification.setLineWrap(true);
//        xmlSpecification.setWrapStyleWord(true);

        // Create the scrolling text area with the content.
        final JScrollPane areaScrollPane = new JScrollPane(xmlSpecification);
        areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(1000, 1000));

        add(areaScrollPane, BorderLayout.CENTER);
    }

    /**
     * Update the text area to display the current xml specification for the
     * drawn graph.
     */
    public final void displayXMLSpecification() {
        if (dataModel != null) {
            this.xmlSpecification.setDocument(this.xmlSpecification.getEditorKit().createDefaultDocument());
            this.xmlSpecification.setText(dataModel.getGraphXML().replaceAll("\t", ""));
        }
    }
}

