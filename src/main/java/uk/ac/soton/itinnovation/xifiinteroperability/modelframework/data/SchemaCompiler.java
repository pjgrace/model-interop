package uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data;

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
//	Created By :			Paul Grace
//	Created for Project :		XIFI (http://www.fi-xifi.eu)
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

//package uk.ac.soton.itinnovation.xifiinteroperability.PatternEngine.Data;
//
//
//import java.io.File;
//import java.io.IOException;
//import java.io.StringReader;
//import java.lang.reflect.Field;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import junit.framework.TestSuite;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;
//import uk.ac.soton.itinnovation.xifiinteroperability.Utils.FileUtils;
//
///**
// * Execute the JAXB xjc compiler programmatically rather than at the
// * command line. Hence it can support higher level user tools e.g. GUIs
// * 
// * @author pjg
// */
//public class SchemaCompiler{
//
//    /**
//     * Given a schema definition specified within an xsd file-compile
//     * and generate java classes equivalent to the documented data format.
//     * These class files are copied into the InteropTL generated file
//     * content folder.
//     * 
//     * @param packageName The package name for the generated classes.
//     * @param f The xsd schema file to compile.
//     * @param overwrite The flag to overwrite existing content (true).
//     * @throws IOException 
//     * @throws InterruptedException 
//     */
//    public static void generateClass(String packageName, File f, boolean overwrite) 
//            throws IOException, InterruptedException{
//        
//        /*
//         * Build the compiler command and execute it in a new Java 
//         * process.
//         */
//        String fileName = f.getCanonicalPath();
//        String command = "xjc -p "+packageName+" -d "+ FileUtils.resourceFiles() + " "+fileName; 
//        Process p = Runtime.getRuntime().exec(command, null);
//        p.waitFor();
//        
//        command = "javac "+FileUtils.resourceFiles() + packageName+ FileUtils.separator+"*.java";
//        p = Runtime.getRuntime().exec(command, null);
//        p.waitFor();
//    }
//    
//    /**
//     * Unit test to check that the compilation process correctly generates
//     * java class files from an xsd definition. 
//     */
//    @Test
//    public void testXSDCompilation() {
//        String pkg_name = "test";
//        File testFile = new File(FileUtils.resourceFiles()+"Data.xsd");
//        try{
//            generateClass("test", testFile, true);
//        }
//        catch(IOException | InterruptedException e){
//            System.err.println("Compilation error: "+e.getMessage());
//        }
//        
//        Object testObject = null;
//        try {
//            JAXBContext jc = JAXBContext.newInstance(pkg_name);
//            Unmarshaller u = jc.createUnmarshaller();
//            String input = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+
//                    "<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
//                    " xsi:noNamespaceSchemaLocation=\"data.xsd\">"+ 
//                    "<name>IT Innovation</name><year>2013</year></data>";
//            testObject= (Object)u.unmarshal( new StringReader( input ));
//        } catch (JAXBException ex) {
//           System.err.println("Jaxb unmarshall error: "+ex.getMessage());
//        }
//        assertNotNull(testObject);
//        try {
//            Field[] fields = testObject.getClass().getDeclaredFields();
//            for(Field f: fields){
//                f.setAccessible(true);
//            }
//            
//            assertTrue(fields[0].get(testObject).equals("IT Innovation"));
//            assertTrue(fields[1].getInt(testObject)==2013);
//        } catch ( SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//            Logger.getLogger(SchemaCompiler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    public static TestSuite suite() {
//        return new TestSuite(SchemaCompiler.class);
//    }
//    
//    public static void main(String args[]) {
//        new SchemaCompiler().testXSDCompilation();
////        junit.textui.TestRunner.run(suite());
//    }
//}
