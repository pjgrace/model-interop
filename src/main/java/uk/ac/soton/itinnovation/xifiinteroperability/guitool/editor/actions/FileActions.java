/*
Copyright (c) 2001-2014, JGraph Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the JGraph nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL JGRAPH BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2017
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
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.jsoup.Jsoup;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ArchitectureNode;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.DataModel;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.DefaultFileFilter;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InvalidPatternException;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.GraphGenerator;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.specification.XMLStateMachine;

/**
 * Modification of mxGraph Action operations to open, close, new and save
 * files.
 *
 * Project acknowledgements - developed in FIESTA (http://www.fiesta-iot.eu)
 * & XIFI (http://www.fi-xifi.eu)
 *
 * @author Paul Grace
 */
public class FileActions {

    /**
     * Save the current graph specification to file. Takes the xml specification
     * and writes this to a user selected file location.
     *
     */
    public static class SaveAction extends AbstractAction {

        /**
         * Remember the directory location for saving files to.
         */
        private transient String lastDir = null;

        /**
         * a boolean, which represents if the saveAs button was clicked
         */
        private final transient boolean saveAsClicked;

        /**
         * Editor context - the editor where we are saving files.
         */
        private transient BasicGraphEditor editor;

        /**
         * Saving XML files - constant for the file type.
         */
        private static final String XMLFILE = ".xml";

        /**
         * Create a new concrete action for saving files.
         * @param edtr The editor context information.
         * @param saveAsClicked a boolean to show if Save As button was clicked
         * or just the Save button
         */
        public SaveAction(final BasicGraphEditor edtr, boolean saveAsClicked) {
            super();
            this.editor = edtr;
            this.saveAsClicked = saveAsClicked;
        }

        /**
         * When the action is selected via the UI perform the file save.
         * @param actEvent The UI event e.g. button click.
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            if (editor == null) {
                editor = EditorActions.getEditor(actEvent);
            }

            if (editor != null) {
                FileFilter selectedFilter;
                final DefaultFileFilter xmlPngFilter = new DefaultFileFilter(XMLFILE,
                                "XML " + mxResources.get("file") + " (" + XMLFILE + ")");

                final StringBuffer filename = new StringBuffer();

                if (editor.getCurrentFile() == null || saveAsClicked) {
                    java.io.FileWriter fWriter = null;
                    try {
                        String wDir;
                        if (lastDir != null) {
                            wDir = lastDir;
                        } else if (editor.getCurrentFile() != null) {
                            wDir = editor.getCurrentFile().getParent();
                        } else {
                            wDir = System.getProperty("user.dir");
                        }

                        final JFileChooser fChooser = new JFileChooser(wDir);
                        // Adds the default file format
                        final FileFilter defaultFilter = xmlPngFilter;
                        fChooser.addChoosableFileFilter(defaultFilter);
                        // Adds special vector graphics formats and HTML
                        fChooser.addChoosableFileFilter(new DefaultFileFilter(".xml",
                                "mxGraph Editor " + mxResources.get("file")
                                        + " (.xml)"));
                        // Adds a filter for each supported image format
                        Object[] imageFormats = ImageIO.getReaderFormatNames();
                        // Finds all distinct extensions
                        final HashSet<String> formats = new HashSet();

                        for (Object imageFormat : imageFormats) {
                            final String ext = imageFormat.toString().toLowerCase(Locale.ENGLISH);
                            formats.add(ext);
                        }
                        imageFormats = formats.toArray();
                        for (Object imageFormat : imageFormats) {
                            final String ext = imageFormat.toString();
                            fChooser.addChoosableFileFilter(new DefaultFileFilter("."
                                    + ext, ext.toUpperCase(Locale.ENGLISH) + " "
                                            + mxResources.get("file") + " (." + ext + ")"));
                        }
                        // Adds filter that accepts all supported image formats
                        fChooser.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
                                            mxResources.get("allImages")));
                        fChooser.setFileFilter(defaultFilter);
                        final int rcheck = fChooser.showDialog(null, mxResources.get("save"));

                        if (rcheck != JFileChooser.APPROVE_OPTION) {
                            return;
                        } else {
                            lastDir = fChooser.getSelectedFile().getParent();
                        }
                        filename.append(fChooser.getSelectedFile().getAbsolutePath());
                        selectedFilter = fChooser.getFileFilter();
                        if (selectedFilter instanceof DefaultFileFilter) {
                            final String ext = ((DefaultFileFilter) selectedFilter)
                                    .getExtension();

                            if (!filename.toString().toLowerCase(Locale.ENGLISH).endsWith(ext)) {
                                filename.append(ext);
                            }
                        }
                        if (new File(filename.toString()).exists()
                                && JOptionPane.showConfirmDialog(editor.getBehaviourGraph(),
                                        mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                            return;
                        }
                        final String xml = editor.getDataModel().getGraphXML();
                        fWriter = new java.io.FileWriter(filename.toString());
                        try {
                            editor.setModified(false);
                            editor.setCurrentFile(new File(filename.toString()));

                            fWriter.write(xml);
                        } finally {
                            fWriter.close();
                        }
                    } catch (IOException ex) {
                        ServiceLogger.LOG.error("Error writing specification to file", ex);
                    } finally {
                        try {
                            if (fWriter != null) {
                                fWriter.close();
                            }
                        } catch (IOException ex) {
                            ServiceLogger.LOG.error("Error closing file stream", ex);
                        }
                    }
                }
                else {
                    filename.append(editor.getCurrentFile().getAbsolutePath());
                    final String xml = editor.getDataModel().getGraphXML();
                    java.io.FileWriter fWrite = null;
                    try {
                        fWrite = new java.io.FileWriter(filename.toString());
                        try {
                            editor.setModified(false);
                            editor.setCurrentFile(new File(filename.toString()));

                            fWrite.write(xml);
                        } finally {
                            fWrite.close();
                        }
                    } catch (IOException ex) {
                        ServiceLogger.LOG.error("Error saving file", ex);
                    } finally {
                        try {
                            if (fWrite != null) {
                                fWrite.close();
                            }
                        } catch (IOException ex) {
                            ServiceLogger.LOG.error("Error saving file", ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a new graph. Essentially clear all the data models and views
     * from the UI.
     */
    public static class NewAction extends AbstractAction {
        /**
         * The editor context.
         */
        private transient BasicGraphEditor editor;

        /**
         * Create a new new action.
         * @param edtr The editor context.
         */
        public NewAction(final BasicGraphEditor edtr) {
            super();
            this.editor = edtr;
        }

        /**
         * Perform the new action on a UI Event.
         * @param actEvent The UI event - e.g. button pressed.
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            if (editor == null) {
                editor = EditorActions.getEditor(actEvent);
            }
            if (editor == null) {
                return;
            }

            if (!editor.isModified() || JOptionPane.showConfirmDialog(editor,
                        mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {

                /**
                 * Clear the data model.
                 */
                editor.resetEditor();

                /**
                 * Clear the graph views.
                 */
                final mxCell root = new mxCell();
                root.insert(new mxCell());

                final mxCell root2 = new mxCell();
                root2.insert(new mxCell());

                final mxGraph graph = editor.getSystemGraph().getGraph();
                graph.getModel().setRoot(root);
                final mxGraph graph2 = editor.getBehaviourGraph().getGraph();
                graph2.getModel().setRoot(root2);

                editor.setModified(false);
                editor.setCurrentFile(null);

                editor.getSystemGraph().zoomAndCenter();
                editor.getBehaviourGraph().zoomAndCenter();

                /**
                 * Clear the xml specification page.
                 */
                editor.getCodePanel().getXMLPanel().displayXMLSpecification();
                /**
                 * Clear the testing page.
                 */
                editor.getCodePanel().getTestingPanel().clearTestingPanel();
                /**
                 * Clear the previous reports panel
                 */
                editor.getCodePanel().getReportsPanel().clearTabbedPane();
            }
        }
    }

    /**
     * Action to open up a view of specification from a file.
     */

    public static class OpenAction extends AbstractAction {
        /**
         * The last used directory for dialogue.
         */
        private transient String lastDir;

        /**
         * The editor context of this action.
         */
        private transient BasicGraphEditor editor;

        /**
         * Create a new open action.
         * @param edtr The UI context of this operation.
         */
        public OpenAction(final BasicGraphEditor edtr) {
            super();
            this.editor = edtr;
        }

        /**
         * Clear the editor information of data and history.
         */
        protected final void resetEditor() {
            // Check modified flag and display save dialog
            editor.getCodePanel().getTestingPanel().clearTestingPanel();

            final mxGraph graph = editor.getBehaviourGraph().getGraph();
            final mxCell root = new mxCell();
            root.insert(new mxCell());
            graph.getModel().setRoot(root);

            final mxGraph agraph = editor.getSystemGraph().getGraph();
            final mxCell root2 = new mxCell();
            root2.insert(new mxCell());
            agraph.getModel().setRoot(root2);

            editor.setModified(false);
            editor.resetEditor();
        }

        /**
        * Reads XML file in xml format and update the tool data model and views.
        * @param file The file to open.
        * @throws java.io.IOException Error in the opening or reading of file.
         */
        protected final void openXmlPng(final File file)
                        throws IOException {
            try {
                final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                final Document doc = dBuilder.parse(file);
                final GraphGenerator gGenerate = new GraphGenerator(editor);

                editor.setCurrentFile(file);
                resetEditor();
                gGenerate.createGraph(doc);
                editor.getXmlUndoManager().add(editor.getDataModel().getState());

                final mxHierarchicalLayout layout = new mxHierarchicalLayout(editor.getBehaviourGraph().getGraph());
                layout.execute(editor.getBehaviourGraph().getGraph().getDefaultParent());
                editor.getCodePanel().getXMLPanel().displayXMLSpecification();
                editor.setRules();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading file: Invalid Pattern specification", "Pattern error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException ex) {
                JOptionPane.showMessageDialog(editor, "Error Parsing the xml document", "Pattern error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SAXException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading xml content: Invalid Pattern specification", "Pattern error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (InvalidPatternException ex) {
                JOptionPane.showMessageDialog(editor, "Error in pattern data: Invalid Pattern specification", "Pattern error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Perform open file on UI event action.
         * @param actEvent The UI event.
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            if (editor == null) {
                editor = EditorActions.getEditor(actEvent);
                if (editor == null) {
                    return;
                }
            }

            if (!editor.isModified() || JOptionPane.showConfirmDialog(editor,
                    mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {

                final String wDir = (lastDir != null) ? lastDir : System
                                .getProperty("user.dir");

                final JFileChooser fChoose = new JFileChooser(wDir+"//src//main//resources//examples");

                // Adds file filter for supported file format
                final DefaultFileFilter defaultFilter = new DefaultFileFilter(
                    ".xml", mxResources.get("allSupportedFormats")
                                    + " (.xml)") {

                    @Override
                    public boolean accept(final File file) {
                            String lcase = file.getName().toLowerCase(Locale.ENGLISH);

                            return super.accept(file)
                                            || lcase.endsWith(".xml");
                    }
                };
                fChoose.addChoosableFileFilter(defaultFilter);

                fChoose.addChoosableFileFilter(new DefaultFileFilter(".xml",
                                "mxGraph Editor " + mxResources.get("file")
                                                + " (.xml)"));

                fChoose.setFileFilter(defaultFilter);

                final int rChck = fChoose.showDialog(null,
                                mxResources.get("openFile"));

                if (rChck == JFileChooser.APPROVE_OPTION) {
                        lastDir = fChoose.getSelectedFile().getParent();

                    try {
                        if (fChoose.getSelectedFile().getAbsolutePath()
                                .toLowerCase().endsWith(".xml")) {
                            openXmlPng(fChoose.getSelectedFile());
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                                editor.getBehaviourGraph(),
                                ex.toString(),
                                mxResources.get("error"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    /**
     * Action to open a template model - API test model or Interoperability test
     * model
     */
    public static class OpenTemplateAction extends AbstractAction {

        /**
         * a reference to the editor
         */
        private BasicGraphEditor editor;

        /**
         * a constructor for the OpenTemlate action
         *
         * @param editor
         */
        public OpenTemplateAction(final BasicGraphEditor editor) {
            this.editor = editor;
        }

        /**
         * generates the data model for a simple API test template
         * @param protocol http or coap
         * @return
         */
        private DataModel generateAPItemplate(String protocol){
            // Create the API template model
            DataModel dataModel = new DataModel();
            dataModel.addNode("1", XMLStateMachine.TRIGGERSTART_LABEL, XMLStateMachine.TRIGGERSTART_LABEL);
            dataModel.addNode("2", XMLStateMachine.NORMAL_LABEL, XMLStateMachine.NORMAL_LABEL);
            dataModel.addNode("3", XMLStateMachine.END_LABEL, XMLStateMachine.END_LABEL);
            dataModel.addNode("4", XMLStateMachine.INTERFACE_LABEL, XMLStateMachine.INTERFACE_LABEL);
            ArchitectureNode archNode = (ArchitectureNode) dataModel.getComponentByLabel(XMLStateMachine.INTERFACE_LABEL);
            archNode.addInterfaceData("rest", protocol.toLowerCase() + "://127.0.0.1:8000/", protocol.toLowerCase());
            dataModel.addConnection("5", "1", "2");
            dataModel.addConnection("6", "2", "3");

            return dataModel;
        }

        /**
         * generates the data model for a simple interoperability test template
         */
        private DataModel generateInteropTemplate(String protocol){
            // Create the interoperability template model
            DataModel dataModel = new DataModel();
            dataModel.addNode("1", XMLStateMachine.START_LABEL, XMLStateMachine.START_LABEL);
            dataModel.addNode("2", XMLStateMachine.NORMAL_LABEL, XMLStateMachine.NORMAL_LABEL);
            dataModel.addNode("3", XMLStateMachine.END_LABEL, XMLStateMachine.END_LABEL);
            dataModel.addNode("4", XMLStateMachine.INTERFACE_LABEL, XMLStateMachine.INTERFACE_LABEL);
            dataModel.addNode("7", DataModel.CLIENT, DataModel.CLIENT);
            ArchitectureNode archNode = (ArchitectureNode) dataModel.getComponentByLabel(XMLStateMachine.INTERFACE_LABEL);
            archNode.addInterfaceData("rest", protocol.toLowerCase() + "://127.0.0.1:8000/", protocol.toLowerCase());
            dataModel.addConnection("5", "1", "2");
            dataModel.addConnection("6", "2", "3");

            return dataModel;
        }

        /**
         * Clear the editor information of data and history.
         */
        private void resetEditor() {
            editor.getCodePanel().getTestingPanel().clearTestingPanel();

            final mxGraph graph = editor.getBehaviourGraph().getGraph();
            final mxCell root = new mxCell();
            root.insert(new mxCell());
            graph.getModel().setRoot(root);

            final mxGraph agraph = editor.getSystemGraph().getGraph();
            final mxCell root2 = new mxCell();
            root2.insert(new mxCell());
            agraph.getModel().setRoot(root2);

            editor.setModified(false);
            editor.resetEditor();
        }

        /**
         * a method which loads the string model into the tool
         *
         * @param model
         */
        private void openModel(String model) {
            try {
                final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                final Document doc = dBuilder.parse(new InputSource(new StringReader(model)));
                final GraphGenerator gGenerate = new GraphGenerator(editor);

                editor.setCurrentFile(null);
                resetEditor();
                gGenerate.createGraph(doc);
                editor.getXmlUndoManager().add(editor.getDataModel().getState());

                final mxHierarchicalLayout layout = new mxHierarchicalLayout(editor.getBehaviourGraph().getGraph());
                layout.execute(editor.getBehaviourGraph().getGraph().getDefaultParent());
                editor.getCodePanel().getXMLPanel().displayXMLSpecification();
                editor.setRules();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading file: Invalid Pattern specification", "Pattern error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException ex) {
                JOptionPane.showMessageDialog(editor, "Error Parsing the xml document", "Pattern error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SAXException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading xml content: Invalid Pattern specification", "Pattern error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (InvalidPatternException ex) {
                JOptionPane.showMessageDialog(editor, "Error in pattern data: Invalid Pattern specification", "Pattern error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * The action to be performed
         *
         * @param ae the actual action event
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (editor == null) {
                editor = EditorActions.getEditor(ae);
            }
            if (editor == null) {
                return;
            }

            if (editor.isModified() && JOptionPane.showConfirmDialog(editor,
                    mxResources.get("loseChanges")) != JOptionPane.YES_OPTION) {
                return;
            }

            String[] options = {"HTTP API test template", "COAP API test template", "HTTP Interoperability test template", "COAP Interoperability test template"};
            String choice = (String) JOptionPane.showInputDialog(editor,
                    "Please choose the type of template you want to generate.",
                    "Model from template", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice == null){
                return;
            }

            if (choice.equalsIgnoreCase(options[0])){
                // Create the API template model
                DataModel dataModel = generateAPItemplate("http");

                // generate the model
                openModel(dataModel.getGraphXML());
            }
            else if (choice.equalsIgnoreCase(options[1])){
                // Create the API template model
                DataModel dataModel = generateAPItemplate("coap");

                // generate the model
                openModel(dataModel.getGraphXML());
            }
            else if (choice.equalsIgnoreCase(options[2])){
                // Create the interoperability template model
                DataModel dataModel = generateInteropTemplate("http");

                // generate the model
                openModel(dataModel.getGraphXML());
            }
            else if (choice.equalsIgnoreCase(options[3])){
                // Create the interoperability template model
                DataModel dataModel = generateInteropTemplate("coap");

                // generate the model
                openModel(dataModel.getGraphXML());
            }
        }
    }

    /**
     * Action to open a model from web repositories
     */
    public static class OpenFromWebAction extends AbstractAction {

        /**
         * a reference to the editor
         */
        private BasicGraphEditor editor;

        private boolean certification;

        /**
         * a constructor for the OpenFromWebAction, assumes no certification
         * @param editor the editor reference
         */
        public OpenFromWebAction(final BasicGraphEditor editor){
            this(editor, false);
        }

        /**
         * a constructor for the OpenFromWebAction, which also indicates whether this model is opened for certification
         * @param editor the editor reference
         * @param certification whether this model is opened for certification or not
         */
        public OpenFromWebAction(final BasicGraphEditor editor, boolean certification){
            this.editor = editor;
            this.certification = certification;
        }

        /**
         * Clear the editor information of data and history.
         */
        private void resetEditor() {
            editor.getCodePanel().getTestingPanel().clearTestingPanel();

            final mxGraph graph = editor.getBehaviourGraph().getGraph();
            final mxCell root = new mxCell();
            root.insert(new mxCell());
            graph.getModel().setRoot(root);

            final mxGraph agraph = editor.getSystemGraph().getGraph();
            final mxCell root2 = new mxCell();
            root2.insert(new mxCell());
            agraph.getModel().setRoot(root2);

            editor.setModified(false);
            editor.resetEditor();
        }

        /**
         * a method which loads the string model into the tool
         * @param model
         */
        private void openModel(String model){
            try {
                final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                final Document doc = dBuilder.parse(new InputSource(new StringReader(model)));
                final GraphGenerator gGenerate = new GraphGenerator(editor);

                editor.setCurrentFile(null);
                resetEditor();
                gGenerate.createGraph(doc);
                editor.getXmlUndoManager().add(editor.getDataModel().getState());

                final mxHierarchicalLayout layout = new mxHierarchicalLayout(editor.getBehaviourGraph().getGraph());
                layout.execute(editor.getBehaviourGraph().getGraph().getDefaultParent());
                editor.getCodePanel().getXMLPanel().displayXMLSpecification();
                editor.setRules();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading file: Invalid Pattern specification", "Pattern error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException ex) {
                JOptionPane.showMessageDialog(editor, "Error Parsing the xml document", "Pattern error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SAXException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading xml content: Invalid Pattern specification", "Pattern error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (InvalidPatternException ex) {
                JOptionPane.showMessageDialog(editor, "Error in pattern data: Invalid Pattern specification", "Pattern error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * a method which replaces the **Value** strings in the xml model
         */
        private String replaceValues(String model){
            int startIndex = model.indexOf("**");
            int endIndex;
            while (startIndex != -1) {
                endIndex = model.indexOf("**", startIndex + 2);
                String originalValue = model.substring(startIndex+2, endIndex);
                int horizontalBarIndex = originalValue.indexOf("|");
                String newVal;
                if (horizontalBarIndex >= 1){
                    newVal = (String) JOptionPane.showInputDialog(editor, originalValue,
                            originalValue.substring(0, horizontalBarIndex) + " - input for model", JOptionPane.PLAIN_MESSAGE,
                            null, null, originalValue.substring(0, horizontalBarIndex));
                }
                else {
                    newVal = (String) JOptionPane.showInputDialog(editor, originalValue,
                            "Input value for model", JOptionPane.PLAIN_MESSAGE);
                }
                if (newVal == null){
                    return null;
                }
                model = model.replace(model.substring(startIndex + 2, endIndex), newVal);
                model = model.replaceFirst("\\*\\*", "").replaceFirst("\\*\\*", "");
                startIndex = model.indexOf("**");
            }
            return model;
        }

        /**
         * a method which initialises a JTable with the available models in the repository
         */
        private void initTable(String[] columns, Object[][] data, Map<String, String> modelsIDs){
            JDialog tableDialog = new JDialog();
            tableDialog.setTitle("Available models in the repository");
            tableDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


            // initialising the JTable and overriding the isCellEditable method to not allow editing
            JTable table = new JTable(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };

            // setting the rendered of the description column to a custom one which wraps long text
            table.getColumnModel().getColumn(1).setCellRenderer(new CustomCellRenderer());
            // setting the table to fill the viewport height
            table.setFillsViewportHeight(true);

            // add a listener to the table to react on double clicks over a name of a model
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();
                        int column = target.getSelectedColumn();
                        // check for a non populated row
                        if (row > table.getRowCount()) {
                            return;
                        }

                        // only clicks on the name of the model are allowed
                        if (column != 0) {
                            return;
                        }

                        // extract the model and open it
                        String urlStr = modelsIDs.get((String) table.getValueAt(row, column));
                        StringBuilder response = new StringBuilder();
                        String jsonResponse;
                        try {
                            URL url = new URL(urlStr);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = br.readLine()) != null) {
                                response.append(line);
                            }
                            br.close();

                            jsonResponse = response.toString();
                            Map<String, Object> jsonMap = new ObjectMapper().readValue(jsonResponse, HashMap.class);
                            String model = (String) jsonMap.get("model");
                            String testName = (String) jsonMap.get("name");
                            if (model == null) {
                                return;
                            }

                            // close the dialog and open the model
                            tableDialog.dispose();
                            model = replaceValues(model);
                            if (model != null) {
                                openModel(model);

                                // if opened from the certification tab set the last url and the name of the test in the certification manager
                                if (certification) {
                                    editor.getCertificationManager().setInfo(urlStr, testName);
                                }
                            }
                        } catch (MalformedURLException ex) {
                            JOptionPane.showMessageDialog(editor,
                                    "There is something wrong with the URL of the repository.",
                                    "Invalid URL", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(editor,
                                    "There is something wrong with the repository of the model you supplied.",
                                    "Invalid model repository", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);

            tableDialog.add(scrollPane);
            tableDialog.pack();
            tableDialog.setLocationRelativeTo(null);
            tableDialog.setVisible(true);
        }

        /**
         * The action to be performed
         * @param ae the actual action event
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (editor == null) {
                editor = EditorActions.getEditor(ae);
            }
            if (editor == null) {
                return;
            }

            if (editor.isModified() && JOptionPane.showConfirmDialog(editor,
                    mxResources.get("loseChanges")) != JOptionPane.YES_OPTION) {
                return;
            }

            String urlStr = JOptionPane.showInputDialog(editor,
                    "Please specify the url of the repository with the models.",
                    "Model from web", JOptionPane.PLAIN_MESSAGE);
            if (urlStr == null){
                return;
            }

            /**
             * extract the models from the repository and initialise a table with the name and description
             * of the available models
             */
            StringBuilder response = new StringBuilder();
            String jsonResponse;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                jsonResponse = response.toString();
                Object[] jsonArray = new ObjectMapper().readValue(jsonResponse, Object[].class);
                String[] columns = {"Name", "Description"};
                Object[][] data = new String[jsonArray.length][2];
                Map<String, String> modelsIDs = new HashMap<>();
                int index = 0;
                for(Object model : jsonArray){
                    Map<String, Object> modelMap = (HashMap<String, Object>) model;
                    // using Jsoup to remove all html tags and get just the text description
                    String name = Jsoup.parse((String) modelMap.get("name")).text();
                    data[index][0] = name;
                    data[index][1] = Jsoup.parse((String) modelMap.get("description")).text();
                    if (urlStr.endsWith("/")){
                        modelsIDs.put(name, urlStr + ((String) modelMap.get("id")));
                    }
                    else {
                        modelsIDs.put(name, urlStr + "/" + ((String) modelMap.get("id")));
                    }
                    index += 1;
                }

                initTable(columns, data, modelsIDs);
            }
            catch (MalformedURLException ex){
                JOptionPane.showMessageDialog(editor,
                        "There is something wrong with the URL of the repository.",
                        "Invalid URL", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException ex){
                JOptionPane.showMessageDialog(editor,
                        "There is something wrong with the repository of the model you supplied.",
                        "Invalid model repository", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * a custom cell rendered for the JTable , which uses a JTextArea set to wrap long text
         */
        private class CustomCellRenderer extends JTextArea implements TableCellRenderer {

            /**
             * the constructor for the CustomCellRendered
             */
            private CustomCellRenderer() {
                // adjusts the wrapping settings of the cell
                setLineWrap(true);
                setWrapStyleWord(true);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setText(value.toString());
                setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
                // adjust the height of the table cell to fit the long text
                if (table.getRowHeight(row) != getPreferredSize().height) {
                    table.setRowHeight(row, getPreferredSize().height);
                }
                return this;
            }
        }
    }

     /**
     * Action to import a specification from a file.
     */

    public static class ImportAction extends AbstractAction {
        /**
         * The last used directory for dialogue.
         */
        private transient String lastDir;

        /**
         * The editor context of this action.
         */
        private transient BasicGraphEditor editor;

        /**
         * Create a new import action.
         * @param edtr The UI context of this operation.
         */
        public ImportAction(final BasicGraphEditor edtr) {
            super();
            this.editor = edtr;
        }

        /**
        * Reads XML file in xml format and update the tool data model and views.
        * @param file The file to open.
        * @throws java.io.IOException Error in the opening or reading of file.
         */
        protected final void importXmlPng(final File file)
                        throws IOException {
            try {
                final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                final Document doc = dBuilder.parse(file);
                final GraphGenerator gGenerate = new GraphGenerator(editor);

                gGenerate.importGraph(doc);

                final mxHierarchicalLayout layout = new mxHierarchicalLayout(editor.getBehaviourGraph().getGraph());
                layout.execute(editor.getBehaviourGraph().getGraph().getDefaultParent());
                editor.getCodePanel().getXMLPanel().displayXMLSpecification();
                editor.setRules();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading file: Invalid Pattern specification", "Pattern error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException ex) {
                JOptionPane.showMessageDialog(editor, "Error Parsing the xml document", "Pattern error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SAXException ex) {
                JOptionPane.showMessageDialog(editor, "Error reading xml content: Invalid Pattern specification", "Pattern error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (InvalidPatternException ex) {
                JOptionPane.showMessageDialog(editor, ex.getMessage(), "Pattern error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Perform open file on UI event action.
         * @param actEvent The UI event.
         */
        @Override
        public final void actionPerformed(final ActionEvent actEvent) {
            if (editor == null) {
                editor = EditorActions.getEditor(actEvent);
                if (editor == null) {
                    return;
                }
            }

//            final mxGraph graph = editor.getBehaviourGraph().getGraph();
//            // Check modified flag and display save dialog
//            editor.getCodePanel().getTestingPanel().clearTestingPanel();
//
//            final mxCell root = new mxCell();
//            root.insert(new mxCell());
//            graph.getModel().setRoot(root);
//
//            final mxGraph agraph = editor.getSystemGraph().getGraph();
//            final mxCell root2 = new mxCell();
//            root2.insert(new mxCell());
//            agraph.getModel().setRoot(root2);

            final String wDir = (lastDir != null) ? lastDir : System
                            .getProperty("user.dir");

            final JFileChooser fChoose = new JFileChooser(wDir);

            // Adds file filter for supported file format
            final DefaultFileFilter defaultFilter = new DefaultFileFilter(
                ".xml", mxResources.get("allSupportedFormats")
                                + " (.xml)") {

                    @Override
                    public boolean accept(final File file) {
                            String lcase = file.getName().toLowerCase(Locale.ENGLISH);

                            return super.accept(file)
                                            || lcase.endsWith(".xml");
                    }
                };
            fChoose.addChoosableFileFilter(defaultFilter);

            fChoose.addChoosableFileFilter(new DefaultFileFilter(".xml",
                            "mxGraph Editor " + mxResources.get("file")
                                            + " (.xml)"));

            fChoose.setFileFilter(defaultFilter);

            final int rChck = fChoose.showDialog(null,
                            mxResources.get("openFile"));

            if (rChck == JFileChooser.APPROVE_OPTION) {
                        lastDir = fChoose.getSelectedFile().getParent();

                        try {
                            if (fChoose.getSelectedFile().getAbsolutePath()
                                            .toLowerCase().endsWith(".xml")) {
                                    importXmlPng(fChoose.getSelectedFile());
                            }
                        } catch (IOException ex) {
                                JOptionPane.showMessageDialog(
                                                editor.getBehaviourGraph(),
                                                ex.toString(),
                                                mxResources.get("error"),
                                                JOptionPane.ERROR_MESSAGE);
                        }
            }
        }
    }
}
