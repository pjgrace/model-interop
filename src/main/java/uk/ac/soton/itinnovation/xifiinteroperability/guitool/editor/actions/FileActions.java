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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.DefaultFileFilter;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InvalidPatternException;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.GraphGenerator;

/**
 * Modification of mxGraph Action operations to open, close, new and save
 * files.
 *
 * @author pjg
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
                FileFilter selectedFilter = null;
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
                editor.getDataModel().clearData();
                editor.getUndoManager().clear();
                editor.updateTableView(null);

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
            final mxGraph graph = editor.getBehaviourGraph().getGraph();
            // Check modified flag and display save dialog
            editor.getCodePanel().getTestingPanel().clearTestingPanel();

            final mxCell root = new mxCell();
            root.insert(new mxCell());
            graph.getModel().setRoot(root);

            final mxGraph agraph = editor.getSystemGraph().getGraph();
            final mxCell root2 = new mxCell();
            root2.insert(new mxCell());
            agraph.getModel().setRoot(root2);
            
            editor.setModified(false);
            editor.getUndoManager().clear();
            editor.getDataModel().clearData();
            editor.updateTableView(null);
            editor.getCodePanel().getReportsPanel().clearTabbedPane();
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
         * Create a new open action.
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
