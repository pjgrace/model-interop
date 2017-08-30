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
// Created By : Nikolay Stanchev
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
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BehaviourGraphComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.SystemGraphComponent;

/**
 * A panel to display the behaviour and the system graph when running a test
 * 
 * @author ns17
 */
public class ExecutionPanel extends JPanel {
      
    /**
     * a reference to the editor
     */
    private final BasicGraphEditor editor;
    
    /**
     * constructor for the panel, initialises the GUI components
     * @param editor the editor reference
     */
    public ExecutionPanel(BasicGraphEditor editor){
        super(new BorderLayout());
        
        this.editor = editor;
        
        JPanel graphs = new JPanel(new GridLayout(2, 1));
        graphs.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        mxGraphComponent behaviourComponent = new BehaviourGraphComponent(editor.getBehaviourGraph().getGraph());
        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));
        graphs.add(behaviourComponent);
        
        mxGraphComponent systemComponent = new SystemGraphComponent(editor.getSystemGraph().getGraph());
        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
        systemComponent.setEnabled(false);
        graphs.add(systemComponent);
             
        add(graphs, BorderLayout.CENTER);
    }
    
    /**
     * a method to set the current test state by changing the selection in the graph
     * @param labelID the labelID of the current state
     */
    public void setTestState(String labelID){
        mxGraph graph = editor.getBehaviourGraph().getGraph();
        String guiID = editor.getDataModel().getNodeByLabel(labelID).getUIIdentifier();
        mxCell toSelect = (mxCell) ((mxGraphModel) graph.getModel()).getCell(guiID);
        graph.getSelectionModel().setSingleSelection(false);
        graph.clearSelection();
        graph.getSelectionModel().setCell(toSelect);
        graph.getSelectionModel().setSingleSelection(true);
    }
}
