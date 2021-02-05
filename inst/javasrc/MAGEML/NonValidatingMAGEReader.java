package MAGEML;


/*
 * Adapted from MAGEReader.java to allow parsing MAGEML where the DTD is not available
 *
 * Created on 12. mai 2002, 15:45
 */


import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

import org.biomage.Common.MAGEJava;
import org.biomage.tools.helpers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.biomage.tools.xmlutils.*;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.helpers.XMLReaderFactory;



/**
 *
 * @author  Kjell Petersen + little contribution of Steffen Durinck
 */
  public class NonValidatingMAGEReader {

    protected MAGEJava mageObj;

    /** Creates a new instance of MAGEReader */
     public NonValidatingMAGEReader(String filename) {

        // Set debug level
        StringOutputHelpers.setVerbose(0);

        try {
            // Create the parser

            org.apache.xerces.jaxp.SAXParserFactoryImpl factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();

            // Create the content handler.
            org.biomage.tools.xmlutils.MAGEContentHandler cHandler =
                new org.biomage.tools.xmlutils.MAGEContentHandler();

            // Parse the file(s)
            File fileHandle = new File(filename);

            if (fileHandle.exists()) {
                if (fileHandle.isFile()) {
                    parser.parse(fileHandle,cHandler);
                } else if (fileHandle.isDirectory()){
                    cHandler.setLastDocument(false);

                    // get File handles for all mage-ml files in the directory
                    // NB: All xml files are assumed to be MAGE-ML...
                    File[] magemlFiles = fileHandle.listFiles(
                    new java.io.FilenameFilter() {
                        public boolean accept(File file, String name) {
                            if (name.matches(".+.xml"))
                                return true;
                            else
                                return false;
                        }
                    }
                    );

                    for (int i=0; i<magemlFiles.length-1; i++) {
                        parser.parse(magemlFiles[i].getAbsolutePath(), cHandler);
                    }

                    cHandler.setLastDocument(true);
                    parser.parse(magemlFiles[magemlFiles.length-1].getAbsolutePath(), cHandler);

                }

                // Here is the object.
                mageObj = cHandler.getMAGEJava();

            } else { // if (fileHandle.exists())

                mageObj = null;

            }

            // Output test :
            // testing1();
            // testing2();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws java.text.ParseException {

        // Some date tests
        // Date releaseDate1 = new Date("12/02/2001");
        // Date releaseDate2 = new java.text.SimpleDateFormat("yyyy-MM-dd").parse("2001-02-12");

        System.out.println(args[0]);

        MAGEReader mr = new MAGEReader(args[0]);

    }


    public MAGEJava getMAGEobj() {
        return mageObj;
    }


    private void testing1() throws Exception {

        // TESTING:  This simply tries to write back out the
        //           raw XML of the MAGE object.  This should
        //           compare favorably to what we read in

        FileWriter tmpFile = new FileWriter("./raw.xml");
        mageObj.writeMAGEML(tmpFile);
        tmpFile.flush();
        tmpFile.close();

        // Now lets use an XML OutputFormat to write it to the
        // screen in a pretty format.
        OutputFormat xmlFormat = new OutputFormat("xml","utf-8",true);
        xmlFormat.setLineWidth(60);
        xmlFormat.setLineSeparator("\n");

        XMLSerializer xmlWriterDocHndler = new XMLSerializer(System.out,xmlFormat);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser xmlWriterParser = factory.newSAXParser();

        xmlWriterParser.getXMLReader().setContentHandler(xmlWriterDocHndler);
        xmlWriterParser.getXMLReader().parse(new InputSource(new FileReader("./raw.xml")));

    }


    private void testing2() throws Exception {

        // TESTING:  This simply tries to write out empty XML from an object
        //           derived from a MAGE class.


        class Audit2 extends org.biomage.AuditAndSecurity.Audit {
            Audit2() {
                super();
            }
        }

        Audit2 audit = new Audit2();

        FileWriter tmpFile = new FileWriter("./raw.xml");
        audit.writeMAGEML(tmpFile);

        tmpFile.flush();
        tmpFile.close();

        // Now lets use an XML OutputFormat to write it to the
        // screen in a pretty format.
        OutputFormat xmlFormat = new OutputFormat("xml","utf-8",true);
        xmlFormat.setLineWidth(60);
        xmlFormat.setLineSeparator("\n");

        XMLSerializer xmlWriterDocHndler = new XMLSerializer(System.out,xmlFormat);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser xmlWriterParser = factory.newSAXParser();

        xmlWriterParser.getXMLReader().setContentHandler(xmlWriterDocHndler);
        xmlWriterParser.getXMLReader().parse(new InputSource(new FileReader("./raw.xml")));

    }

}



