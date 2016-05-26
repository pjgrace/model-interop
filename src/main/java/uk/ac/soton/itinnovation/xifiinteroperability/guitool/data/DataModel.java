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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.soton.itinnovation.xifiinteroperability.SystemProperties;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.GUIdentifier;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.specification.XMLStateMachine;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine.InvalidTransitionException;

/**
 * The Pattern Editor is built upon a data model. That is, the graph edited
 * visually corresponds to a graph data structure. This data structure
 * is concretised using XML. The XML pattern can be used to execute the
 * automated testing tool.
 *
 * @author pjg
 */
public class DataModel {

    /**
     * The constant label for a client graph element.
     */
    public static final String CLIENT = "client";

    /**
     * Each pattern may contain 0 or more component elements. Note
     * a trigger-based graph may not require component elements. Although
     * for completeness may include the interface to comply check
     */
    private final transient List<ArchitectureNode> archElements;

    /**
     * The Graph is a set of nodes (vertices).
     * @see GraphNode
     */
    private final transient List<GraphNode> graphElements;

    /**
     * Index of connection IDs to the source node.
     */
    private final transient Map<String, GraphNode> connectionIndex;

    /**
     * Initialse the data model.
     */
    public DataModel() {
       this.graphElements = new ArrayList();
       this.connectionIndex = new HashMap();
       this.archElements = new ArrayList();
   }

   /**
    * Get the data node specified by the UI ID from the graph. That is,
    * select the node data.
    *
    * @param nodeID The User Interface generated ID.
    * @return The data
    */
   public final AbstractGraphElement getNode(final String nodeID) {
       for (GraphNode e : this.graphElements) {
           if (e.getUIIdentifier().equalsIgnoreCase(nodeID)) {
                   return e;
           }
       }
       for (ArchitectureNode e : this.archElements) {
           if (e.getUIIdentifier().equalsIgnoreCase(nodeID)) {
                   return e;
           }
       }
       return null;
   }

   /**
    * Detect if there is a start node in the current graph specification.
    * @return Returns true where a start node has been added.
    */
   public final boolean containsStart() {
       for (GraphNode e : this.graphElements) {
           if (e.getType().contains(XMLStateMachine.START_LABEL)) {
                   return true;
           }
       }
       return false;
   }

   /**
    * Get the data node specified by the label from the graph. That is,
    * select the node data.
    *
    * @param label The state label.
    * @return The data
    */
   public final AbstractGraphElement getNodeByLabel(final String label) {
       for (GraphNode e : this.graphElements) {
           if (e.getLabel().equalsIgnoreCase(label)) {
               return e;
           }
       }
       return null;
   }

    /**
    * Get the data node specified by the label from the graph. That is,
    * select the node data.
    *
    * @param label The state label.
    * @return The data
    */
   public final AbstractGraphElement getComponentByLabel(final String label) {
       for (ArchitectureNode e : this.archElements) {
           if (e.getIdentifier().equalsIgnoreCase(label)) {
               return e;
           }
       }
       return null;
   }

   /**
    * Retrieve a transition description from the graph based on the UI
    * identifier.
    * @param ident GUI identifier; in mxGraph = integer > 0
    * @return  The graph element transition info.
    */
   public final AbstractGraphElement getTransition(final String ident) {
       final GraphNode grpNode = this.connectionIndex.get(ident);
       if (grpNode == null) {
            return null;
       } else {
           return grpNode.getTransition(ident);
       }
   }

   /**
    * Convert the graph data into an XML representation that matches the
    * schema of the Interoperability Pattern Testing tool input.
    * @return The XML as a String
    */
   public final String getGraphXML() {
       final StringBuilder graphXML = new StringBuilder();
       graphXML.append("<?xml version=\"1.0\"?>\n<pattern xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
               + "xsi:noNamespaceSchemaLocation =\"" + SystemProperties.PATTERNSCHEMA + "\">");

       for (GraphNode e : this.graphElements) {
            if (e.getConstantData().size() > 0) {
                graphXML.append(e.generatePatternDataXML());
            }
        }

       if (this.archElements.size() > 0) {
            graphXML.append("\n\t<architecture>");
            for (ArchitectureNode e : this.archElements) {
                graphXML.append("\t\t").append(e.generateTransitionXML());
            }
            graphXML.append("\n\t</architecture>");
       }
       if (this.graphElements.size() > 0) {
            graphXML.append("\n\t<behaviour>");
            for (GraphNode e : this.graphElements) {
                graphXML.append("\t\t").append(e.generateTransitionXML());
            }
            graphXML.append("\n\t</behaviour>");
       }
       graphXML.append("\n</pattern>");
       String xml = graphXML.toString();
       xml = xml.replaceAll("&(?!amp;)", "&amp;");
       return xml;
   }

   /**
    * Add a new node to the data model.
    * @param ident The identifier of the new node element.
    * @param label The label of the new node element.
    * @param type. The type of the node (client or interface).
    */
   public final void addNode(final String ident, final String label, final String type) {
       switch(type) {
           case XMLStateMachine.INTERFACE_LABEL:
               this.archElements.add(new ArchitectureNode(GUIdentifier.setArchID(ident), label));
               break;
           case CLIENT:
                this.archElements.add(new ArchitectureNode(GUIdentifier.setArchID(ident), label));
                break;
           default:
                this.graphElements.add(new GraphNode(ident, label));
       }
   }

   /**
    * Remove the identified element from the data model.
    * @param ident The element to remove.
    */
   public final void deleteNode(final String ident) {
       GraphNode toDelete = this.connectionIndex.get(ident);
       if (toDelete != null) {
           toDelete.deleteTranstion(ident);
           this.connectionIndex.remove(ident);
           return;
       }
       for (GraphNode e : this.graphElements) {
           if (e.getUIIdentifier().equalsIgnoreCase(ident)) {
                toDelete = e;
                break;
           }
       }
       if (toDelete != null) {
            this.graphElements.remove(toDelete);
        } else {
           ArchitectureNode aDelete = null;
           for (ArchitectureNode e : this.archElements) {
                if (e.getUIIdentifier().equalsIgnoreCase(ident)) {
                    aDelete = e;
                    break;
                }
            }
            this.archElements.remove(aDelete);
        }
   }

   /**
    * Add a new connection in the model.
    * @param connID The identifier of the connection.
    * @param srcID The ID of the source node of the connection.
    * @param trgtID The ID of the target node of the connection.
    */
   public final void addConnection(final String connID, final String srcID, final String trgtID) {
       // use the type of the src to determine connection type
       final String type = getNode(srcID).getType();
       // if src is a start or normal
       if (type.equalsIgnoreCase(XMLStateMachine.START_LABEL) || type.equalsIgnoreCase(XMLStateMachine.NORMAL_LABEL)) {
            final AbstractGraphElement connection = new Guard(connID, "guard", getNode(trgtID));
            final GraphNode src = (GraphNode) getNode(srcID);
            this.connectionIndex.put(connID, src);
            src.addTransition(connection);
       } else if (type.equalsIgnoreCase(XMLStateMachine.TRIGGER_LABEL) || type.equalsIgnoreCase(XMLStateMachine.TRIGGERSTART_LABEL)) {
           final AbstractGraphElement connection = new Message(connID, "message", getNode(trgtID));
           final GraphNode src = (GraphNode) getNode(srcID);
           this.connectionIndex.put(connID, src);
           src.addTransition(connection);
       }
   }

   /**
    * Trace through the graph and change all references to the old label to
    * the new label.
    *
    * @param original The original state label
    * @param newLabel The new state label.
    */
   public final void updateConnectionLabel(final String original, final String newLabel) {
       for (GraphNode e : this.graphElements) {
            for (int i = 0; i < e.getNumberTransitions(); i++) {
                final AbstractGraphElement transition = e.getTransition(i);
                if (transition.getClass().toString().contains("Guard")) {
                    final Guard gTrans = (Guard) transition;
                    if (gTrans.getTarget().equalsIgnoreCase(original)) {
                        gTrans.setTarget(newLabel);
                    }
                } else if (transition.getClass().toString().contains("Message")) {
                    final Message gTrans = (Message) transition;
                    if (gTrans.getTarget().equalsIgnoreCase(original)) {
                        gTrans.setTarget(newLabel);
                    }
                }
            }
        }
   }

   /**
    * Update a connection in the graph to the new target label. Essentially
    * changing a connection from pointing between 2 nodes to other nodes.
    * @param connID The Id of the connection
    * @param srcID The id of the connection source.
    * @param trgtID The id of the connection target.
    * @throws InvalidTransitionException Error where trying to change to a connection
    * that doesn't exist in the graph.
    */
   public final void updateConnection(final String connID, final String srcID, final String trgtID) throws InvalidTransitionException {
        final GraphNode connx = this.connectionIndex.get(connID);
        if (connx == null) {
            return;
        }
        if (connx.getUIIdentifier().equalsIgnoreCase(srcID)) {
            final String type = connx.getType();
            if (type.equalsIgnoreCase(XMLStateMachine.START_LABEL) || type.equalsIgnoreCase(XMLStateMachine.NORMAL_LABEL)) {
                final Guard grd = (Guard) connx.getTransition(connID);
                if (grd != null) {
                    grd.setTarget(getNode(trgtID).getLabel());
                }
           } else if (type.equalsIgnoreCase(XMLStateMachine.TRIGGER_LABEL) || type.equalsIgnoreCase(XMLStateMachine.TRIGGERSTART_LABEL)) {
                final Message msg = (Message) connx.getTransition(connID);
                if (msg != null) {
                    msg.setTarget(getNode(trgtID).getLabel());
                }
           }
        } else {
            String type = connx.getType();
            if (type.equalsIgnoreCase(XMLStateMachine.START_LABEL) || type.equalsIgnoreCase(XMLStateMachine.NORMAL_LABEL)) {
                type = getNode(srcID).getType();
                if (!(type.equalsIgnoreCase(XMLStateMachine.START_LABEL) || type.equalsIgnoreCase(XMLStateMachine.NORMAL_LABEL))) {
                    throw new InvalidTransitionException("Cannot reconnect to this type of src node");
                }
                final Guard grd = (Guard) connx.getTransition(connID);
                if (grd != null) {
                    ((GraphNode) getNode(srcID)).addTransition(grd);
                    connx.deleteTranstion(grd.getUIIdentifier());
                    this.connectionIndex.put(connID, (GraphNode) getNode(srcID));
                }
            } else if (type.equalsIgnoreCase(XMLStateMachine.TRIGGER_LABEL) || type.equalsIgnoreCase(XMLStateMachine.TRIGGERSTART_LABEL)) {
                type = getNode(srcID).getType();
                if (!(type.equalsIgnoreCase(XMLStateMachine.TRIGGER_LABEL) || type.equalsIgnoreCase(XMLStateMachine.TRIGGERSTART_LABEL))) {
                    throw new InvalidTransitionException("Cannot reconnect to this type of src node");
                }
                final Message msg = (Message) connx.getTransition(connID);
                if (msg != null) {
                    ((GraphNode) getNode(srcID)).addTransition(msg);
                    connx.deleteTranstion(msg.getUIIdentifier());
                    this.connectionIndex.put(connID, (GraphNode) getNode(srcID));
                }
            } else {
                throw new InvalidTransitionException("Cannot reconnect to this type of src node");
            }
        }

   }

   /**
    * Empty the data model. Method is typically used when the graph GUI needs
    * to be reset i.e. new graph, open graph, etc.
    */
   public final void clearData() {
       this.archElements.clear();
       this.connectionIndex.clear();
       this.graphElements.clear();
   }

}
