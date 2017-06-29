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
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.AttributePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;

/**
 * Table related to the data attached to graph nodes. Essentially, there
 * is little need for data attached to nodes at present (although in future
 * this may be extended e.g. time outs in timed automata.
 *
 * Attach name value pairs for constant values used in the graph.
 *
 * todo: add rules for one ID, one address only
 *
 * @author pjg
 */

public class NodeForm extends JPanel {

    /**
     * The UI view onto this node form.
     */
    private final transient GraphNodeAttributeTable nodeView;

    /**
     * Viewable data fields matched with the GUI element's data.
     */

    /**
     * The text field capturing the node identity field.
     */
    private final transient JTextField ident;

    /**
     * The text field capturing the node address field.
     */
    private final transient JTextField address;

    /**
     * The text field capturing the url ID field data.
     */
    private final transient JTextField urlID = new JTextField();

    /**
     * The text field capturing the url field data.
     */
    private final transient JTextField url = new JTextField();

    /**
     * The form has a one-to-many relationship with an architecture node. The
     * form is a changing view of the selected node. This field stores the
     * current selected node (via setData() method)
     */
    private transient GraphNode mirrorNode;

    /**
     * Create a form with the specified labels, tooltips, and sizes.
     * @param editor
     */
    public NodeForm(BasicGraphEditor editor) {
        /**
         * Create the form properties with a border layout.
         */
        super(new BorderLayout());
        nodeView = new GraphNodeAttributeTable();

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Info Panel
        final JPanel listPane = new JPanel();
        final GridLayout gridLayout = new GridLayout(6 , 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        listPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        listPane.setLayout(gridLayout);

        final JLabel title = new JLabel("Add new node attribute", JLabel.CENTER);
        final Font font = title.getFont();
        final Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        title.setFont(font.deriveFont(attributes));

        listPane.add(title);
        listPane.add(new JLabel("", SwingConstants.LEFT));

        listPane.add(new JLabel("node parameter:",  JLabel.RIGHT));
        ident = new JTextField();
        listPane.add(ident);

        listPane.add(new JLabel("parameter value:", JLabel.RIGHT));
        address = new JTextField();
        listPane.add(address);
        listPane.add(new JLabel(""));

        final JButton update = new JButton("Add parameter");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                mirrorNode.addConstantData(ident.getText(), address.getText());
                nodeView.clearData();
                nodeView.setData(mirrorNode);
                ident.setText("");
                address.setText("");
            }
          });

        listPane.add(update);
        topPanel.add(listPane);

        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));


        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        final JLabel tableTitle = new JLabel("Table of nodes", JLabel.RIGHT);
        tableTitle.setFont(font.deriveFont(attributes));
        topPanel.add(tableTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        final JTable nodeTable = new JTable(nodeView);
        nodeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = nodeTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < nodeTable.getRowCount()) {
                    nodeTable.setRowSelectionInterval(r, r);
                } else {
                    nodeTable.clearSelection();
                }
                
                int rowindex = nodeTable.getSelectedRow();
                if (rowindex < 0)
                    return;
                
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new ChangeTable(editor, nodeView, r, mirrorNode);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        AttributePanel.setTableConsistentLookAndFeel(nodeTable);
        final JScrollPane nodeScrollPane = JTable.createScrollPaneForTable(nodeTable);

        add(topPanel, BorderLayout.NORTH);
        add(nodeScrollPane, BorderLayout.CENTER);

    }

    /**
     * Set the data of the form. That is, fill in the fields with the
     * data stored in the object parameter.
     * @param grphNode The data to fill the form in with. This is a node data
     * element from the pattern.
     */
    public final void setData(final GraphNode grphNode) {
        mirrorNode = grphNode;
        nodeView.setData(grphNode);
    }

    /**
     * Reset the content of the node form. Clear all the text fields.
     */
    public final void clearData() {
        nodeView.clearData();
        ident.setText("");
        address.setText("");
        urlID.setText("");
        url.setText("");
    }


}

