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

package uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

/**
 * Each architecture is made up of REST components. These exchange REST
 * messages using HTTP operations (typically).
 *
 * @author pjg
 */
public class RESTComponent {

    // Static constants related to the XML tag types in the pattern spec:

    /**
     * ID tag in the XML specification.
     */
    public static final String IDTAG = "id";

    /**
     * ADDRESS tag in the XML specification.
     */
    public static final String ADDRESSTAG = "address";

    /**
     * INTERFACE tag in the xml specification.
     */
    public static final String INTERFACETAG = "interface";

    /**
     * The unique identifier of the component within the specification. This is
     * not a globally unique ID used outside the tool e.g. a GUID.
     */
    private final transient String componentID;

    /**
     * Getter for the componentID.
     * @return The value of this architecture component's ID in the pattern.
     */
    public final String getComponentID() {
        return componentID;
    }

    /**
     * The public ip address of the component, such that it can be recongnised
     * and communicated with.
     *
     * - currently the tool recognises IPv4 addresses. However, the String type
     * will allow migration to both v4 and v6 addresses.
     */
    private final transient String ipAddress;

    /**
     * Getter for the ip address of this component.
     * @return The value of this architecture component's IP in the pattern.
     */
    public final String getipAddress() {
        return ipAddress;
    }

    /**
     * The list of 0 or more interface provided by the component e.g. the
     * URL of the REST interface where GET, POST, etc. operations are targetted.
     */
    private final transient List<RESTInterface> interfaces;

    /**
     * Getter for the list of 0 or more interface provided by the component e.g. the
     * URL of the REST interface where GET, POST, etc. operations are targeted.
     * @return The interfaces as a simple list.
     */
    public final List<RESTInterface> getInterfaces() {
        return interfaces;
    }

    /**
     * Construct a new component instance using the DOM <component> tag content.
     *
     * @param eltIndex The DOM element of the component.
     * @param capture The event capture interface.
     * @throws InvalidArchitectureException Error indicating error in specifying
     * .new architecture component.
     */
    public RESTComponent(final Element eltIndex, final EventCapture capture)
        throws InvalidArchitectureException {

        this.interfaces = new ArrayList();

        // Get the state label
        componentID = eltIndex.getChildText(IDTAG);
        if (componentID == null) {
            throw new InvalidArchitectureException("Component ID canot be null");
        }
        ipAddress = eltIndex.getChildText(ADDRESSTAG);
        if (ipAddress == null) {
            throw new InvalidArchitectureException("Component IP address cannot be null");
        }

        final List<Element> xmlStates = eltIndex.getChildren(INTERFACETAG);
        for (Element eltIntfIndex : xmlStates) {
            try {
                addInterface(new RESTInterface(eltIntfIndex, capture));
            } catch (InvalidInterfaceException ex) {
                throw new InvalidArchitectureException("Invalid Interface on Component", ex);
            }
        }
    }

    /**
     * Add an interface to this component.
     * @param newInterface The interface to add to the component's list.
     */
    private void addInterface(final RESTInterface newInterface) {
        interfaces.add(newInterface);
    }
}
