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
package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms;

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.GraphNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Table related to the data attached to end states in the graph nodes.
 *
 * The form supports the entry of success field and reason information.
 *
 * @author pjg
 */

public class EndForm extends JPanel {

    /**
     * Viewable data fields matched with the GUI element's data.
     */

    private final transient JComboBox successBox = new JComboBox();
    /**
     * The text field capturing the node identity field.
     */
    private final transient JTextField reasonInput = new JTextField();


    /**
     * The form has a one-to-many relationship with an architecture node. The
     * form is a changing view of the selected node. This field stores the
     * current selected node (via setData() method)
     */
    private transient GraphNode mirrorEndNode;

    /**
     * Create a form with the specified labels, tooltips, and sizes.
     */
    public EndForm() {
        /**
         * Create the form properties with a border layout.
         */
        super(new BorderLayout());

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Info Panel
        final JPanel listPane = new JPanel();
        final GridLayout gridLayout = new GridLayout(3 , 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        listPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        listPane.setLayout(gridLayout);


        listPane.add(new JLabel("Success:",  JLabel.RIGHT));
        successBox.addItem(true);
        successBox.addItem(false);

        listPane.add(successBox);

        listPane.add(new JLabel("Test report:", JLabel.RIGHT));
        listPane.add(reasonInput);

        listPane.add(new JLabel("",  JLabel.RIGHT));
        final JButton update = new JButton("Update end state");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                mirrorEndNode.addEndStateData((Boolean) successBox.getSelectedItem(), reasonInput.getText());
            }
          });

        listPane.add(update);
        topPanel.add(listPane);
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Set the data of the form. That is, fill in the fields with the
     * data stored in the object parameter.
     * @param grphNode The data to fill the form in with. This is a node data
     * element from the pattern.
     */
    public final void setData(final GraphNode grphNode) {
        mirrorEndNode = grphNode;
        this.successBox.setSelectedItem(grphNode.getEndStateSuccess());
        this.reasonInput.setText(grphNode.getEndStateReport());
    }

    /**
     * Reset the content of the node form. Clear all the text fields.
     */
    public final void clearData() {
        this.successBox.setSelectedItem(null);
        this.reasonInput.setText("");
    }


}

