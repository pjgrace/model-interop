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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.PDFGenerator;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.OpenFromWebAction;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;

/**
 * This class holds all the actions related to generation and verification of certificates
 * @author ns17
 */
public class CertificateActions {
    
    /**
     * Utility class, therefore use a private constructor.
     */
    private CertificateActions() {
        // empty implementation
    }
    
    /**
     * an action to open a certification model
     */
    public static class CertificateOpenAction extends OpenFromWebAction {
        
        public CertificateOpenAction(BasicGraphEditor editor){
            super(editor, true);
        }
        
    }
    
    /**
     * an action to request a certificate
     */
    public static class CertificateRequestAction extends AbstractAction {
        
        /**
         * reference to the editor
         */
        private final BasicGraphEditor editor;
        
        /**
         * constructor for this action, sets the editor reference
         * 
         * @param editor the editor reference
         */
        public CertificateRequestAction(BasicGraphEditor editor){
            this.editor = editor;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
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
                testReport.put("report", report.getTextTrace());
                testReport.put("model", editor.getCertificationManager().getExecutedModel());
                testReport.put("modelUrl", editor.getCertificationManager().getLastURL());
                String jsonTestReport = new ObjectMapper().writeValueAsString(testReport);
                byte[] testReportBytes = jsonTestReport.getBytes(StandardCharsets.UTF_8);
                int testReportLength = testReportBytes.length;
                
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
                
                StringBuilder responseBuilder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                }
                br.close();
                
                Map<String, Object> jsonResponseMap = new ObjectMapper().readValue(responseBuilder.toString(), HashMap.class);
                
                String authID = (String) jsonResponseMap.get("authenticationID");
                String date = (String) jsonResponseMap.get("date");
                String response = (String) jsonResponseMap.get("certificate");
                
                if (response.equalsIgnoreCase("Altered")) {
                    JOptionPane.showMessageDialog(editor,
                            "The originally loaded model for certification has been altered.\n"
                            + "Either reload the certification model or remove your changes.",
                            "Altered model", JOptionPane.ERROR_MESSAGE);
                } 
                else if (response.equalsIgnoreCase("Error")) {
                    JOptionPane.showMessageDialog(editor,
                            "Couldn't generate a certificate, because an unexpected error occured.",
                            "Error while generating certificate", JOptionPane.ERROR_MESSAGE);
                } 
                else if (response.equalsIgnoreCase("Failure")) {
                    JOptionPane.showMessageDialog(editor,
                            "The test's last state is not considered to be a successful end state. A certificate cannot be generated.",
                            "Requesting a certificate", JOptionPane.PLAIN_MESSAGE);
                }
                else if (response.equalsIgnoreCase("Success")) {
                    int choice = JOptionPane.showConfirmDialog(editor, "Successfully generated a certificate. Do you want to save it?",
                            "Saving certificate", JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION){
                        return;
                    }
                    JFileChooser fileChooser = new JFileChooser(System.getProperty("dir"));
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int fileChoice = fileChooser.showOpenDialog(null);

                    if (fileChoice != JFileChooser.APPROVE_OPTION) {
                        return;
                    }

                    File file = fileChooser.getSelectedFile();

                    File certificateFile = new File(Paths.get(file.getPath(), "certificate.pdf").toString());

                    if (certificateFile.exists()) {
                        int confirmation = JOptionPane.showConfirmDialog(editor, "There is already a file named 'certificate.pdf' in this directory."
                                + "Are you sure you want to continue?", "Overriding existing file", JOptionPane.YES_NO_OPTION);
                        if (confirmation != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }

                    PDFGenerator.generate(certificateFile, report.getTextTrace(), authID, date, editor);
                }
            } 
            catch (IOException ex) {}
        }
        
    }
    
    /**
     * action to verify that a pdf certificate was generated by the Fiesta server
     */
    public static class VerifyCertificateAction extends AbstractAction {
        
        /**
         * reference to the editor
         */
        private final BasicGraphEditor editor;
        
        /**
         * constructor for this action, sets the editor reference
         * 
         * @param editor the editor reference
         */
        public VerifyCertificateAction(BasicGraphEditor editor){
            this.editor = editor;
        }
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("dir"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.pdf", "pdf");
            fileChooser.setFileFilter(filter);
            
            int fileChoice = fileChooser.showOpenDialog(null);

            if (fileChoice != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = fileChooser.getSelectedFile();
            try {
                // read the content of the pdf certificate
                StringBuilder fullContent = new StringBuilder();
                PdfReader reader = new PdfReader(file.getAbsolutePath());
                for(int i=1; i <= reader.getNumberOfPages(); i++){
                    fullContent.append(PdfTextExtractor.getTextFromPage(reader, i));
                }
                reader.close();
                
                // extract the verification key from the full content
                String verificationKey;
                String certificateContent;
                int index = fullContent.toString().indexOf(PDFGenerator.verificationKeyLabel);
                if (index < 0){
                    JOptionPane.showMessageDialog(editor, "No verification key found in this certificate. "
                            + "Therefore, certificate is not a valid Fiesta certificate.",
                        "Reading error", JOptionPane.ERROR_MESSAGE);
                }
                
                certificateContent = fullContent.toString().substring(0, index);
                verificationKey = fullContent.toString().substring(index).replaceAll(PDFGenerator.verificationKeyLabel, "").trim();
                
                Map<String, String> dataToVerify = new HashMap<>();
                dataToVerify.put("verificationKey", verificationKey);
                dataToVerify.put("certificateContent", certificateContent);
                String jsonDataToVerify = new ObjectMapper().writeValueAsString(dataToVerify);
                byte[] dataToVerifyBytes = jsonDataToVerify.getBytes();
                
                // send a request to server to verify the certificate content and the verification key
                URL url = new URL("http://localhost:8081/interop/models/certificates/verify"); // FIXME localhost url is currently used for testing purposes
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(dataToVerifyBytes.length));
                
                try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                    dos.write(dataToVerifyBytes);
                }
                
                StringBuilder responseBuilder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                }
                br.close();
                
                boolean verified = Boolean.parseBoolean(responseBuilder.toString());
                
                if (verified){
                    JOptionPane.showMessageDialog(editor, "The PDF certificate is authenticated as a valid certificate generated by Fiesta.",
                            "Succussful certificate verification", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(editor, "The PDF certificate is authenticated as an invalid certificate NOT generated by Fiesta.",
                            "Unsuccussful certificate verification", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException ioe){
                JOptionPane.showMessageDialog(editor, "Something went wrong while reading your PDF certificate.",
                        "Reading error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
    }
}
