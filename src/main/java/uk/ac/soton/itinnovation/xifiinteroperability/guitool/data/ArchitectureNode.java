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

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.tables.InterfaceData;
import java.util.ArrayList;
import java.util.List;

/**
 * An architecture node is the data representation of the two graph elements:
 1) Interface: a rest interface described by its url
 2) A client: a rest user described by its address location.
 *
 * @see AbstractGraphElement
 * @author pjg
 */
public class ArchitectureNode extends AbstractGraphElement {

    /**
     * The address of the interface or client - typically the IP address in string.
     */
    private transient String address;

    /**
     * Getter for the distributed system node address.
     * @return The endpoint address id.
     */
    public final String getAddress() {
        return address;
    }

    /**
     * The id of the graph label of this component node
     */
    private transient final String nodeLabelID;

    /**
     * Getter for the id of the node graph label
     * @return the nodeLabelID of the node
     */
    public final String getNodeLabelID(){
        return nodeLabelID;
    }

    /**
     * The list of data parameters attached to the node. These are the
     * descriptive params adding additional information e.g. urls, ...
     */
    private final transient List<InterfaceData> data = new ArrayList();

    /**
     * Getter for the data parameters.
     * @return The list of data name, value pairs.
     */
    public final List<InterfaceData> getData() {
        return data;
    }

    /**
     * Constructor for the node about the system element.
     * @param idnty The label identifying the system node.
     * @param label The component label identifier, same as the GUI label for the component
     * @param type The type of node, client or interface.
     * @param nodeLabelID id of the node graph label
     */
    public ArchitectureNode(final String idnty, final String label, final String type, final String nodeLabelID) {
        super(idnty, label, type);
        this.address = "null";
        this.nodeLabelID = nodeLabelID;
    }

    /**
     * Add new input data about a REST Interface.
     * @param idnty The data identifier of the interface e.g. "itf1"
     * @param url The fully qualified REST url as a string.
     * @param protocol The different protocol type
     */
    public final void addInterfaceData(final String idnty, final String url, final String protocol) {
        data.add(new InterfaceData(idnty, url, protocol));
    }

    public final void removeInterfaceData(final String restID){
        InterfaceData toRemove = null;
        for(InterfaceData interfaceData: this.data){
            if (interfaceData.getRestID().equalsIgnoreCase(restID)){
                toRemove = interfaceData;
                break;
            }
        }

        if (toRemove != null){
            data.remove(toRemove);
        }
    }

    /**
     * Change the data information about the ID and the address.
     * @param idnty The ID of the interface or client
     * @param addr The address of the interface or client
     */
    public final void setData(final String idnty, final String addr) {
        this.setLabel(idnty);
        this.address = addr;
    }

    /**
     * Generate the xml representation of this system node to form part of the
     * pattern. It will be a component tag.
     * @return The xml specification of the xml component tag.
     */
    @Override
    public final String generateTransitionXML() {
        final StringBuilder strBuilder = new StringBuilder();
        // Add the ident
        strBuilder.append("\n\t\t<component>");
        strBuilder.append("\n\t\t\t<id>").append(this.getLabel()).append("</id>");
        strBuilder.append("\n\t\t\t<address>").append(this.address).append("</address>");

        // Add the interfaces

        for (InterfaceData dfield : this.data) {
            strBuilder.append("\n\t\t\t\t<interface>");
            strBuilder.append("\n\t\t\t\t\t<id>").append(dfield.getRestID()).append("</id>");
            strBuilder.append("\n\t\t\t\t\t<url>").append(dfield.getRestURL()).append("</url>");
            strBuilder.append("\n\t\t\t\t\t<protocol>").append(dfield.getProtocol()).append("</protocol>");
            strBuilder.append("\n\t\t\t\t</interface>");
        }

        strBuilder.append("\n\t\t</component>");
        return strBuilder.toString();
    }

    /**
     * Get the component type - in this case it is a component as part of the
     * system graph.
     * @return The component label
     */
    @Override
    public final String getType() {
        return "component";
    }
}
