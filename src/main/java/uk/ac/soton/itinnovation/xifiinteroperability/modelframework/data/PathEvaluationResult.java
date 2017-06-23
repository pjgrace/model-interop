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

package uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data;

/**
 * This class is used when evaluating XPaths or JSONPaths to encapsulate both the 
 * boolean result and the path expression value in a single class
 * 
 * @author ns17
 */
public class PathEvaluationResult {
    
    /** 
     * The boolean result of the evaluation. 
     */
    final private boolean result;
    
    /** 
     * The value returned from parsing the XPath or JSONPath expression 
     */
    final private Object exprValue;
    
     /**
     * Construct an invalid JSONPath exception with a given string message.
     * @param result The boolean result of the evaluation.
     * @param exprValue The value returned from parsing the XPath or JSONPath expression 
     */
    public PathEvaluationResult(boolean result, Object exprValue){
        this.result = result;
        this.exprValue = exprValue;
    }
    
    /**
     * Get the result of the evaluation.
     * @return The boolean result.
     */
    public boolean getResult(){
        return this.result;
    }
    
    /**
     * Get the value return from parsing the XPath or JSONPath expression
     * @return The Object exprValue
     */
    public Object getValue(){
        return this.exprValue;
    }
}
