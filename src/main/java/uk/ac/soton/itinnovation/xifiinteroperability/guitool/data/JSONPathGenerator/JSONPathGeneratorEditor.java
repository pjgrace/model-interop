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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONPathGenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONEditorKit.JSONEditorKit;

/**
 * An editor, which loads a json file and generates JSONPath on click on elements
 * 
 * @author ns17
 */
public class JSONPathGeneratorEditor extends JDialog { 
    
    public JSONPathGeneratorEditor(){
        super();
    }
    
    public void initGUI(String json) {
        this.setTitle("JSONPath Generator");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JEditorPane editorPane = new JEditorPane("text/json", json);
        final JScrollPane areaScrollPane = new JScrollPane(editorPane);
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(850, 850));
        this.add(areaScrollPane, BorderLayout.CENTER);
        editorPane.setEditable(false);
        editorPane.setBorder(new CompoundBorder(new LineBorder(Color.GRAY),
                new EmptyBorder(1, 3, 1, 1)));
        editorPane.setEditorKit(new JSONEditorKit());
        int caret = editorPane.getCaretPosition();
        editorPane.setText(json);
        editorPane.setCaretPosition(caret);
        
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
