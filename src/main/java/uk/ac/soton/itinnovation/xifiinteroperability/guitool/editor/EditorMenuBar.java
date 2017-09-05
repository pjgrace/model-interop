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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mxgraph.util.mxResources;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.JSONPathGenerator.JSONPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.XPathGenerator.XPathGeneratorEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.ExitAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.GraphAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.OpenAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.SaveAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.XMLAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.ImportAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.NewAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.PopUpMenuActions.HistoryAction;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;

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
        
        menuItem = new JMenuItem(editor.bind("Open Template Model", new FileActions.OpenTemplateAction(editor), "/images/open16.png"));
        menu.add(menuItem);
        
        menuItem = new JMenuItem(editor.bind("Open Model From Web", new FileActions.OpenFromWebAction(editor), "/images/open16.png"));
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
        
        menuItem = new JMenuItem(editor.bind("Cut", new DefaultEditorKit.CutAction(), "/images/cut16.png"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editor.bind("Copy", new DefaultEditorKit.CopyAction(), "/images/copy16.png"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editor.bind("Paste", new DefaultEditorKit.PasteAction(), "/images/paste16.png"));
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
        menuItem.addActionListener(new EditorActions.Delete(editor));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menu.add(menuItem);

         // Creates the view menu
        menu = add(new JMenu(mxResources.get("view")));

        menu.add(editor.bind("Model", new GraphAction(editor), "/images/graph16.png"));
        menu.add(editor.bind(mxResources.get("XML"), new XMLAction(editor), "/images/xml16.png"));
        menuItem = new JMenuItem("Current Test");
        menuItem.addActionListener(new EditorActions.TestViewAction(editor));
        menu.add(menuItem);
        menu.addSeparator();
        menu.add(editor.bind("Test Reports", new EditorActions.ReportsAction(editor), "/images/report16.png"));

        // creates the run menu
        menu = add(new JMenu("Run"));
        
        menu.add(editor.bind("Run test", new EditorActions.ExecuteAction(editor), "/images/run16.png"));
        
        JMenuItem stopButton = new JMenuItem("Stop test");
        stopButton.addActionListener((ActionEvent ae) -> {
            EditorToolBar toolBar = (EditorToolBar) ((BorderLayout) editor.getLayout()).getLayoutComponent(BorderLayout.NORTH);
            JButton stop = (JButton) toolBar.getComponentAtIndex(toolBar.getStopButtonIndex());
            stop.doClick();
        });
        stopButton.setIcon(new ImageIcon(BasicGraphEditor.class.getResource("/images/stop16.png")));
        menu.add(stopButton);
        
        JMenuItem nextButton = new JMenuItem("Next step");
        nextButton.addActionListener((ActionEvent e) -> {
            EditorToolBar toolBar = (EditorToolBar) ((BorderLayout) editor.getLayout()).getLayoutComponent(BorderLayout.NORTH);
            JButton next = (JButton) toolBar.getComponentAtIndex(toolBar.getNextButtonIndex());
            next.doClick();
        });
        nextButton.setIcon(new ImageIcon(BasicGraphEditor.class.getResource("/images/step16.png")));
        menu.add(nextButton);
        
        // creates the certification menu
        menu = add(new JMenu("Certification"));
        JMenuItem openCertificationModelItem = new JMenuItem("Open certification model");
        // TODO the OpenFromWebAction might have to be adjusted to ask for username or API KEY to handle authentication for certification
        openCertificationModelItem.addActionListener(new FileActions.OpenFromWebAction(editor, true));
        menu.add(openCertificationModelItem);
        
        JMenuItem certifyItem = new JMenuItem("Request a certificate");
        certifyItem.addActionListener((ActionEvent e) -> {
            if (editor.getCertificationManager().getLastURL() == null){
                JOptionPane.showMessageDialog(editor, 
                        "In order to request a certificate open a repository model from the certification menu.",
                        "Certification error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!editor.getCertificationManager().getExecuted()){
                JOptionPane.showMessageDialog(editor,
                        "In order to request a certificate you must execute the loaded test first. Click on the 'Run' icon in the menu toolbar.",
                        "Certification error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                InteroperabilityReport report = editor.getCodePanel().getTestingPanel().getInteroperabilityReport();
                Map<String, String> testReport = new HashMap<>();
                testReport.put("success", report.getSuccess());
                testReport.put("report", report.getReport());
                testReport.put("model", editor.getCertificationManager().getExecutedModel());
                testReport.put("modelUrl", editor.getCertificationManager().getLastURL());
                String jsonTestReport = new ObjectMapper().writeValueAsString(testReport);
                byte[] testReportBytes = jsonTestReport.getBytes(StandardCharsets.UTF_8);
                int testReportLength = jsonTestReport.length();
                
                String urlLink = editor.getCertificationManager().getLastURL();
                int index = urlLink.length();
                String id = "";
                while (!urlLink.substring(index-1, index).equals("/")){
                    id = urlLink.substring(index-1, index) + id;
                    index -= 1;
                }
                // URL url = new URL(urlLink + "/certify"); // this implementation is to be used when the API is updated on the actual server
                URL url = new URL("http://localhost:8081/interop/models/" + id + "/certify"); // localhost url is currently used for testing purposes
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(testReportLength));
                
                try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                    dos.write(testReportBytes);
                }
                
                StringBuilder response = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                if (response.toString().equalsIgnoreCase("Altered")) {
                    JOptionPane.showMessageDialog(editor,
                            "The originally loaded model for certification has been altered.\n"
                            + "Either reload the certification model or remove your changes.",
                            "Altered model", JOptionPane.ERROR_MESSAGE);
                } 
                else if (response.toString().equalsIgnoreCase("Error")) {
                    JOptionPane.showMessageDialog(editor,
                            "Couldn't generate a certificate, because an unexpected error occured.",
                            "Error while generating certificate", JOptionPane.ERROR_MESSAGE);
                } 
                else if (response.toString().equalsIgnoreCase("Failure")) {
                    JOptionPane.showMessageDialog(editor,
                            "The test's last state is not considered to be a successful end state. A certificate cannot be generated.",
                            "Requesting a certificate", JOptionPane.PLAIN_MESSAGE);
                }
                else if (response.toString().equalsIgnoreCase("Success")) {
                    int choice = JOptionPane.showConfirmDialog(editor, "Successfully generated a certificate. Do you want to save it?",
                            "Saving certificate", JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION){
                        return;
                    }
                    try{
                        JFileChooser fileChooser = new JFileChooser(System.getProperty("dir"));
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int fileChoice = fileChooser.showOpenDialog(null);
                        
                        if (fileChoice != JFileChooser.APPROVE_OPTION){
                            return;
                        }
                        
                        File file = fileChooser.getSelectedFile();
                        
                        File certificateFile = new File(Paths.get(file.getPath(), "certificate.pdf").toString());
                        
                        if (certificateFile.exists()){
                            int confirmation = JOptionPane.showConfirmDialog(editor, "There is already a file named 'certificate.pdf' in this directory."
                                    + "Are you sure you want to continue?", "Overriding existing file", JOptionPane.YES_NO_OPTION);
                            if (confirmation != JOptionPane.YES_OPTION){
                                return;
                            }
                        }
                        
                        Document document = new Document();
                        PdfWriter.getInstance(document, new FileOutputStream(certificateFile));
                        document.open();
                        
                        Font font = FontFactory.getFont(FontFactory.COURIER, 19, BaseColor.DARK_GRAY);
                        Paragraph heading = new Paragraph("Certificate", font);
                        heading.setAlignment(Element.ALIGN_CENTER);
                        
                        font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);
                        Paragraph reportParagraph = new Paragraph(report.getTextTrace(), font);
                        
                        document.add(heading);
                        document.add(new Paragraph(" "));
                        
                        document.add(reportParagraph);
                        document.close();
                        
                        JOptionPane.showMessageDialog(editor,
                            "Successfully saved your certificate in " + file.getPath() + ".",
                            "Saving certificate", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (DocumentException ex){
                        JOptionPane.showMessageDialog(editor,
                                "Something went wrong while generating your certificate.",
                                "Certificate generation error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (IOException ex) {}
        });
        menu.add(certifyItem);
        
        // creates the tools menu
        menu = add(new JMenu("Tools"));

        JMenuItem xPathGeneratorItem = new JMenuItem("XPath Expression Generator");
        xPathGeneratorItem.addActionListener((ActionEvent ae) -> {

            int choice = (int) JOptionPane.showConfirmDialog(editor, "Do you want to load an existing XML file?",
                    "Load XML file", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION){
                return;
            }
            else if (choice == JOptionPane.NO_OPTION){
                new XPathGeneratorEditor().initGUI(false, null);
                return;
            }

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

            int choice = (int) JOptionPane.showConfirmDialog(editor, "Do you want to load an existing JSON file?",
                    "Load JSON file", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
            else if (choice == JOptionPane.NO_OPTION) {
                new JSONPathGeneratorEditor().initGUI(false, null);
                return;
            }

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
        item.addActionListener((final ActionEvent event) -> {
            editor.about();
        });
    }
};
