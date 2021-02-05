package MAGEML;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
/*
 * modified MAGEReader.java for use of internal DTD
 *
 * Created on 06. feb 2004, 15:45
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
 * @author  Kjell Petersen + modified by Steffen Durinck with code from Steve Harris
 */

public class MAGEReaderDTDinc {

   protected MAGEJava mageObj;

    /** Creates a new instance of MAGEReader */
    public MAGEReaderDTDinc(String filename) {

        // Set debug level
        StringOutputHelpers.setVerbose(0);

        try {
            // Create the parser

            org.apache.xerces.jaxp.SAXParserFactoryImpl factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
            SAXParser parser = factory.newSAXParser();

// code by Steve Harris to solve MAGE-ML-dtd location problem

            EntityResolver my_resolver = new EntityResolver() {
              public InputSource resolveEntity (String public_id, String system_id)
              {
                if ( system_id != null && system_id.indexOf("MAGE-ML.dtd") != -1 )
                {
                  try
                  {
                    String dtd_path = "MAGE-ML.dtd"; //change this back to MAGE-ML.dtd
                    java.net.URL dtd_url = getClass().getClassLoader().getResource(dtd_path);

                    if ( dtd_url == null ) {
                      System.out.println("Couldn't find dtd from jar file or class path," + " will try to load from " + system_id );
                      return null;
                    }
                    else
                    {
                      return new InputSource(new InputStreamReader(dtd_url.openStream()));
                    }
                  }
                  catch(Exception e)
                  {
                    System.err.println("No dtd found in jar file:");
                    e.printStackTrace();
                    return null;
                  }
                }
                else
                {
                  // use the default behaviour
                  return null;
                }
              }
            };


          parser.getXMLReader().setEntityResolver(my_resolver);

          // Create the content handler.
            org.biomage.tools.xmlutils.MAGEContentHandler cHandler =
                new org.biomage.tools.xmlutils.MAGEContentHandler();

            parser.getXMLReader().setContentHandler(cHandler);

            // Parse the file(s)
            File fileHandle = new File(filename);

            if (fileHandle.exists()) {
                if (fileHandle.isFile()) {
                  parser.getXMLReader().parse(new InputSource(filename));
                    //parser.parse(fileHandle,cHandler);
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
                  //      parser.parse(magemlFiles[i].getAbsolutePath(), cHandler);
                  parser.getXMLReader().parse(new InputSource(magemlFiles[i].getAbsolutePath()));
                    }

                    cHandler.setLastDocument(true);
                    parser.getXMLReader().parse(new InputSource(magemlFiles[magemlFiles.length-1].getAbsolutePath()));
                    //parser.parse(magemlFiles[magemlFiles.length-1].getAbsolutePath(), cHandler);

                }

                // Here is the object.
                mageObj = cHandler.getMAGEJava();
//               Class c = Class.forName("org.biomage.Common.MAGEJava") ;
  //             Object mageObj = (Object) c.newInstance();
    //           mageObj =cHandler.getMAGEJava();

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

  //      Audit2 audit = new Audit2();

       /* FileWriter tmpFile = new FileWriter("./raw.xml");
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
        */
    }

}



