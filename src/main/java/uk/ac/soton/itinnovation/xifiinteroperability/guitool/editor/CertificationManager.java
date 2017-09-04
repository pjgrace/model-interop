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

/**
 * a Manager class to handle the certification services
 * 
 * @author ns17
 */
public class CertificationManager {
    
    /**
     * holds the URL of the last loaded test
     */
    private String lastURL;
    
    /**
     * getter for the URL of the last loaded test
     * @return the lastURL attribute of the manager
     */
    public String getLastURL(){
        return lastURL;
    }
    
    /**
     * setter for the URL of the last loaded test,
     * this method is to be used only when someone opens a model from the Certification menu
     * @param url the new URL of the last loaded test
     */
    public void setLastURL(String url){
        this.lastURL = url;
    }
    
    /**
     * this method is to be used when a new model is loaded or created from somewhere
     * different than the Certification menu, the last URL is set to null
     */
    public void resetURL(){
        this.lastURL = null;
        this.executed = false;
    }
    
    /**
     * boolean to represent if the test has been executed after it was loaded
     */
    private boolean executed;
    
    /**
     * a getter for the executed attribute of the certification manager
     * @return true if the test was executed after it has been loaded and false otherwise
     */
    public boolean getExecuted(){
        return executed;
    }
    
    /**
     * a setter method for the executed attribute of the certification manager
     * @param executed True if the test was executed after it has been loaded and false otherwise
     */
    public void setExecuted(boolean executed){
        this.executed = executed;
    }
    
    /**
     * a constructor for the certification manager
     */
    public CertificationManager(){
        // empty constructor, nothing to initialise
    }
    
}
