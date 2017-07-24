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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONPathGenerator.JSONPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XPathGenerator.XPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.tables.XMLSpecificationPanel;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;

/**
 * The form to input data attached to a guard transition. It appears in the
 * left hand panel when a guard transition is clicked in the graph.
 *
 * @author pjg
 */

public class GuardForm extends JPanel {

    /**
     * the html helper text for guard description
     */
    private final static String DESCRIPTION_HELPER = "<html><body>"
            + "<font size=+1><b><i>Guard description</i></b></font><br>"
            + "This can be either an HTTP field or a value from the content <br>"
            + "extracted using XPath or JSONPath depending on the content-type.<br><br>"
            + "1) Common HTTP fields:<br>"
            + "<ul><li>HTTP.from</li></li><li>HTTP.code</li><li>HTTP.msg</li>"
            + "<li>HTTP.date</li><li>HTTP.to</li><li>HTTP.expires</li><li>HTTP.content-type</li>"
            + "<li>HTTP.server</li><li>HTTP.transfer-encoding</li><li>HTTP.accept-ranges</li></ul>"
            + "2) Content extraction:<br>"
            + "<ul><li>Use the following format - <b>content[XPath/JSONPath]</b></li></ul>"
            + "</body></html>";
    
    /**
     * the html helper text for guard value
     */
    private final static String VALUE_HELPER = "<html><body>"
            + "<font size=+1><b><i>Guard value</i></b></font><br>"
            + "This can be any value you want to compare against. "
            + "There are three special cases.<br><br>"
            + "1) If you want to use pattern data:"
            + "<ul><li>Use the following format - <b>$$patterndata.id$$</b> - replace 'id' "
            + "with the id of the pattern data you want to use</li>"
            + "<li>You can also click the right button of the mouse and select "
            + "the <b><i>'Insert pattern data'</i></b> option</li></ul>"
            + "2) If you want to use the address of a certain component:"
            + "<ul><li>Use the following format - "
            + "<b>component.id.address</b> - substite 'id' with the id of the component you want to use</li><br>"
            + "<li>Example : <b>component.fixer.address</b> - here fixer is the id of the component, for which"
            + " we want to use the address</li></ul>"
            + "3) If you want to use data from previous states - you can use content or "
            + "headers data from previous states:"
            + "<ul><li>Use the following format - "
            + "<b>$$state_label | {content or headers} | {XPath or JSONPath or header_id}$$</b>"
            + "</li><br>"
            + "<li>Example 1: <b>$$A2|content|//result/total_price$$</b><br>"
            + "Here, A2 is a label of a state with content of XML type and "
            + "the XPath '//result/total_price' is used to extract a value from the content.<br></li>"
            + "<li>Example 2: <b>$$A4|content|tenants[0].id$$</b><br>"
            + "Here, A4 is a label of a state with content of JSON type and "
            + "the JSONpath 'tenants[0].id' is used to extract a value from the content.<br></li>"
            + "<li> Example 3: <b>$$A1|headers|content-type$$</b><br>"
            + "Here, A1 is a label of a state with headers and the value of the header with "
            + "name 'content-type' is extracted.</li><br>"
            + "<li>You can also click the right button of the mouse and select "
            + "the <b><i>'Insert previous states data'</i></b> option</li></ul>"
            + "</body></html>";
    
    /**
     * the posible header fields for guard description
     */
    private final String[] headerFields = {"From", "Code", "Msg", "Date", "To", "Expires", "Content-Type",
        "Server", "Transfer-Encoding", "Accept-Ranges"};
    
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
        final GridLayout gridLayout = new GridLayout(8 , 2);
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
        ident.setToolTipText("Click right button for selection dialog.");
        ident.addFocusListener(MessageForm.COLOUR_CHANGER);
        ident.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if (SwingUtilities.isRightMouseButton(e)){
                    String[] types = {"Header", "Message content"};
                    String type = (String) JOptionPane.showInputDialog(topPanel,
                            "Please choose the type of data to generate:", "Selection dialog",
                            JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
                    
                    if (type != null && type.equals("Header")){
                        String[] protocols = {"HTTP", "COAP"};
                        String protocol = (String) JOptionPane.showInputDialog(topPanel,
                            "Please choose the type of protocol you want to use:", "Selection dialog",
                            JOptionPane.PLAIN_MESSAGE, null, protocols, protocols[0]);
                        if (protocol != null){
                            String header = (String) JOptionPane.showInputDialog(topPanel,
                                    "Please choose the header field you want to use:", "Selection dialog",
                                    JOptionPane.PLAIN_MESSAGE, null, headerFields, headerFields[0]);
                            if (header != null){
                                try {
                                    ident.getDocument().insertString(ident.getCaretPosition(), protocol + "." + header, null);
                                }
                                catch (BadLocationException ex) {
                                    JOptionPane.showMessageDialog(topPanel,
                                            "An error occured while inserting your selection.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                    else if (type != null && type.equals("Message content")){
                        String[] paths = {"XML", "JSON"};
                        String path = (String) JOptionPane.showInputDialog(topPanel,
                                "Please choose the content data type you want to use:", "Selection dialog",
                                JOptionPane.PLAIN_MESSAGE, null, paths, paths[0]);
                        if (path != null && path.equals("XML")){
                            new XPathGeneratorEditor().initGUI("", true, ident);
                        }
                        else if (path != null && path.equals("JSON")){
                            new JSONPathGeneratorEditor().initGUI("", true, ident);
                        }
                    }
                }
            }
        });
        listPane.add(ident);

        listPane.add(new JLabel("Required guard value:", JLabel.RIGHT));
        address = new JTextField();
        address.setComponentPopupMenu(new FormPopUpMenu(editor, address));
        address.addFocusListener(MessageForm.COLOUR_CHANGER);
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
        
        JButton descriptionButton = new JButton("Helper for guard description");
        XMLSpecificationPanel.customizeButton(descriptionButton);
        descriptionButton.addActionListener((ActionEvent ae) -> {
            JOptionPane.showMessageDialog(listPane, DESCRIPTION_HELPER,
                    "Helper wizard", JOptionPane.INFORMATION_MESSAGE);
        });
        listPane.add(descriptionButton);
        
        JButton valueButton = new JButton("Helper for guard value");
        XMLSpecificationPanel.customizeButton(valueButton);
        valueButton.addActionListener((ActionEvent ae) -> {
            JOptionPane.showMessageDialog(listPane, VALUE_HELPER,
                    "Helper wizard", JOptionPane.INFORMATION_MESSAGE);
        });
        listPane.add(valueButton);
        
        topPanel.add(listPane);
        
        topPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        final JLabel tableTitle = new JLabel("Table of Guards", JLabel.RIGHT);
        tableTitle.setFont(font.deriveFont(attributes));
        topPanel.add(tableTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));


        AttributePanel.setTableConsistentLookAndFeel(guardTable);
        final JScrollPane guardScrollPane = JTable.createScrollPaneForTable(guardTable);

        add(topPanel, BorderLayout.NORTH);
        add(guardScrollPane, BorderLayout.CENTER);
        
        this.addMouseListener(MessageForm.FOCUS_CHANGER);
        topPanel.addMouseListener(MessageForm.FOCUS_CHANGER);
        listPane.addMouseListener(MessageForm.FOCUS_CHANGER);
        comboPanel.addMouseListener(MessageForm.FOCUS_CHANGER);
        guardTable.addMouseListener(MessageForm.FOCUS_CHANGER);
        guardScrollPane.addMouseListener(MessageForm.FOCUS_CHANGER);
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

