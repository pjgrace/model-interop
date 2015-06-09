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

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.Message;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
public class MessageForm extends JPanel {

    /**
     * The form first contains text fields to input the main content about
     * the message.
     */

    /**
     * Text field to enter the urlEndpoint destination of the message.
     */
    private final transient JTextField url;

     /**
     * Text field to enter the resource path destination of the message.
     */
    private final transient JTextField path;

    /**
     * Text field to enter the http body content.
     */
    private final transient JTextArea body;

    /**
     * Text field to enter the http method contentType.
     */
    private final transient JComboBox method;

    /**
     * Text field to enter the http body content.
     */
    private final transient JComboBox contentType;

    /**
     * The Message form has a table list of header fields to
     * be added to the message. This is a simple table
     * of header and values.
     */
    private final transient MessageTableModel messageView;

    /**
     * The text field to enter the new header field name.
     */
    private final transient JTextField header = new JTextField();

    /**
     * The text field to enter the new header field value.
     */
    private final transient JTextField headerValue = new JTextField();

    /**
     * Each instance of a form reflects the values in the data model. This
     * is a reference to that data structure.
     */
    private transient Message mirrorNode;

    /**
     * Create a new message form.
     * @param methodCombo The list of message methods to go into the method field
     * @param dataTypes The list of data types that are selectable.
     * @param editor The context of the form.
     */
    public MessageForm(final String[] methodCombo, final String[] dataTypes,
            final BasicGraphEditor editor) {
        super(new BorderLayout());
        messageView = new MessageTableModel();

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Info Panel
        final JPanel listPane = new JPanel();
        GridLayout gridLayout = new GridLayout(6 , 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        listPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        listPane.setLayout(gridLayout);

        final JLabel title = new JLabel("Message Data", JLabel.CENTER);
        final Font font = title.getFont();
        final Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        title.setFont(font.deriveFont(attributes));

        listPane.add(title);
        listPane.add(new JLabel("", SwingConstants.LEFT));

        listPane.add(new JLabel("URL pointer:",  JLabel.RIGHT));
        url = new JTextField();
        url.setComponentPopupMenu(new FormPopUpMenu(editor, url));
        listPane.add(url);

        listPane.add(new JLabel("Resource path:", JLabel.RIGHT));
        path = new JTextField();
        path.setComponentPopupMenu(new FormPopUpMenu(editor, path));
        listPane.add(path);

        listPane.add(new JLabel("Method:", JLabel.RIGHT));
        method = new JComboBox(methodCombo.clone());
        listPane.add(method);

        listPane.add(new JLabel("Type:", JLabel.RIGHT));
        contentType = new JComboBox(dataTypes.clone());
        listPane.add(contentType);


        body = new JTextArea("", 20, 10);

        body.setLineWrap(true);
        body.setComponentPopupMenu(new FormPopUpMenu(editor, body));
        topPanel.add(listPane);
        topPanel.add(new JLabel("Message Content:"));
        topPanel.add(new JScrollPane(body));

//        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));


        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        final JLabel tableTitle = new JLabel("Table of Headers", JLabel.RIGHT);
        tableTitle.setFont(font.deriveFont(attributes));
        topPanel.add(tableTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        final JTable nodetable = new JTable(messageView);
        final JScrollPane messageScrollPane = JTable.createScrollPaneForTable(nodetable);

//        add(messageScrollPane, BorderLayout.CENTER);
        topPanel.add(messageScrollPane);

        // Interface add Panel
        final JPanel newIntfPane = new JPanel();
        gridLayout = new GridLayout(4 , 2);
        newIntfPane.setLayout(gridLayout);
        newIntfPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        final JLabel stitle = new JLabel(" Add new Header Field");
        stitle.setFont(font.deriveFont(attributes));
        newIntfPane.add(stitle);
        newIntfPane.add(new JLabel("", JLabel.LEFT));

        newIntfPane.add(new JLabel(" Header ", JLabel.LEFT));
        newIntfPane.add(new JLabel("Value", JLabel.LEFT));

        newIntfPane.add(header);
        newIntfPane.add(headerValue);

        final JButton addIntf = new JButton("Add Header");
        addIntf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
              mirrorNode.addHeader(header.getText(), headerValue.getText());
              messageView.clearData();
              messageView.setData(mirrorNode);
              header.setText("");
              headerValue.setText("");
            }
          });
        newIntfPane.add(new JLabel(""));
        newIntfPane.add(addIntf);

        topPanel.add(newIntfPane);

        add(topPanel, BorderLayout.CENTER);
        final JButton update = new JButton("Update Message");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                mirrorNode.addMessage(url.getText(),
                    path.getText(), method.getSelectedItem().toString(),
                        contentType.getSelectedItem().toString(), body.getText());
            }
          });

        add(update, BorderLayout.SOUTH);

    }

    /**
     * Update the form text fields with the data from a network message. This
     * is a http message at present.
     * @param msg The HTTP message data to use to update text boxes.
     */
    public final void setData(final Message msg) {
        mirrorNode = msg;
        url.setText(msg.getEndpoint());
        path.setText(msg.getPath());

        if (msg.getHTTPMethod() == null) {
            method.setSelectedItem(0);
        } else {
            method.setSelectedItem(msg.getHTTPMethod().toUpperCase(Locale.ENGLISH)); //setText(msg.method);
        }
        if (msg.getDataType() == null) {
            method.setSelectedItem(0);
        } else {
            contentType.setSelectedItem(msg.getDataType().toUpperCase(Locale.ENGLISH));
        }
        body.setText(msg.getHTTPBody());
        messageView.setData(msg);
    }

    /**
     * Empty all the fields in the GUI form, so that is becomes and empty form.
     */
    public final void clearData() {
        messageView.clearData();
        url.setText("");
        path.setText("");
        method.setSelectedIndex(0); //method.setText("");
        contentType.setSelectedIndex(0);
        body.setText("");
    }
}
