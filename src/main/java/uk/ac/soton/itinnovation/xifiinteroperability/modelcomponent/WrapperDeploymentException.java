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

package uk.ac.soton.itinnovation.xifiinteroperability.modelcomponent;

/**
 * Exception when there is an error during the deployment of a wrapper; or
 * when there is an exception during the event-capture redirect.
 *
 * @author pjg
 */
public class WrapperDeploymentException extends Exception {

    /**
     * Error during the deployment of the proxy.
     * @param exceptionMessage The error message to relay.
     * @param excep The exception context.
     */
    public WrapperDeploymentException(final String exceptionMessage, final Exception excep) {
        super(exceptionMessage, excep);
    }
}
