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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XPathGenerator;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.AttributeNameView;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.PlainTextView;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.TagNameView;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.TagView;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit.XMLEditorKit;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XPathGenerator.GeneratorXMLDocument.GeneratorLeafElement;

/**
 * An editor kit which extends the XML editor kit and modifies the on click actions
 * in order to be compatible with the XPathGenerator
 * 
 * @author ns17
 */
public class GeneratorXMLEditorKit extends XMLEditorKit {

    /**
     * a constructor for the editor kit, initialises the extended kit
     */
    public GeneratorXMLEditorKit() {
        super(null, false);
    }

    /**
     * Overriding the default document used in the kit, GeneratorXMLDocument is used
     * so that the document is compatible with the XPath Generator
     * 
     * @return a new instance of a GeneratorXMLDocument
     */
    @Override
    public Document createDefaultDocument() {
        return new GeneratorXMLDocument();
    }
    
    /**
     * Overriding the read method to use a GeneratorXMLReader in order to be compatible 
     * with the XPath generator
     * 
     * @param in reference to the reader to get the text from
     * @param doc Document to insert the text into
     * @param pos starting position
     * @throws IOException
     * @throws BadLocationException 
     */
    @Override
    public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
        BufferedReader br=new BufferedReader(in);
        String s=br.readLine();
        StringBuilder buff=new StringBuilder();
        while (s!=null) {
            buff.append(s);
            s=br.readLine();
        }

        int p=getInsertPosition(pos, doc);
        GeneratorXMLReader.getInstance().read(new ByteArrayInputStream(buff.toString().getBytes()), doc, p);
    }
    
    /**
     * Overriding the read method to use a GeneratorXMLReader in order to be compatible
     * with the XPath generator
     * 
     * @param in input stream to get the text from
     * @param doc document to insert the text into
     * @param pos starting position
     * @throws IOException
     * @throws BadLocationException 
     */
    @Override
    public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
        int p=getInsertPosition(pos, doc);
        GeneratorXMLReader.getInstance().read(in, doc, p);
    }
    
    /**
     * modifying the mouse listener, and more precisely the on click method
     */
    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JEditorPane src = (JEditorPane) e.getSource();

            int pos = src.viewToModel(e.getPoint());
            
            // checking for a click over a PlainTextView
            PlainTextView deepestPlainTextView = (PlainTextView) getDeepestView(pos, src, PlainTextView.class);
            if (deepestPlainTextView != null) {
                Shape a = getAllocation(deepestPlainTextView, src);
                if (a != null) {
                    Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                    if (r.contains(e.getPoint())) {
                        Element element = deepestPlainTextView.getElement();
                        JOptionPane.showMessageDialog(src, "XPath: " + XPathGenerator.getXPath(((GeneratorLeafElement) element).getNode()));
                        return;
                    }
                }
            }
            
            // checking for a click over a tag element name
            TagNameView deepestTagNameView = (TagNameView) getDeepestView(pos, src, TagNameView.class);
            if (deepestTagNameView != null) {
                Shape a = getAllocation(deepestTagNameView, src);
                if (a != null) {
                    Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                    if (r.contains(e.getPoint())) {
                        Element element = deepestTagNameView.getElement();
                        if (element instanceof GeneratorLeafElement){
                            JOptionPane.showMessageDialog(src, "XPath: " + XPathGenerator.getXPath(((GeneratorLeafElement) element).getNode()));
                        }
                        return;
                    }
                }
            }
            
            // checking for a click over a tag attribute name
            AttributeNameView deepestAttributeNameView = (AttributeNameView) getDeepestView(pos, src, AttributeNameView.class);
            if (deepestAttributeNameView != null) {
                Shape a = getAllocation(deepestAttributeNameView, src);
                if (a != null) {
                    Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                    if (r.contains(e.getPoint())) {
                        Element element = deepestAttributeNameView.getElement();
                        if (element instanceof GeneratorLeafElement){
                            JOptionPane.showMessageDialog(src, "XPath: " + XPathGenerator.getXPath(((GeneratorLeafElement) element).getNode()));
                        }
                        return;
                    }
                }
            }
            
            // checking for a click over an expanding tag
            TagView deepest = (TagView) getDeepestView(pos, src, TagView.class);
            if (deepest != null && !deepest.isStartTag()) {
                Shape a = getAllocation(deepest, src);
                if (a != null) {
                    Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                    if (!deepest.isSecondTag()) {
                        r.x += TagView.AREA_X_SHIFT;
                    }
                    r.y += TagView.AREA_SHIFT / 4;
                    r.width = TagView.AREA_SHIFT;
                    r.height = TagView.AREA_SHIFT;

                    if (r.contains(e.getPoint())) {
                        deepest.setExpanded(!deepest.isExpanded());

                        GeneratorXMLDocument doc = (GeneratorXMLDocument) src.getDocument();
                        try {
                            pos++;
                            doc.insertString(pos, "\n", new SimpleAttributeSet());
                            doc.remove(pos, 1);
                        } catch (BadLocationException e1) {
                            JOptionPane.showMessageDialog(null,
                                    "Something went wrong while processing your XML file.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    };

    /**
     * overriding the install method, so that it installs the altered mouse listener
     * and remove the old mouse listener
     * @param c the editor pane
     */
    @Override
    public void install(JEditorPane c) {
        super.install(c);
        c.removeMouseListener(lstCollapse);
        c.addMouseListener(mouseListener);
    }

    /**
     * overriding the deinstall method, so that it removes the new mouse listener as well
     * @param c the editor pane
     */
    @Override
    public void deinstall(JEditorPane c) {
        super.deinstall(c);
        c.removeMouseListener(mouseListener);
    }
}
