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
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONPathGenerator.JSONPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XPathGenerator.XPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.ExitAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.GraphAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.OpenAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.SaveAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.XMLAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.ImportAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.NewAction;
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

        JMenuItem menuItem = new JMenuItem(mxResources.get("new"), new ImageIcon(BasicGraphEditor.class.getResource("/images/new16.png")));
        menuItem.addActionListener(new NewAction(editor));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(mxResources.get("openFile"), new ImageIcon(BasicGraphEditor.class.getResource("/images/open16.png")));
        menuItem.addActionListener(new OpenAction(editor));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(mxResources.get("save"), new ImageIcon(BasicGraphEditor.class.getResource("/images/save16.png")));
        menuItem.addActionListener(new SaveAction(editor, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        menu.add(menuItem);
        menu.add(editor.bind(mxResources.get("saveAs"), new SaveAction(editor, true), "/images/saveas16.png"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("import"), new ImportAction(editor), "/images/import16.png"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("exit"), new ExitAction(editor)));

        // Creates the edit menu
        menu = add(new JMenu(mxResources.get("edit")));

        menuItem = new JMenuItem(mxResources.get("cut"), new ImageIcon(BasicGraphEditor.class.getResource("/images/cut16.png")));
        menuItem.addActionListener(new SaveAction(editor, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(mxResources.get("copy"), new ImageIcon(BasicGraphEditor.class.getResource("/images/copy16.png")));
        menuItem.addActionListener(new SaveAction(editor, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(mxResources.get("paste"), new ImageIcon(BasicGraphEditor.class.getResource("/images/paste16.png")));
        menuItem.addActionListener(new SaveAction(editor, false));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(mxResources.get("undo"), new ImageIcon(BasicGraphEditor.class.getResource("/images/undo16.png")));
        menuItem.addActionListener(new HistoryAction(true, editor));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(mxResources.get("redo"), new ImageIcon(BasicGraphEditor.class.getResource("/images/redo16.png")));
        menuItem.addActionListener(new HistoryAction(false, editor));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(mxResources.get("delete"), new ImageIcon(BasicGraphEditor.class.getResource("/images/bin16.png")));
        menuItem.addActionListener(new HistoryAction(false, editor));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menu.add(menuItem);

         // Creates the view menu
        menu = add(new JMenu(mxResources.get("view")));

        menu.add(editor.bind("Model", new GraphAction(editor), "/images/graph16.png"));
        menu.add(editor.bind(mxResources.get("XML"), new XMLAction(editor), "/images/xml16.png"));
        menu.addSeparator();
        menu.add(editor.bind("Test Reports", new EditorActions.ReportsAction(editor), "/images/report16.png"));


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

