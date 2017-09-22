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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ArchitectureNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.GraphNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ObjectDeepCloner;

/**
 * a DataModelState encapsulates a single state of the DataModel class,
 * used for the XMLUndoManager
 * 
 * @author ns17
 */
public class DataModelState {
    
    /**
     * Each pattern may contain 0 or more component elements. Note
     * a trigger-based graph may not require component elements. Although
     * for completeness may include the interface to comply check
     */
    private final List<GraphNode> graphElements;
    
    /**
     * Getter for the list of graph nodes
     * @return graphElements
     */
    public List<GraphNode> getGraphElements(){
        return graphElements;
    }
    
    /**
     * The Graph is a set of nodes (vertices).
     * @see GraphNode
     */
    private final List<ArchitectureNode> architectureElements;
    
    /**
     * Getter for the list of architecture nodes
     * @return architectureElements
     */
    public List<ArchitectureNode> getArchitectureElements() {
        return architectureElements;
    }
    
    /**
     * Index of connection IDs to the source node.
     */
    private final Map<String, GraphNode> connectionIndex;
    
    /**
     * Getter for the map of index of connection IDs to the source node
     * @return connectionIndex 
     */
    public Map<String, GraphNode> getConnectionIndex() {
        return connectionIndex;
    }
    
    /**
     * Boolean to represent if the model has a start state.
     * Only one start state is allowed
     */
    private final boolean hasStart;
    
    /**
     * Getter for the boolean representing if the data model contains a start
     * @return hasStart
     */
    public boolean getHasStart() {
        return hasStart;
    }
    
    /**
     * constructor, which encapsulates the state of the data model
     * @param graphElements the list of graph nodes
     * @param architectureElements the list of architecture nodes
     * @param connectionIndex the map for transitions
     * @param hasStart the boolean representing if there is a start node in the model
     */
    public DataModelState(final List<GraphNode> graphElements, final List<ArchitectureNode> architectureElements, 
            final Map<String, GraphNode> connectionIndex, final boolean hasStart) {
        this.hasStart = hasStart;

        this.graphElements = new ArrayList<>();
        graphElements.forEach((graphNode) -> {
            this.graphElements.add((GraphNode) ObjectDeepCloner.deepCopy(graphNode));
        });
        
        this.architectureElements = new ArrayList<>();
        architectureElements.forEach((archNode) -> {
            this.architectureElements.add((ArchitectureNode) ObjectDeepCloner.deepCopy(archNode));
        });
        
        this.connectionIndex = new HashMap<>();
        for(String index: connectionIndex.keySet()){
            for (GraphNode testNode: this.graphElements){
                if (testNode.getLabel().equals(connectionIndex.get(index).getLabel())){
                    this.connectionIndex.put(index, testNode);
                    break;
                }
            }
        }
    }
}
