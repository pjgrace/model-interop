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
import java.util.List;

/**
 * Description of a node in the state machine: start, end, normal, trigger, or
 * trigger start. Label information and the set of conncections to other nodes
 * in the state machine.
 *
 * @author pjg
 */
public class GraphNode extends AbstractGraphElement {

    /**
     * The list of transitions from this node to other nodes.
     */
    private final transient List<AbstractGraphElement> transitions = new ArrayList();

    /**
     * IF the node is a Start node - parameter data can be attached. These
     * act as global constants only.
     */
    private final transient List<ConstantData> data = new ArrayList();

    /**
     * Get the list of constant data elements attached to the node (these
     * are only attached to start node).
     * @return The list of constant data fields.
     */
    public final List<ConstantData> getConstantData() {
        return data;
    }

    /**
     * Create a new graph node element.
     * @param idty The UI identity of the node
     * @param type The type of the node (state type)
     */

    public GraphNode(final String idty, final String type) {
        super(idty, type);
    }

    /**
     * Add new constant data to the node. This is the constant name and value
     * as strings
     * @param consName The constant name
     * @param consValue The constant value
     */
    public final void addConstantData(final String consName, final String consValue) {
        data.add(new ConstantData(consName, consValue));
    }

    /**
     * Add a new transition when two nodes have been connected in the graph of
     * the state machine.
     * @param idty The id of the graph node to connect the transition to
     */
    public final void addTransition(final AbstractGraphElement idty) {
        this.transitions.add(idty);
    }

    /**
     * Read a transition value.
     * @param idty The id of the connection to read
     * @return The graph element data structure describing the transition
     */
    public final AbstractGraphElement getTransition(final String idty) {
        for (AbstractGraphElement e : this.transitions) {
            if (e.getUIIdentifier().equalsIgnoreCase(idty)) {
                return e;
            }
        }
        return null;
    }

    /**
     * When a connection is removed from the graph; remove the transition data
     * from the graph data model.
     * @param idty  The transition id to delete
     */
    public final void deleteTranstion(final String idty) {
        AbstractGraphElement toDelete = null;
        for (AbstractGraphElement e : this.transitions) {
            if (e.getUIIdentifier().equalsIgnoreCase(idty)) {
                toDelete = e;
                break;
            }
        }
        this.transitions.remove(toDelete);
    }

    /**
     * Get the number of transitions from this node.
     * @return Integer count of transitions from this node.
     */
    public final int getNumberTransitions() {
        return this.transitions.size();
    }

    /**
     * Get the indexed transition e.g. first, second.
     * @return transition data object.
     */
    public final AbstractGraphElement getTransition(int index) {
        return this.transitions.get(index);
    }


    @Override
    public final String generateTransitionXML() {
        final StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("\n\t\t<state>\n\t\t\t<label>").append(this.getLabel()).append("</label>");
        sBuilder.append("\n\t\t\t<type>").append(getType()).append("</type>");

        for (AbstractGraphElement t : transitions) {
            sBuilder.append(t.generateTransitionXML());
        }
        sBuilder.append("\n\t\t</state>");
        return sBuilder.toString();
    }

    /**
     * Create the pattern xml about the constant data for the graph.
     * @return The XML string to be attached to the pattern data xml tag.
     */
    public final String generatePatternDataXML() {
        final StringBuilder sBuilder = new StringBuilder();
        if (this.data.size() > 0) {
            sBuilder.append("\n\t<patterndata>");
            for (ConstantData t : this.data) {
                sBuilder.append(t.generateTransitionXML());
            }
            sBuilder.append("\n\t</patterndata>");
        }
        return sBuilder.toString();
    }


}
