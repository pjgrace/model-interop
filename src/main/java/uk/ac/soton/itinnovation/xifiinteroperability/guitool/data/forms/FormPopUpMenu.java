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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.DataModel;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.GraphNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;

/**
 * The menu that pops up when right click on a field in the UI form. Generally
 * applies cut/copy/paste actions to the menu.
 *
 * @author pjg
 */
public class FormPopUpMenu extends JPopupMenu {

    /**
     * The textfield this pop up instance works with.
     */
    private final transient JTextComponent bufferField;
    
    /**
     * reference to the data model
     */
    private final DataModel dataModel;

    /**
     * Create a new pop up menu associated with a text field.
     * @param editor The editor context.
     * @param txtField The text field to cut/copy/paste.
     */
    public FormPopUpMenu(final BasicGraphEditor editor, final JTextComponent txtField) {
        super();
        add(editor.bind("Copy", new CopyAction()));
        add(editor.bind("Paste", new PasteAction()));
        add(editor.bind("Cut", new CutAction()));
        add(editor.bind("Insert pattern data", new InsertAction()));
        this.bufferField = txtField;
        this.bufferField.setToolTipText("Pattern data can be inserted by clicking the "
                + "mouse right button and choosing the option 'Insert pattern data'");
        this.dataModel = editor.getDataModel();
    }

     /**
     * Copy selected text from field.
     */
    public class CopyAction extends AbstractAction {
        /**
         * Copy text from text field to clipboard buffer.
         * @param actEvent The UI event
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            bufferField.copy();

        }
    }

    /**
     * The text field cut action.
     */
    public class CutAction extends AbstractAction {
        /**
         * Cut text from text field to clipboard buffer.
         * @param actEvent The UI event
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            bufferField.cut();

        }
    }

    /**
     * The textfield paste action.
     */
    public class PasteAction extends AbstractAction {
        /**
         * Paste text from clipboard to text field.
         * @param actEvent The UI event
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            bufferField.paste();

        }
    }
    
    /**
     * insert action for inserting pattern specific data
     */
    public class InsertAction extends AbstractAction {
        /**
         * Choose pattern data and insert it
         * @param actEvent the UI event
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent){
            GraphNode startNode = dataModel.getStartNode();
            List<String> patternDataIDs = new ArrayList<>();
            startNode.getConstantData().forEach((data) -> {
                patternDataIDs.add(data.getFieldName());
            });
            if (patternDataIDs.size() > 0){
                Object result = JOptionPane.showInputDialog(bufferField, 
                        "Please choose the ID of the pattern data you want to insert.", 
                        "Inserting pattern data", JOptionPane.PLAIN_MESSAGE,
                        null, patternDataIDs.toArray(), patternDataIDs.get(0));
                
                if (result == null){
                    return;
                }
                String selected = result.toString();
                selected = "$$patterndata." + selected + "$$";
                try {
                    bufferField.getDocument().insertString(bufferField.getCaretPosition(), selected, null);
                } catch (BadLocationException ex) {
                    // empty block since we shouldn't be entering this catch block
                }
            }
            else {
                JOptionPane.showMessageDialog(bufferField, 
                        "You haven't added any pattern data, yet.",
                        "No pattern data found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
