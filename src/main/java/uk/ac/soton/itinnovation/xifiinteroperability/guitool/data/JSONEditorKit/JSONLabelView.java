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
// Created By : Nikolay Stanchev - ns17@it-innovation.soton.ac.uk
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONEditorKit;

import javax.swing.text.Element;
import javax.swing.text.LabelView;

/**
 * A child class of the LabelView, represents key and value labels
 * 
 * @author ns17
 */
public class JSONLabelView extends LabelView {
    
    /**
     * Constructor for JSONLabelView 
     * No overriding of methods or addition of new ones
     * The class is used to separate key and value text in the JSON from other text
     * that uses the LabelView.
     *
     * @param elem the element associated with this view
     */
    public JSONLabelView(Element elem){
        super(elem);
    }
    
}
