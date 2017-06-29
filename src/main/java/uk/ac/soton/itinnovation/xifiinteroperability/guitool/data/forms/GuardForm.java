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

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.Function;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.Guard;
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
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;

/**
 * The form to input data attached to a guard transition. It appears in the
 * left hand panel when a guard transition is clicked in the graph.
 *
 * @author pjg
 */

public class GuardForm extends JPanel {

    /**
     * The user interface model i.e. this data is what this form is
     * viewing upon - list of guards on a particular transition.
     */
    private final transient GuardTransitionAttributeTable guardView;

    /**
     * Viewable data fields matched with the GUI element's data.
     */

    /**
     * The text area to input the identity data.
     */
    private final transient JTextField ident;

    /**
     * The text area to enter the machine address.
     */
    private final transient JTextField address;

    /**
     * The text area to enter a identifier for a url.
     */
    private final transient JTextField urlID = new JTextField();

    /**
     * The text area to input a URL.
     */
    private final transient JTextField url = new JTextField();

    /**
     * The form has a one-to-many relationship with an architecture node. The
     * form is a changing view of the selected node. This field stores the
     * current selected node (via setData() method)
     */
    private transient Guard mirrorNode;

    /**
     * Create a form with the specified labels, tooltips, and sizes.
     * @param editor a basic graph editor
     */
    public GuardForm(final BasicGraphEditor editor) {
        super(new BorderLayout());
        guardView = new GuardTransitionAttributeTable();
        final JTable guardTable = new JTable(guardView);
        guardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = guardTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < guardTable.getRowCount()) {
                    guardTable.setRowSelectionInterval(r, r);
                } else {
                    guardTable.clearSelection();
                }

                int rowindex = guardTable.getSelectedRow();
                if (rowindex < 0)
                    return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new ChangeTable(editor, guardView, r, mirrorNode);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

         // The guard table needs a dropbox input field
        guardTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(guardView.getGuardCombo()));

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Info Panel
        final JPanel listPane = new JPanel();
        final GridLayout gridLayout = new GridLayout(6 , 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        listPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        listPane.setLayout(gridLayout);

        final JLabel title = new JLabel("Add new Guard", JLabel.CENTER);
        final Font font = title.getFont();
        final Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        title.setFont(font.deriveFont(attributes));

        listPane.add(title);
        listPane.add(new JLabel("", SwingConstants.LEFT));

        listPane.add(new JLabel("Guard Function:",  JLabel.RIGHT));
        final JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(Function.FunctionType.values()));
        final JPanel comboPanel = new JPanel();
        comboPanel.add(comboBox, BorderLayout.CENTER);
        listPane.add(comboPanel);

        listPane.add(new JLabel("Guard description:",  JLabel.RIGHT));
        ident = new JTextField();
        listPane.add(ident);

        listPane.add(new JLabel("Required guard value:", JLabel.RIGHT));
        address = new JTextField();
        listPane.add(address);
        listPane.add(new JLabel(""));

        final JButton update = new JButton("Add guard");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                mirrorNode.addGuard((Function.FunctionType) comboBox.getSelectedItem(), ident.getText(), address.getText());
                guardView.clearData();
                guardView.setData(mirrorNode);
                ident.setText("");
                address.setText("");
            }
          });

        listPane.add(update);
        topPanel.add(listPane);

        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));


        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        final JLabel tableTitle = new JLabel("Table of Guards", JLabel.RIGHT);
        tableTitle.setFont(font.deriveFont(attributes));
        topPanel.add(tableTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));


        AttributePanel.setTableConsistentLookAndFeel(guardTable);
        final JScrollPane guardScrollPane = JTable.createScrollPaneForTable(guardTable);

        add(topPanel, BorderLayout.NORTH);
        add(guardScrollPane, BorderLayout.CENTER);

    }

    /**
     * Update the data of this form. For a new graph selected element.
     * @param guardData The new graph element to view.
     */
    public final void setData(final Guard guardData) {
        mirrorNode = guardData;
        guardView.setData(guardData);
    }

    /**
     * Clear the content of the form, i.e. replace all the text fields with
     * empty data.
     */
    public final void clearData() {
        guardView.clearData();
        ident.setText("");
        address.setText("");
        urlID.setText("");
        url.setText("");
    }


}

