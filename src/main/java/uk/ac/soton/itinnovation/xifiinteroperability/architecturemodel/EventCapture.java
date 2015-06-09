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

import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.RESTEvent;

/**
 * Interface between the Interoperability cradle (that monitors the applications
 * and services. Passes events and exceptions to the pattern exection engine
 * that tests interoperability.
 *
 * @author pjg
 */
public interface EventCapture {
    /**
     * When a new http message is observed (e.g. a request or reply) at any
     * REST interface in the cradle this notifies this method.
     * @param httpMessage The new RESTEvent generated.
     * @see Interop.PatternEngine.RESTEvent
     */
    void pushEvent(RESTEvent httpMessage);

    /**
     * Exceptions relevant to interoperation need to be passed from the cradle
     * to the pattern engine. These include connection exceptions or problems
     * with message formats (e.g. poorly constructed http headers.
     * @param excep The exception generated. This is of the general type and it
     * is up to the listener to determine the exception type.
     */
    void logException(Exception excep);
}
