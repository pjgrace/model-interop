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

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.XMLEditorKit;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.DataModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import static uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.XMLDocument.PLAIN_ATTRIBUTES;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.ButtonCustomizer;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.GraphGenerator;

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
     * a getter for the reference to the data model
     * @return the dataModel reference
     */
    public final DataModel getDataModel(){
        return dataModel;
    }
    
    /**
     * The core element of the panel - the text area to display the xml.
     */
    private final transient JEditorPane xmlSpecification;

    /**
     * Create the UI panel element with the given data model.
     * @param dModel The tool data model to build specification from.
     * @param editor the graph editor
     */
    public XMLSpecificationPanel(final DataModel dModel, BasicGraphEditor editor) {
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
        xmlSpecification.setEditorKit(new XMLEditorKit(this, false));
        

        // Create the scrolling text area with the content.
        final JScrollPane areaScrollPane = new JScrollPane(xmlSpecification);
        areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(1000, 1000));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        
         // a button to toggle editing mode
        JButton toggleEditingButton = new JButton("Enable XML pattern editing");
        ButtonCustomizer.customizeButton(toggleEditingButton);
        
        JButton submitChangesButton = new JButton("Validate and update changes");
        submitChangesButton.setVisible(false);
        ButtonCustomizer.customizeButton(submitChangesButton);
        submitChangesButton.addActionListener((ActionEvent ae) -> {
            if (((XMLEditorKit) xmlSpecification.getEditorKit()).isChanged()){
                String xml;
                try {
                    xml = xmlSpecification.getDocument().getText(0, xmlSpecification.getDocument().getLength()).replaceAll("\n", "");
                } 
                catch (BadLocationException ex) {
                    JOptionPane.showMessageDialog(this, "Error while processing the edited version of the xml pattern", 
                            "Error", JOptionPane.ERROR_MESSAGE, null);
                    return;
                }

                if (xml != null){
                    String oldXml = editor.getDataModel().getGraphXML();

                    clearPattern(editor);

                    GraphGenerator graphGenerator = new GraphGenerator(editor);
                    try {
                        graphGenerator.createGraph(GraphGenerator.loadXMLFromString(xml));
                        final mxHierarchicalLayout layout = new mxHierarchicalLayout(editor.getBehaviourGraph().getGraph());
                        layout.execute(editor.getBehaviourGraph().getGraph().getDefaultParent());
                        JOptionPane.showMessageDialog(this, 
                                "Successfully validated and updated the edited XML pattern.", 
                                "Success", JOptionPane.PLAIN_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error while processing the edited version of the xml pattern", 
                            "Error", JOptionPane.ERROR_MESSAGE, null);
                        try {
                            clearPattern(editor);
                            graphGenerator.createGraph(GraphGenerator.loadXMLFromString(oldXml));
                            final mxHierarchicalLayout layout = new mxHierarchicalLayout(editor.getBehaviourGraph().getGraph());
                            layout.execute(editor.getBehaviourGraph().getGraph().getDefaultParent());
                        } catch (Exception e) {
                            // since the old XML pattern is being generated we shouldn't
                            // be entering this catch block
                        }
                    }              
                }
                ((XMLEditorKit)xmlSpecification.getEditorKit()).resetSaved();
            }
            toggleEditingButton.doClick();  
        });
        
        toggleEditingButton.addActionListener((ActionEvent ae) -> {
            XMLEditorKit editorKit = ((XMLEditorKit)xmlSpecification.getEditorKit());
            if (editorKit.editingAllowed()){
                if (editorKit.isChanged() && !editorKit.changesSaved()){
                    if (JOptionPane.showConfirmDialog(this, 
                            "Your changes are not validated and will not be updated in the pattern unless "
                                    + "you click the validation button on the top before disabling editing. "
                                    + "Do you still want to continue ? ", 
                            "Changes not updated", JOptionPane.YES_NO_OPTION, 
                            JOptionPane.WARNING_MESSAGE, null) == JOptionPane.NO_OPTION){
                        return;
                    }
                }
                toggleEditingButton.setText("Enable XML pattern editing");
                editorKit.toggleEditingMode();
                submitChangesButton.setVisible(false);
                StyleConstants.setBackground(PLAIN_ATTRIBUTES, Color.WHITE);
                displayXMLSpecification();
            }
            else {
                toggleEditingButton.setText("Disable XML pattern editing");
                editorKit.toggleEditingMode();
                editorKit.resetChanged();
                editorKit.resetSaved();
                submitChangesButton.setVisible(true);
                StyleConstants.setBackground(PLAIN_ATTRIBUTES, new Color(241, 218, 218));
                displayXMLSpecification();
            }
        });
        
        buttonsPanel.add(toggleEditingButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10,0)));
        buttonsPanel.add(submitChangesButton);
        
        add(areaScrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.NORTH);
    }

    /**
     * Update the text area to display the current xml specification for the
     * drawn graph.
     */
    public final void displayXMLSpecification() {
        if (dataModel != null) {
            int caretPosition = xmlSpecification.getCaretPosition();
            this.xmlSpecification.setDocument(this.xmlSpecification.getEditorKit().createDefaultDocument());
            this.xmlSpecification.setText(dataModel.getGraphXML());
            xmlSpecification.setCaretPosition(caretPosition);
        }
    }
    
    private void clearPattern(BasicGraphEditor editor){
        final mxGraph graph = editor.getBehaviourGraph().getGraph();
        final mxCell root = new mxCell();
        root.insert(new mxCell());
        graph.getModel().setRoot(root);

        final mxGraph agraph = editor.getSystemGraph().getGraph();
        final mxCell root2 = new mxCell();
        root2.insert(new mxCell());
        agraph.getModel().setRoot(root2);
        
        editor.getDataModel().clearData();
        editor.updateTableView(null);
    }
}

