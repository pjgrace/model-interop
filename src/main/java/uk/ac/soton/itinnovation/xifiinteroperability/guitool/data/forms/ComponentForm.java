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

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ArchitectureNode;
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
import javax.swing.JOptionPane;
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

public class ComponentForm extends JPanel {

    /**
     * The panel displaying the entered URLs of this component.
     */
    private final transient ComponentTableModel componentView;

    /**
     * Viewable data fields matched with the GUI element's data.
     */

    /**
     * Text field to enter the identifier of the component (system node).
     */
    private final transient JTextField ident;

    /**
     * Text field to enter the ip address of the component (system node).
     */
    private final transient JTextField address;

    /**
     * Text field to enter id of URL.
     */
    private final transient JTextField urlID = new JTextField();

    /**
     * Text field to enter the full URL.
     */
    private final transient JTextField url = new JTextField();

    /**
     * The form has a one-to-many relationship with an architecture node. The
     * form is a changing view of the selected node. This field stores the
     * current selected node (via setData() method).
     */
    private transient ArchitectureNode mirrorNode;

    /**
     * Create a form with the specified labels, tooltips, and sizes.
     * @param editor
     */
    public ComponentForm(BasicGraphEditor editor) {
        super(new BorderLayout());
        componentView = new ComponentTableModel();

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Info Panel
        final JPanel listPane = new JPanel();
        final GridLayout gridLayout = new GridLayout(5 , 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        listPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        listPane.setLayout(gridLayout);

        final JLabel title = new JLabel("Component Information", JLabel.CENTER);
        final Font font = title.getFont();
        final Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        title.setFont(font.deriveFont(attributes));

        listPane.add(title);
        listPane.add(new JLabel("", SwingConstants.LEFT));

        listPane.add(new JLabel("Component Identifier:",  JLabel.RIGHT));
        ident = new JTextField();
        listPane.add(ident);

        listPane.add(new JLabel("Component Address:", JLabel.RIGHT));
        address = new JTextField();
        listPane.add(address);
        listPane.add(new JLabel(""));

        final JButton update = new JButton("Update");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                
                if (mirrorNode.getLabel().equalsIgnoreCase(ident.getText()) || !editor.getDataModel().archIdentExist(ident.getText())){
                    mxGraphModel model = (mxGraphModel) editor.getSystemGraph().getGraph().getModel();
                    mxCell cellChanged = (mxCell) model.getCell(mirrorNode.getNodeLabelID());
                    cellChanged.setValue(ident.getText());
                    editor.getSystemGraph().refresh();
                    mirrorNode.setData(ident.getText(), address.getText());
                }
                else {
                    JOptionPane.showMessageDialog(editor, 
                            "Component id '" + ident.getText() + "' is already used. Please choose another label."
                            , "Renaming error", JOptionPane.ERROR_MESSAGE);
                    ident.setText(mirrorNode.getLabel());
                }
            }
          });

        listPane.add(update);
        topPanel.add(listPane);

        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Interface add Panel
        final JPanel newIntfPane = new JPanel();
        newIntfPane.setLayout(gridLayout);
        newIntfPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        final JLabel stitle = new JLabel(" Add new Interface to component");
        stitle.setFont(font.deriveFont(attributes));
        newIntfPane.add(stitle);
        newIntfPane.add(new JLabel("", JLabel.LEFT));

        newIntfPane.add(new JLabel(" Interface ID", JLabel.LEFT));
        newIntfPane.add(new JLabel("Interface URL", JLabel.LEFT));


        newIntfPane.add(urlID);
        newIntfPane.add(url);
        final JButton addIntf = new JButton("Add Interface Info");
        addIntf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
              mirrorNode.addInterfaceData(urlID.getText(), url.getText());
              componentView.clearData();
              componentView.setData(mirrorNode);
              url.setText("");
              urlID.setText("");
            }
          });
        newIntfPane.add(new JLabel(""));
        newIntfPane.add(addIntf);

        topPanel.add(newIntfPane);

        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        final JLabel tableTitle = new JLabel("Table of URL Interfaces", JLabel.RIGHT);
        tableTitle.setFont(font.deriveFont(attributes));
        topPanel.add(tableTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        final JTable nodetable = new JTable(componentView);
        nodetable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = nodetable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < nodetable.getRowCount()) {
                    nodetable.setRowSelectionInterval(r, r);
                } else {
                    nodetable.clearSelection();
                }
                
                int rowindex = nodetable.getSelectedRow();
                if (rowindex < 0)
                    return;
                
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new ChangeTable(editor, componentView, r, mirrorNode);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        final JScrollPane compScrollPane = JTable.createScrollPaneForTable(nodetable);

        add(topPanel, BorderLayout.NORTH);
        add(compScrollPane, BorderLayout.CENTER);

    }

    /**
     * Set the data input to the fields in the component form using the
     * information from the architecture node in the data model.
     * @param archNodeInput The architecture node data.
     */
    public final void setData(final ArchitectureNode archNodeInput) {
        mirrorNode = archNodeInput;
        ident.setText(archNodeInput.getLabel());
        address.setText(archNodeInput.getAddress());
        componentView.setData(archNodeInput);
    }

    /**
     * Reset the fields in the component form.
     */
    public final void clearData() {
        componentView.clearData();
        ident.setText("");
        address.setText("");
        urlID.setText("");
        url.setText("");
    }
}

