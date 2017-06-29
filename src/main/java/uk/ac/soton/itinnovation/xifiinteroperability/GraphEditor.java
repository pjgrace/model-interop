/////////////////////////////////////////////////////////////////////////
//
// University of Southampton IT Innovation Centre, 2015
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
// Created for Project : FIESTA (http://www.fiesta-iot.eu)
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

/**
 * This source code is a heavily modified version of mxGraph source code. To
 * comply with mxGraph license. The following statement is retained:
 *
 *  Copyright (c) 2001-2014, JGraph Ltd
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of the JGraph nor the
          names of its contributors may be used to endorse or promote products
          derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL JGRAPH BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.ac.soton.itinnovation.xifiinteroperability;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraphSelectionModel;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.BasicConfigurator;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.AbstractGraphElement;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ArchitectureNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.DataModel;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.GraphNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.SystemGraphComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.CustomGraph;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BehaviourGraphComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.EditorMenuBar;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.EditorPalette;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.GUIdentifier;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.specification.XMLStateMachine;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.InvalidTransitionException;


/**
 * Executable GUI framework that extends the BasicGraphEditor class. This
 * is an edited variation of the GraphEditor class developed as an example
 * of the mxGraph library in Java. We utilise a similar GUI layout, but
 * the content here is largely modified to reflect the Interoperability
 * Tool pattern editor.
 *
 * @author pjg
 */
public class GraphEditor extends BasicGraphEditor {



    /**
     * Application name.
     */
    private static final String APPTITLE = "Interoperability Pattern Editor";






    /**
     * The Graph editor is made up of two graphical views onto a single
     * data structure: the pattern has two editable views: 1) the
     * architecture graph, and 2) the behaviour graph.
     *
     * Here we create these two graphs and attach the rules and listeners.
     */
    public GraphEditor() {
        this(APPTITLE, new BehaviourGraphComponent(
            new CustomGraph() {
                // Overrides method to provide a cell label in the display
                @Override
                public String convertValueToString(final Object cell) {
                    if (cell instanceof mxCell) {
                        final String value = (String) ((mxCell) cell).getValue();
                        if (value.length() > 0) {
                            return value;
                        } else {
                            return null;
                        }
                    }
                    return super.convertValueToString(cell);
                }
            }),
            new SystemGraphComponent(new CustomGraph() {
                @Override
                public String convertValueToString(final Object cell) {
                    if (cell instanceof mxCell) {
                        final Object value = ((mxCell) cell).getValue();
                        return value.toString();
                    }

                    return super.convertValueToString(cell);
                }
            }));

        getBehaviourGraph().getConnectionHandler().setCreateTarget(false);
        ((BehaviourGraphComponent) getBehaviourGraph()).setDataModel(getDataModel());
        getSystemGraph().getConnectionHandler().setCreateTarget(false);
        ((SystemGraphComponent) getSystemGraph()).setDataModel(getDataModel());

        setListeners(getBehaviourGraph());
        setListeners(getSystemGraph());
        setRules();
    }

    /**
    * The Graph editor is made up of two graphical views onto a single
    * data structure: the pattern has two editable views: 1) the
    * architecture graph, and 2) the behaviour graph.
    * @param appTitle The title to be displayed on the GUI header bar
    * @param component The graph view for the pattern behaviour
    * @param arcComponent The graph view for the architecture specification
    */
    public GraphEditor(final String appTitle, final mxGraphComponent component,
            final mxGraphComponent arcComponent) {

        super(appTitle, component, arcComponent);

        // Creates the two palettes: i) arch shapes, ii) behaviour shapes
        final EditorPalette arcPalette = insertPalette(mxResources.get("images"));
        final EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));

        // Add the two node types to the architecture palette
         /**
         * The REST component. A web service interface implementing a REST
         * API. Invoked by client components.
         */
        arcPalette.addTemplate(XMLStateMachine.INTERFACE_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/server.png")),
                "image;image=/images/server.png",
                50, 50, XMLStateMachine.INTERFACE_LABEL);

        /**
         * A REST client. The user of a particular interface.
         */
        arcPalette.addTemplate(DataModel.CLIENT,
                new ImageIcon(GraphEditor.class.getResource("/images/workplace.png")),
                "image;image=/images/workplace.png",
                50, 50, DataModel.CLIENT);

        // Adds the six node types to the behaviour pallete

        /**
         * The start node. The waiting start
         */
        shapesPalette.addTemplate(XMLStateMachine.START_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/event_end.png")),
                "image;image=/images/event_end.png",
                50, 50, XMLStateMachine.START_LABEL);
        /**
         * The end node.
         */
        shapesPalette.addTemplate(XMLStateMachine.END_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/terminate.png")),
                "image;image=/images/terminate.png",
                50, 50, XMLStateMachine.END_LABEL);
        /**
         * The normal node
         */
        shapesPalette.addTemplate(XMLStateMachine.NORMAL_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/event.png")),
                "image;image=/images/event.png",
                50, 50, XMLStateMachine.NORMAL_LABEL);

        /**
         * Start with a trigger node
         */
        shapesPalette.addTemplate(XMLStateMachine.TRIGGERSTART_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/event_triggerstart.png")),
                "image;image=/images/event_triggerstart.png",
                50, 50, XMLStateMachine.TRIGGERSTART_LABEL);

        /**
         * Standard trigger node
         */
        shapesPalette.addTemplate(XMLStateMachine.TRIGGER_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/link.png")),
                "image;image=/images/link.png",
                50, 50, XMLStateMachine.TRIGGER_LABEL);

        /**
         * Standard Loop node
         */
        shapesPalette.addTemplate(XMLStateMachine.LOOP_LABEL,
                new ImageIcon(GraphEditor.class.getResource("/images/loop.png")),
                "image;image=/images/loop.png",
                50, 50, XMLStateMachine.LOOP_LABEL);
    }

    /**
     * Rules for connecting nodes: based on type rather than the mxGraph
     * value rules.
     * @param graphComponent The mxGraph component element.
     * @param evt The user interface event.
     * @throws InvalidTransitionException Error in the transition that is being checked.
     */
    private void checkRules(final mxGraphComponent graphComponent, final mxEventObject evt) throws InvalidTransitionException {

        // If source is a client or a interface
        mxICell connectionCell = ((mxCell) evt.getProperty("cell")).getSource();
        final String ident = GUIdentifier.getGUIdentifier(((mxCell) connectionCell).getId(), graphComponent);
        String type = getDataModel().getNode(ident).getType();

//        AbstractGraphElement transition = getDataModel().getTransition(connectionCell.getSource().getId());
        if (type.equalsIgnoreCase("component")) {
            throw new InvalidTransitionException("Cannot connect client component nodes");
        } else if (type.equalsIgnoreCase(XMLStateMachine.INTERFACE_LABEL)) {
            throw new InvalidTransitionException("Cannot connect interface component nodes");
        } else if (type.equalsIgnoreCase(XMLStateMachine.END_LABEL)) {
            throw new InvalidTransitionException("An end node cannot have an output transition");
        } else if ((type.equalsIgnoreCase(XMLStateMachine.TRIGGER_LABEL)) || (type.equalsIgnoreCase(XMLStateMachine.TRIGGERSTART_LABEL))) {
            final GraphNode node = (GraphNode) getDataModel().getNode(ident);
            if (node.getNumberTransitions() != 0) {
                throw new InvalidTransitionException("A trigger node can only have one output");
            }
        }

        connectionCell = ((mxCell) evt.getProperty("cell")).getTarget();
        if (connectionCell != null) {
            final String ident2 = GUIdentifier.getGUIdentifier(((mxCell) connectionCell).getId(), graphComponent);
            type = getDataModel().getNode(ident2).getType();
            if (type.equalsIgnoreCase(XMLStateMachine.TRIGGERSTART_LABEL)) {
                throw new InvalidTransitionException("A trigger start node cannot have an input transition");
            }
            if (type.equalsIgnoreCase(XMLStateMachine.START_LABEL)) {
                throw new InvalidTransitionException("A start node cannot have an input transition");
            }
        }
    }

    /**
     * Create the UI listeners.
     * 1) Listen for graph connections between two nodes
     * 2) Listen for graph element selections
     * @param graphComponent The graph to attach the listeners to.
     */
    private void setListeners(final mxGraphComponent graphComponent) {
        /**
         * Listen for a newly created edge between two vertices in the
         * drawn graph.
         */
        graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT,
            new mxIEventListener() {
                @Override
                public void invoke(final Object sender, final mxEventObject evt) {
                    try {
                        checkRules(graphComponent, evt);
                    } catch (InvalidTransitionException ex) {
                        JOptionPane.showMessageDialog(graphComponent.getParent(),
                        ex.getLocalizedMessage(),
                        "Graph edge connection error",
                        JOptionPane.PLAIN_MESSAGE);
                        graphComponent.getGraph().getModel().beginUpdate();
                         try {
                           graphComponent.getGraph().getModel().remove(evt.getProperty("cell"));
                        } finally {
                           graphComponent.getGraph().getModel().endUpdate();
                        }
                        return;
                    }

                    final mxCell connData = (mxCell) evt.getProperty("cell");
                    connData.setConnectable(false);
                    getDataModel().addConnection(connData.getId(), connData.getSource().getId(),
                            connData.getTarget().getId());
                };
            }
        );

        graphComponent.getGraph().addListener(mxEvent.CELL_CONNECTED, new mxIEventListener() {
            @Override
            public void invoke(final Object sender, final mxEventObject evt) {
                final mxCell connectionCell = (mxCell) evt.getProperty("edge");

                if (connectionCell.getTarget() == null) {
                    return;
                }

                try {
                    getDataModel().updateConnection(connectionCell.getId(), connectionCell.getSource().getId(), connectionCell.getTarget().getId());
                } catch (InvalidTransitionException ex) {
                    JOptionPane.showMessageDialog(graphComponent.getParent(),
                        ex.getLocalizedMessage(),
                        "Graph edge reconnection",
                        JOptionPane.PLAIN_MESSAGE);
                    getUndoModel().undo();
                }
            }
        });

        /**
         * Listen for a selection event: this can be either a vertex or
         * edge that has been selected.
         */
        graphComponent.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
            @Override
            public void invoke(final Object sender, final mxEventObject evt) {
                graphComponent.validateGraph();
                if (sender instanceof mxGraphSelectionModel) {
                    for (Object cell : ((mxGraphSelectionModel) sender).getCells()) {
                        // Get the user interface ID of the selection
                        final String ident = GUIdentifier.getGUIdentifier(((mxCell) cell).getId(), graphComponent);

                        updateTableView(ident);
                        AbstractGraphElement grpghM = null;
                        grpghM = getDataModel().getNode(ident);
                        if (grpghM == null) {
                            grpghM = getDataModel().getTransition(ident);
                        }
                        if (grpghM != null) {
                            final CardLayout cardLayout = (CardLayout) getAttributePanel().getLayout();
                            String type = grpghM.getType();
                            if (type.contains(XMLStateMachine.START_LABEL)) {
                                type = XMLStateMachine.START_LABEL;
                            }
                            cardLayout.show(getAttributePanel(), type);
                        }
                    }
                }
            }
        });

        /* the variable is used as a reference to the JPanel in the listener implementation */
        JPanel panel = this;
        
         /**
         * Listen for a selection event: this can be either a vertex or
         * edge that has been selected.
         */
        graphComponent.addListener(mxEvent.LABEL_CHANGED, new mxIEventListener() {

            @Override
            public void invoke(final Object obj, final mxEventObject evt) {
                DataModel dataModel = getDataModel();
                mxCell labelCell = (mxCell) evt.getProperty("cell");
                String newLabel = (String) evt.getProperty("value");
                final String ident = GUIdentifier.getGUIdentifier(((mxCell) labelCell).getId(), graphComponent);
                final AbstractGraphElement node = dataModel.getNode(ident);
                final String originalLabel = node.getLabel();
                newLabel = newLabel.replaceAll("\\s+", "_");
                
                if (node instanceof GraphNode){
                    if (!(newLabel.equalsIgnoreCase(originalLabel)) && dataModel.graphIdentExist(newLabel)){
                        JOptionPane.showMessageDialog(panel, 
                                "Please choose a different label for this state.",
                                "Renaming error", 
                                JOptionPane.ERROR_MESSAGE);
                        labelCell.setValue(originalLabel);
                    }
                    else {
                        node.setLabel(newLabel);
                        labelCell.setValue(newLabel);
                        dataModel.updateConnectionLabel(originalLabel, newLabel);
                    }
                }
                
                else if (node instanceof ArchitectureNode) {
                    if (!(newLabel.equalsIgnoreCase(originalLabel)) && dataModel.archIdentExist(newLabel)){
                        JOptionPane.showMessageDialog(panel, 
                                "Please choose a different label identifier for this component.",
                                "Renaming error", 
                                JOptionPane.ERROR_MESSAGE);
                        labelCell.setValue(originalLabel);
                    }
                    else {
                        node.setLabel(newLabel);
                        labelCell.setValue(newLabel);
                        
                        
                        // Get the user interface ID of the selection and update the table
                        final String id = GUIdentifier.getGUIdentifier(labelCell.getId(), graphComponent);
                        updateTableView(id);
                    }
                }
            }
        });
    }

    /**
     * Start the editor GUI tool.
     * @param args The command line parameters (not used & ignored).
     */
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e1) {
            ServiceLogger.LOG.error("Look and feel class missing " + e1.getMessage());
        } catch (InstantiationException e1) {
            ServiceLogger.LOG.error("Unable to construct GUI: " + e1.getMessage());
        } catch (IllegalAccessException e1) {
             ServiceLogger.LOG.error("Illegal Access: " + e1.getMessage());
        } catch (UnsupportedLookAndFeelException e1) {
            ServiceLogger.LOG.error("Invalid Look and feel try: " + e1.getMessage());
        }

        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

        /**
         * Set up the logger to display to standard output i.e. console.
         */
        BasicConfigurator.configure();

        final GraphEditor editor = new GraphEditor();
        editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
    }
}