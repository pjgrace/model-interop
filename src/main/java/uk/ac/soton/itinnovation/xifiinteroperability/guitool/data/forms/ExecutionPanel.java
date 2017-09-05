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
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.RESTComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ObjectDeepCloner;
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
    private final transient BasicGraphEditor editor;
     
    /**
     * a reference to the behaviour component
     */
    private transient mxGraphComponent behaviourComponent;
    
    /**
     * a reference to the system component
     */
    private transient mxGraphComponent systemComponent;
    
    /**
     * reference to the panel containing the two graphs
     */
    private transient final JPanel graphs;
    
    /**
     * reference to the table with the interfaces and their respective ports
     */
    private transient final JTable portsTable;
    
    /**
     * constructor for the panel, initialises the GUI components
     * @param editor the editor reference
     */
    public ExecutionPanel(BasicGraphEditor editor){
        super(new BorderLayout());
        
        this.editor = editor;
        
        graphs = new JPanel(new GridLayout(2, 1));
        graphs.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        behaviourComponent = new BehaviourGraphComponent(editor.getBehaviourGraph().getGraph());
        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));
        graphs.add(behaviourComponent);
        
        systemComponent = new SystemGraphComponent(editor.getSystemGraph().getGraph());
        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
        systemComponent.setEnabled(false);
        graphs.add(systemComponent);
             
        add(graphs, BorderLayout.CENTER);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Interface:", "Running on port:"};
        Object[][] data = {};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        portsTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }  
        };
        portsTable.setPreferredScrollableViewportSize(new Dimension(portsTable.getPreferredSize().width, portsTable.getRowHeight()*8));
        portsTable.setFillsViewportHeight(true);
        tablePanel.add(new JScrollPane(portsTable), BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.SOUTH);
    }
    
    /**
     * called when starting a new test, refreshes the graph components and the table of interfaces
     * @param restComponents a map linking component IDs to rest component objects
     */
    public void refreshGraph(Map<String, RESTComponent> restComponents){
        mxGraph behaviourGraph = new mxGraph((mxIGraphModel) ObjectDeepCloner.deepCopy(editor.getBehaviourGraph().getGraph().getModel()),
                editor.getBehaviourGraph().getGraph().getStylesheet());
        mxGraph systemGraph = new mxGraph((mxIGraphModel) ObjectDeepCloner.deepCopy(editor.getSystemGraph().getGraph().getModel()),
                editor.getSystemGraph().getGraph().getStylesheet());
        behaviourComponent = new BehaviourGraphComponent(behaviourGraph);
        systemComponent = new SystemGraphComponent(systemGraph);
        
        graphs.removeAll();
        
        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));
        graphs.add(behaviourComponent);
        
        systemComponent.setEnabled(false);
        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
        graphs.add(systemComponent);
             
        graphs.revalidate();
        graphs.repaint();
        
        List<List<String>> data = new ArrayList<>();
        restComponents.values().forEach((component) -> {
            component.getInterfaces().forEach((restInterface) -> {
                data.add(new ArrayList<>(Arrays.asList(restInterface.getURL(), Integer.toString(restInterface.getPort()))));
            });
        });
        
        refreshTable(data);
    }
    
    /**
     * refresh the table of interfaces and port numbers
     * @param data the new data to insert into the table
     */
    private void refreshTable(List<List<String>> data){
        refreshTable();
        data.forEach((row) -> {
            ((DefaultTableModel) portsTable.getModel()).addRow(row.toArray());
        });
    }
    
    /**
     * refresh the table without inserting new data into it
     */
    private void refreshTable(){
        DefaultTableModel model = (DefaultTableModel) portsTable.getModel();
        model.setRowCount(0);
    }
    
    /**
     * resets the Execution panel, by reseting the graph components
     */
    public void resetGraph(){
        behaviourComponent = new BehaviourGraphComponent(editor.getBehaviourGraph().getGraph());
        systemComponent = new SystemGraphComponent(editor.getSystemGraph().getGraph());

        graphs.removeAll();

        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));
        graphs.add(behaviourComponent);

        systemComponent.setEnabled(false);
        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
        graphs.add(systemComponent);

        graphs.revalidate();
        graphs.repaint();
        
        // reset the table
        refreshTable();
    }
    
    /**
     * a method to set the current test state by changing the selection in the graph
     * @param labelID the labelID of the current state
     */
    public void setTestState(String labelID){
        mxGraph graph = behaviourComponent.getGraph();
        String guiID = editor.getDataModel().getNodeByLabel(labelID).getUIIdentifier();
        mxCell toSelect = (mxCell) ((mxGraphModel) graph.getModel()).getCell(guiID);
        graph.getSelectionModel().setCell(toSelect);
    }
}
