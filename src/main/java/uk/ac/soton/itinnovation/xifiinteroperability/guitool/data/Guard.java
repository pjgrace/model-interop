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

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.Function.FunctionType;
import java.util.ArrayList;
import java.util.List;

/**
 * A guard is the data structure that describes a transition between nodes
 * in the state machine; and it contains checks and rules for testing
 * conditions of behaviour.
 *
 * @author pjg
 */
public class Guard extends AbstractGraphElement {

    /**
     * The set of guards attached to the transition.
     */
    private final transient List<GuardData> data = new ArrayList();

    /**
     * The target id of this guard.
     */
    private transient String targetLabel;

    /**
     * Getter for the set of guard of data.
     * @return The data list.
     */
    public final List<GuardData> getData() {
        return data;
    }

    /**
     * Create a new guard data structure between two points on the graph.
     * @param ident The id of the connection/transition.
     * @param type The connection type.
     * @param toNode  The target node id (in the connection).
     */
    public Guard(final String ident, final String type, final AbstractGraphElement toNode) {
        super(ident, type);
        this.targetLabel = toNode.getLabel();
    }

    /**
     * Add a new guard data description to this transition.
     * @param function The function of the gaurd
     * @param grdlabel The parameter name
     * @param reqVal The required parameter value
     */
    public final void addGuard(final FunctionType function, final String grdlabel, final String reqVal) {
        data.add(new GuardData(function, grdlabel, reqVal));
    }

    /**
     * Update the target id of the connection.
     * @param newTarget The label id of the
     */
    public final void setTarget(final String newTarget) {
        this.targetLabel = newTarget;
    }

    /**
     * Get the target label id of the connection.
     * @return The id label.
     */
    public final String getTarget() {
        return this.targetLabel;
    }

    /**
     * Generate the xml documentaion for this guard set. Essentially the
     * transition specification of the state transition.
     * @return The xml content as a string.
     */
    @Override
    public final String generateTransitionXML() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\n\t\t\t<transition>");
        strBuilder.append("\n\t\t\t\t<to>").append(this.targetLabel).append("</to>");
        if (this.data.size() > 0) {
            strBuilder.append("\n\t\t\t\t<guards>");
            for (GuardData e : this.data) {
                strBuilder.append(e.generateTransitionXML());
            }
            strBuilder.append("\n\t\t\t\t</guards>");
        }
        strBuilder.append("\n\t\t\t</transition>");
        return strBuilder.toString();
    }

}
