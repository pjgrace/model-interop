/*******************************************************************************
 * Created by Stanislav Lapitsky 
 * 
 * Reference to original source code http://java-sl.com/xml_editor_kit.html
 ******************************************************************************/

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
// Modified By : Nikolay Stanchev - ns17@it-innovation.soton.ac.uk
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XMLEditorKit;

import javax.swing.text.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.AbstractGraphElement;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ArchitectureNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ConstantData;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.GraphNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.tables.XMLSpecificationPanel;

public class XMLEditorKit extends StyledEditorKit {
    ViewFactory defaultFactory = new XMLViewFactory();
    
    /**
     * reference to the xmlPanel
     */
    private final XMLSpecificationPanel xmlPanel;
    
    /**
     * a boolean representing the state of the editor - enabled or disabled editing 
     */
    private boolean editingMode;
    
    /**
     * a method to check if editing is allowed
     * @return True if editing is allowed, false otherwise
     */
    public final boolean editingAllowed(){
        return editingMode;
    }
    
    /**
     * a method to toggle between editing modes
     */
    public final void toggleEditingMode(){
        editingMode = !editingMode;
    }
    
    /**
     * a boolean which represents if the XML data has been changed by the user
     */
    private boolean changed;
    
    /**
     * a method to check if the XML data is changed
     * @return True if the pattern has been changed
     */
    public final boolean isChanged(){
        return changed;
    }
    
    /**
     * a method to reset the changed variable
     */
    public final void resetChanged(){
        changed = false;
    }
    
    /**
     * a boolean to represent if changes were updated
     */
    private boolean saved;
    
    /**
     * a method to check if the changes were updated
     * @return True if changes were updated and False otherwise
     */
    public final boolean changesSaved(){
        return saved;
    }
    
    /**
     * setting the saved variable
     */
    public final void resetSaved(){
        this.saved = true;
    }
    
    public XMLEditorKit(XMLSpecificationPanel xmlPanel, boolean editingMode){
        super();
        this.xmlPanel = xmlPanel;
        this.editingMode = editingMode;
        this.changed = false;
        this.saved = true;
    }
    
    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    @Override
    public Document createDefaultDocument() {
        return new XMLDocument();
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

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
        XMLReader.getInstance().read(new ByteArrayInputStream(buff.toString().getBytes()), doc, p);
    }
    
    @Override
    public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
        int p=getInsertPosition(pos, doc);
        XMLReader.getInstance().read(in, doc, p);
    }
    @Override
    public void write(OutputStream out, Document doc, int pos, int len) throws IOException, BadLocationException {
        int[] sel=new int[2];
        sel[0]=pos;
        sel[1]=pos+len;
        correctSelectionBounds(sel, doc);
        pos=sel[0];
        len=sel[1]-pos;
        super.write(out, doc, pos, len);
    }
    @Override
    public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
        int[] sel=new int[2];
        sel[0]=pos;
        sel[1]=pos+len;
        correctSelectionBounds(sel, doc);
        pos=sel[0];
        len=sel[1]-pos;
        super.write(out, doc, pos, len);
    }

    public static void correctSelectionBounds(int[] selection, Document d) {
        if (d instanceof XMLDocument && d.getLength()>0) {
            XMLDocument doc=(XMLDocument)d;
            int start=selection[0];
            Element root=doc.getDefaultRootElement();
            int i=root.getElementIndex(start);
            while (i>=0 && root.getElement(i).getName().equals(XMLDocument.TAG_ELEMENT)) {
                root=root.getElement(i);
                i=root.getElementIndex(start);
            }

            Element startTag=root;

            int end=selection[0];
            root=doc.getDefaultRootElement();
            i=root.getElementIndex(end);
            while (i>=0 && root.getElement(i).getName().equals(XMLDocument.TAG_ELEMENT)) {
                root=root.getElement(i);
                i=root.getElementIndex(end);
            }

            Element endTag=root;
            Element commonParent=startTag;
            while (commonParent!=null &&
                    !(commonParent.getStartOffset()<=endTag.getStartOffset() &&
                     commonParent.getEndOffset()>=endTag.getEndOffset()) ) {
                commonParent=commonParent.getParentElement();
            }

            if (commonParent!=null) {
                selection[0]=commonParent.getStartOffset();
                selection[1]=commonParent.getEndOffset();
            }
        }
    }

    protected int getInsertPosition(int pos, Document d) {
        if (d instanceof XMLDocument && d.getLength()>0) {
            XMLDocument doc=(XMLDocument)d;
            Element root=doc.getDefaultRootElement();
            int i=root.getElementIndex(pos);
            while (i>=0 && root.getElement(i).getName().equals(XMLDocument.TAG_ELEMENT)) {
                root=root.getElement(i);
                i=root.getElementIndex(pos);
            }

            while (root.getElementCount()<3) {
                root=root.getParentElement();
            }
            return root.getElement(0).getEndOffset();
        }

        return pos;
    }

    protected MouseListener lstCollapse=new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JEditorPane src=(JEditorPane)e.getSource();

            int pos=src.viewToModel(e.getPoint());
            
            if (editingAllowed()){
                // checking for plain text view click, only if editing is allowed
                PlainTextView deepestPlainTextView = (PlainTextView) getDeepestView(pos, src, PlainTextView.class);
                if (deepestPlainTextView != null){
                    Shape a = getAllocation(deepestPlainTextView, src);
                    if (a != null){
                        Rectangle r=a instanceof Rectangle ? (Rectangle)a : a.getBounds();
                        if (r.contains(e.getPoint())){
                            int start = deepestPlainTextView.getStartOffset();
                            int end = deepestPlainTextView.getEndOffset();
                            String oldValue = deepestPlainTextView.getText(start, end).toString();
                            String newValue = (String) JOptionPane.showInputDialog(xmlPanel, 
                                    "Please type a value to replace the chosen one", 
                                    "Editting", JOptionPane.PLAIN_MESSAGE, 
                                    null, null, oldValue);
                            
                            if (newValue == null)
                                return;
                            
                            try {
                                if (!validateData(oldValue, newValue, deepestPlainTextView))
                                    return;
                            } catch (BadLocationException ex) {
                                return;                            
                            }
                            
                            try{
                                XMLDocument doc = (XMLDocument) deepestPlainTextView.getDocument();
                                doc.remove(start, end-start);
                                if (doc.getText(start-1, 1).equals("\n")){
                                    doc.insertString(start, newValue + "\n", XMLDocument.PLAIN_ATTRIBUTES);
                                }
                                else {
                                    doc.insertString(start, "\n" + newValue + "\n", XMLDocument.PLAIN_ATTRIBUTES);
                                }
                                changed = true;
                                saved = false;
                            }
                            catch (BadLocationException ex){
                                return;
                            }
                            return;
                        }
                    }
                }
            }
            
            // checking for a click over an expanding tag
            TagView deepest = (TagView) getDeepestView(pos, src, TagView.class);
            if (deepest!=null && !deepest.isStartTag()) {
                Shape a=getAllocation(deepest, src);
                if (a!=null) {
                    Rectangle r=a instanceof Rectangle ? (Rectangle)a : a.getBounds();
                    if (!deepest.isSecondTag()){
                        r.x += TagView.AREA_X_SHIFT;
                    }
                    r.y+=TagView.AREA_SHIFT/4;
                    r.width=TagView.AREA_SHIFT;
                    r.height=TagView.AREA_SHIFT;

                    if (r.contains(e.getPoint())) {
                        deepest.setExpanded(!deepest.isExpanded());

                        XMLDocument doc= (XMLDocument)src.getDocument();
                        try {
                            pos++;
                            doc.insertString(pos, "\n", new SimpleAttributeSet());
                            doc.remove(pos,1);
                        } catch (BadLocationException e1) {
                            JOptionPane.showMessageDialog(xmlPanel, 
                                    "Something went wrong while processing your XML pattern.", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    };

    Cursor oldCursor;
    MouseMotionListener lstMoveCollapse=new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            JEditorPane src=(JEditorPane)e.getSource();
            if (oldCursor==null) {
                oldCursor=src.getCursor();
            }
            
            int pos=src.viewToModel(e.getPoint());
            
            TagView deepest = (TagView) getDeepestView(pos, src, TagView.class);
            if (deepest!=null && !deepest.isStartTag()) {
                Shape a=getAllocation(deepest, src);
                if (a!=null) {
                    Rectangle r=a instanceof Rectangle ? (Rectangle)a : a.getBounds();
                    if (!deepest.isSecondTag()){
                        r.x += TagView.AREA_X_SHIFT;
                    }
                    r.y+=TagView.AREA_SHIFT/4;
                    r.width=TagView.AREA_SHIFT;
                    r.height=TagView.AREA_SHIFT;

                    if (r.contains(e.getPoint())) {
                        src.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
            }

            src.setCursor(oldCursor);
        }
    };

    @Override
    public void install(JEditorPane c) {
        super.install(c);
        c.addMouseListener(lstCollapse);
        c.addMouseMotionListener(lstMoveCollapse);
    }
    
    @Override
    public void deinstall(JEditorPane c) {
        c.removeMouseListener(lstCollapse);
        c.removeMouseMotionListener(lstMoveCollapse);
        super.deinstall(c);
    }

    protected static Shape getAllocation(View v, JEditorPane edit) {
        Insets ins=edit.getInsets();
        View vParent=v.getParent();
        int x=ins.left;
        int y=ins.top;
        while(vParent!=null) {
            int i=vParent.getViewIndex(v.getStartOffset(), Position.Bias.Forward);
            Shape alloc=vParent.getChildAllocation(i, new Rectangle(0,0, Short.MAX_VALUE, Short.MAX_VALUE));
            x+=alloc.getBounds().x;
            y+=alloc.getBounds().y;

            vParent=vParent.getParent();
        }

        if (v instanceof BoxView) {
            int ind=v.getParent().getViewIndex(v.getStartOffset(), Position.Bias.Forward);
            Rectangle r2=v.getParent().getChildAllocation(ind, new Rectangle(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE)).getBounds();

            return new Rectangle(x,y, r2.width, r2.height);
        }

        return new Rectangle(x,y, (int)v.getPreferredSpan(View.X_AXIS), (int)v.getPreferredSpan(View.Y_AXIS));
    }
    
    /**
     * a method to get the deepest view of a given type on click
     * @param pos the position of the click
     * @param src the source
     * @param c the class of the view we are searching for
     * @return 
     */
    public View getDeepestView(int pos, JEditorPane src, Class c){
        try {
            View rootView = src.getUI().getRootView(src);

            while (rootView != null && !c.isInstance(rootView)) {
                int i = rootView.getViewIndex(pos, Position.Bias.Forward);
                rootView = rootView.getView(i);
            }

            View deepestView = (View) c.cast(rootView);
            while (rootView != null && c.isInstance(rootView)) {
                deepestView = (View) c.cast(rootView);
                int i = rootView.getViewIndex(pos, Position.Bias.Forward);
                rootView = rootView.getView(i);
            }

            return deepestView;
        } 
        catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * a method to validate the new value in the view
     * @param value the value replacing the old data
     * @param changedView the view that is being edited
     * @return True if validation is successful and False otherwise
     * @throws BadLocationException 
     */
    private boolean validateData(String oldValue, String value, View changedView) throws BadLocationException{
        if (oldValue.equalsIgnoreCase(value)){
            return true;
        }
        
        View parentTagView = changedView.getParent().getParent();
        String parentContent = parentTagView.getDocument().getText(parentTagView.getStartOffset(), parentTagView.getEndOffset()-parentTagView.getStartOffset());
        
        StringBuilder builder = new StringBuilder();
        int index = 1;
        while (parentContent.charAt(index) != ">".charAt(0)){
            builder.append(parentContent.charAt(index));
            index += 1;
        }
        
        String parentTag = builder.toString();
        
        switch (parentTag){
            // changing label identifier
            case "label":
                if (xmlPanel.getDataModel().graphIdentExist(value)){
                    JOptionPane.showMessageDialog(xmlPanel, 
                            "There already exists a state node with this label.", 
                            "Renaming error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
                
            // changing the target of a transition
            case "to":
                if (!xmlPanel.getDataModel().graphIdentExist(value)){
                    JOptionPane.showMessageDialog(xmlPanel, 
                            "A state node with this label doesn't exist.", 
                            "Transition error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                View stateView = parentTagView.getParent().getParent().getView(1).getView(1);
                String fromLabel = stateView.getDocument().getText(stateView.getStartOffset(), stateView.getEndOffset()-stateView.getStartOffset());
                if (fromLabel.equalsIgnoreCase(value)){
                    JOptionPane.showMessageDialog(xmlPanel, 
                            "You cannot have a transition with the same source and target node.", 
                            "Transition error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                if (xmlPanel.getDataModel().getNodeByLabel(value).getType().equalsIgnoreCase("start") || 
                        xmlPanel.getDataModel().getNodeByLabel(value).getType().equalsIgnoreCase("triggerstart")){
                    JOptionPane.showMessageDialog(xmlPanel, 
                            "You cannot have a transition leading to a start or a trigger start node.", 
                            "Transition error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                break;
                
            case "id":
                View upperView = parentTagView.getParent().getView(0);
                String label = upperView.getDocument().getText(upperView.getStartOffset(), upperView.getEndOffset() - upperView.getStartOffset());
                if (label.substring(1, label.length()-1).equalsIgnoreCase("component")){
                    // component id check
                    if (xmlPanel.getDataModel().archIdentExist(value)){
                        JOptionPane.showMessageDialog(xmlPanel, 
                            "There already exists a component node with this label.", 
                            "Renaming error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                else if (label.substring(1, label.length()-1).equalsIgnoreCase("interface")){
                    // interface id check
                    View componentView = parentTagView.getParent().getParent().getParent().getView(1).getView(1).getView(1);
                    String componentLabel = componentView.getDocument().getText(componentView.getStartOffset(), componentView.getEndOffset() - componentView.getStartOffset());
                    AbstractGraphElement archNode = xmlPanel.getDataModel().getComponentByLabel(componentLabel);
                    if (archNode == null) {
                        return false;
                    }
                    
                    ArchitectureNode node = (ArchitectureNode) archNode;
                    if (!node.getData().stream().noneMatch((data) -> (data.getRestID().equalsIgnoreCase(value)))) {
                        JOptionPane.showMessageDialog(xmlPanel, 
                            "The component already has an interface with this id.", 
                            "Renaming error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                else {
                    // parent of id is either component or interface
                    return false;
                }
                
                break;
                
            
            case "method":
                if (!(value.equalsIgnoreCase("GET") || value.equalsIgnoreCase("POST")
                        || value.equalsIgnoreCase("PUT") || value.equalsIgnoreCase("DELETE"))){
                    JOptionPane.showMessageDialog(xmlPanel,
                            "The method of a message transition can only be one "
                            + "of the following: GET, POST, PUT and DELETE.",
                            "Method error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                break;
            
            case "success":
                if(!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))){
                    JOptionPane.showMessageDialog(xmlPanel, 
                            "The success attribute of an end node can only be true or false.", 
                            "End state error", JOptionPane.ERROR_MESSAGE);
                    
                    return false;
                }
            
                break;
            
            case "protocol":
                // changing of protocol through XML editing is not allowed
                JOptionPane.showMessageDialog(xmlPanel, 
                            "You are not allowed to change the interface protocol by editing the XML pattern.", 
                            "Protocol error", JOptionPane.ERROR_MESSAGE);
                return false;
                
            case "type":
                View upperTypeView = parentTagView.getParent().getView(0);
                String typeLabel = upperTypeView.getDocument().getText(upperTypeView.getStartOffset(), upperTypeView.getEndOffset()-upperTypeView.getStartOffset());
                if (typeLabel.substring(1, typeLabel.length()-1).equalsIgnoreCase("message")){
                    if (!(value.equalsIgnoreCase("XML") || value.equalsIgnoreCase("JSON") 
                            || value.equalsIgnoreCase("OTHER"))) {
                        JOptionPane.showMessageDialog(xmlPanel, 
                            "The type of data can only be one of the following: XML, JSON and OTHER.", 
                            "Data type error", JOptionPane.ERROR_MESSAGE);
                        
                        return false;
                    }
                }
                else if (typeLabel.substring(1, typeLabel.length()-1).equalsIgnoreCase("state")){
                    // changing state type through XML editing is not allowed
                    JOptionPane.showMessageDialog(xmlPanel, 
                            "You are not allowed to change the type of a state node by editing the XML pattern.", 
                            "State type error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                else {
                    // parent of type is either message or state
                    return false;
                }
                
                break;
            
            case "url":
                View upperMessageView = parentTagView.getParent().getView(0);
                String messageLabel = upperMessageView.getDocument().getText(upperMessageView.getStartOffset(), upperMessageView.getEndOffset()-upperMessageView.getStartOffset());
                if (messageLabel.substring(1, messageLabel.length()-1).equalsIgnoreCase("message")){
                    List<String> componentUrls = xmlPanel.getDataModel().getRestUrls();
                    if(!componentUrls.contains(value)){
                        // message url pointers must be existing ones
                        JOptionPane.showMessageDialog(xmlPanel, 
                                "The new url pointer doesn't exist.", 
                                "URL pointer error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                
                break;
                
            case "name":
                GraphNode node = xmlPanel.getDataModel().getStartNode();
                if (node == null){
                    return false;
                }
                else {
                    for(ConstantData data: node.getConstantData()){
                        if (data.getFieldName().equalsIgnoreCase(value)){
                            JOptionPane.showMessageDialog(xmlPanel, 
                                "The new pattern data id is already used.", 
                                "Pattern data error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
                
                break;                
                
            default:
                break;  
        }
        
        return true;
    }
}
