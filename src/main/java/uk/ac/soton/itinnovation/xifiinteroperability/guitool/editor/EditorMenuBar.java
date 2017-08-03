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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor;

import com.mxgraph.util.mxResources;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONPathGenerator.JSONPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XPathGenerator.XPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.Delete;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.ExitAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.GraphAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.NewAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.OpenAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.SaveAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.XMLAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.ImportAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.PopUpMenuActions.HistoryAction;

/**
 * Drop down Menu bar at the top of the GUI. Has a set of drop down menus which
 * are described here.
 * @author pjg
 */
public class EditorMenuBar extends JMenuBar {

    /**
     * Create the drop down menus in the editor.
     * @param editor The editor context.
     */
    public EditorMenuBar(final BasicGraphEditor editor) {
        super();
        JMenu menu = add(new JMenu(mxResources.get("file")));

        menu.add(editor.bind(mxResources.get("new"), new NewAction(editor), "/images/new.gif"));
        menu.add(editor.bind(mxResources.get("openFile"), new OpenAction(editor), "/images/open.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("save"), new SaveAction(editor, false), "/images/save.gif"));
        menu.add(editor.bind(mxResources.get("saveAs"), new SaveAction(editor, true), "/images/saveas.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("import"), new ImportAction(editor), "/images/import-icon.png"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("exit"), new ExitAction(editor)));

        // Creates the edit menu
        menu = add(new JMenu(mxResources.get("edit")));

        menu.add(editor.bind(mxResources.get("undo"), new HistoryAction(true, editor), "/images/undo.gif"));
        menu.add(editor.bind(mxResources.get("redo"), new HistoryAction(false, editor), "/images/redo.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("delete"), new Delete(editor), "/images/cut.gif"));

        // creates the tools menu
        menu = add(new JMenu("Tools"));
        
        JMenuItem xPathGeneratorItem = new JMenuItem("XPath Expression Generator");
        xPathGeneratorItem.addActionListener((ActionEvent ae) -> {
            final JFileChooser fChooser = new JFileChooser(System.getProperty("user.dir"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files (.xml)", "xml");
            fChooser.setFileFilter(filter);
            fChooser.setAcceptAllFileFilterUsed(false);
            
            final int check = fChooser.showDialog(editor, "Choose xml file");
            
            if (check == JFileChooser.APPROVE_OPTION) {
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(fChooser.getSelectedFile()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null){
                        sb.append(line);
                        line = br.readLine();
                    }
                    br.close();
                    
                    new XPathGeneratorEditor().initGUI(sb.toString(), false, null);
                }
                catch (IOException ex){
                    JOptionPane.showMessageDialog(editor, "Something went wrong, while reading your xml file.", "File error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JMenuItem jsonPathGeneratorItem = new JMenuItem("JSONPath Expression Generator");
        jsonPathGeneratorItem.addActionListener((ActionEvent ae) -> {
            final JFileChooser fChooser = new JFileChooser(System.getProperty("user.dir"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files (.json)", "json");
            fChooser.setFileFilter(filter);
            fChooser.setAcceptAllFileFilterUsed(false);
            
            final int check = fChooser.showDialog(editor, "Choose json file");
            
            if (check == JFileChooser.APPROVE_OPTION) {
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(fChooser.getSelectedFile()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = br.readLine();
                    }
                    br.close();
                    
                    new JSONPathGeneratorEditor().initGUI(sb.toString(), false, null);
                }
                catch (IOException ex){
                    JOptionPane.showMessageDialog(editor, "Something went wrong, while reading your json file.", "File error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        menu.add(xPathGeneratorItem);
        menu.add(jsonPathGeneratorItem);
        
        // Creates the view menu
        menu = add(new JMenu(mxResources.get("view")));

        menu.add(editor.bind(mxResources.get("Graph"), new GraphAction(editor)));
        menu.add(editor.bind(mxResources.get("XML"), new XMLAction(editor)));

        // Creates the diagram menu
        menu = add(new JMenu(mxResources.get("layout")));

        menu.add(editor.graphLayout("verticalHierarchical", true));
        menu.add(editor.graphLayout("horizontalHierarchical", true));

        menu.addSeparator();

        menu.add(editor.graphLayout("verticalStack", false));
        menu.add(editor.graphLayout("horizontalStack", false));

        menu.addSeparator();

        menu.add(editor.graphLayout("verticalTree", true));
        menu.add(editor.graphLayout("horizontalTree", true));

        menu.addSeparator();

        menu.add(editor.graphLayout("organicLayout", true));
        menu.add(editor.graphLayout("circleLayout", true));

        // Creates the window menu
        menu = add(new JMenu(mxResources.get("window")));

        final UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();

        for (UIManager.LookAndFeelInfo laf : lafs) {
            final String clazz = laf.getClassName();
            menu.add(new AbstractAction(laf.getName()) {
                @Override
                public void actionPerformed(final ActionEvent event) {
                    editor.setLookAndFeel(clazz);
                }
            });
        }

        // Creates the help menu
        menu = add(new JMenu(mxResources.get("help")));

        final JMenuItem item = menu.add(new JMenuItem(mxResources.get("aboutGraphEditor")));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                    editor.about();
            }
        });     
    }
};

