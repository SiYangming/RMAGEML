package MAGEML;

/***************************************************************************
 *                                                                         *
 * C O P Y R I G H T   N O T I C E                                         *
 *  Copyright (c) 2004 by:                                                 *
 *    * ESAT KUleuven                                                      *
 *                                                                         *
 *                                                                         *
 *    All Rights Reserved.                                                 *
 *                                                                         *
 * Permission is hereby granted, free of charge, to any person             *
 * obtaining a copy of this software and associated documentation files    *
 * (the "Software"), to deal in the Software without restriction,          *
 * including without limitation the rights to use, copy, modify, merge,    *
 * publish, distribute, sublicense, and/or sell copies of the Software,    *
 * and to permit persons to whom the Software is furnished to do so,       *
 * subject to the following conditions:                                    *
 *                                                                         *
 * The above copyright notice and this permission notice shall be          *
 * included in all copies or substantial portions of the Software.         *
 *                                                                         *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,         *
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF      *
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND                   *
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS     *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN      *
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN       *
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE        *
 * SOFTWARE.                                                               *
 ***************************************************************************
 *                                                                         *
 *                                                                         *
 * @author  Steffen Durinck, ESAT KULeuven Belgium                         *
 * @version Revision: 1.0                                                  *
 * @date    Tue, Feb 10, 2004 05:33:53 PM                                  *
 *                                                                         *
 ***************************************************************************
 */

import org.biomage.Common.MAGEJava;
import org.biomage.Description.OntologyEntry;
import org.biomage.BioAssayData.*;
import org.biomage.BioAssay.*;
import org.biomage.QuantitationType.*;
import org.biomage.Protocol.*;
import java.io.*;
import org.xml.sax.helpers.*;
import org.apache.xml.serialize.*;
import org.xml.sax.*;

public class MAGEMLupdater {

  public MAGEJava mageom;

  public MAGEMLupdater(MAGEJava mageom) {
    this.mageom =  mageom;

  }

  public MAGEJava updateBioAssayData(String file, String protocolID, String protocolDesc, String date, String [] qtID, String [] qtName, String [] qtScale, String [] qtDataType, String qtDimID, String transID, String DED, String BAD, String derivedBioAssayID,String derivedBioAssayDataID, int []MBA){

    QuantitationTypeDimension qtDim = new QuantitationTypeDimension();
    boolean qtdAlreadyPresent = false;
    Protocol protocol = new Protocol();
    boolean newProtocol = true;
    String qtDimIDCompl = qtDimID;

    BioDataCube bioDataCube = new BioDataCube();

    //update BioAssayDimension of BioAssayData_package

    BioAssayDimension bioAssayDimension = new BioAssayDimension();

    bioAssayDimension.setIdentifier(BAD);
    //bioAssayDimension.setName("normalized");
    DerivedBioAssay derivedBioAssay = new DerivedBioAssay();
    derivedBioAssay.setIdentifier(derivedBioAssayID);
    bioAssayDimension.addToBioAssays(derivedBioAssay);

    //update Experiment_package with BioAssayDimension

    mageom.experiment_package.getFromExperiment_list(0).addToBioAssays(derivedBioAssay);


    //update DerivedBioAssayData
    DerivedBioAssayData derivedBioAssayData = new DerivedBioAssayData();
    derivedBioAssayData.setIdentifier(derivedBioAssayDataID);
    //derivedBioAssayData.setName("normalized data");
    derivedBioAssayData.setBioAssayDimension(bioAssayDimension);

    //update BioAssay_package
    derivedBioAssay.addToDerivedBioAssayData(derivedBioAssayData);
    mageom.bioAssay_package.addToBioAssay_list(derivedBioAssay);

    for(int i = 0; i < mageom.getBioAssayData_package().getDesignElementDimension_list().size();i++){
      if (mageom.getBioAssayData_package().getFromDesignElementDimension_list(i).
          getIdentifier().equals(DED)) {
        DesignElementDimension designElementDimension = mageom.
            getBioAssayData_package().getFromDesignElementDimension_list(i);
        derivedBioAssayData.setDesignElementDimension(designElementDimension);
      }
    }

    //if new QTDim required make one

    //check if QuantitationTypeDimension already exists

    for(int u=0;u<mageom.getBioAssayData_package().getQuantitationTypeDimension_list().size();u++){;
      if (qtDimIDCompl.equals(mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(u).getIdentifier())){
        qtdAlreadyPresent = true;
        qtDim = mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(u); //if QuantitationTypeDimensio already exists, it will be used
      }
    }

    // if QuantitationTypeDimension doesn't yet exists, create new one

    if( !qtdAlreadyPresent ){
      qtDim.setIdentifier( qtDimIDCompl );
      //qtDim.setName( qtDimName );
      for(int y=0; y < qtID.length; y++){
        DerivedSignal derivedSignal = updateQuantitationTypePackage( qtName[ y ],
            qtID[ y ], qtScale[ y ], qtDataType[ y ]);
        qtDim.addToQuantitationTypes( derivedSignal );
      }
    }

    derivedBioAssayData.setQuantitationTypeDimension(qtDim);

    bioDataCube.setValueByNameOrder("BDQ");
    DataExternal dataExternal = new DataExternal();
    dataExternal.setFilenameURI(file);
    dataExternal.setDataFormat("tab delimited");
    bioDataCube.setDataExternal(dataExternal);

    derivedBioAssayData.setBioDataValues(bioDataCube);

    //check if Protocol already present

    for (int i = 0; i < mageom.protocol_package.getProtocol_list().size(); i++){
      if(mageom.getProtocol_package().getFromProtocol_list(i).getIdentifier().equals(protocolID)){
        protocol = mageom.protocol_package.getFromProtocol_list(i);
        newProtocol = false;
      }
    }

    if(newProtocol){
      //Protocol not present so create new one
      protocol = createProtocol(protocolDesc, protocolID);
    }

    Transformation transformation = createTransformation(protocol, date, transID, MBA);
    derivedBioAssayData.setProducerTransformation(transformation);
    BioAssayData_package bioAssayData_pack = mageom.getBioAssayData_package();
    bioAssayData_pack.addToBioAssayDimension_list(bioAssayDimension);
    bioAssayData_pack.addToBioAssayData_list(derivedBioAssayData);
    mageom.setBioAssayData_package(bioAssayData_pack);   ///added later not necessary
    if(!qtdAlreadyPresent){
      bioAssayData_pack.addToQuantitationTypeDimension_list(qtDim);
    }
    return mageom;
  }


  /**
   * create new ProtocolApplication and Transformation
   */

  public Transformation createTransformation(Protocol protocol, String date, String transID, int [] MBA){

    ProtocolApplication protocolApp = new ProtocolApplication();
    protocolApp.setProtocol(protocol);
    protocolApp.setActivityDate(date);
    Transformation transformation = new Transformation();
    for (int i = 0; i< MBA.length; i++){
      int MBApos =  MBA[ i ] - 1;
      transformation.addToBioAssayDataSources(mageom.bioAssayData_package.
                                              getFromBioAssayData_list(MBApos));
    }
    transformation.addToProtocolApplications(protocolApp);
    transformation.setIdentifier(transID);
    return(transformation);
  }



  /**
   * create new Protocol
   */

  public Protocol createProtocol(String protocolDesc, String protocolID){

    Protocol protocol = new Protocol();
    //SoftwareApplication softwareApp = new SoftwareApplication();
    //Software software = new Software();
    //software.setIdentifier("softID");
    //software.setName("BioConductor");
    //softwareApp.setSoftware(software);
    //protocol.addToSoftwares(software);
    protocol.setText(protocolDesc);
    protocol.setIdentifier(protocolID);
    mageom.protocol_package.addToProtocol_list(protocol);
    return protocol;
  }

  /**
   * update the QuantitationType_package
   */

  public DerivedSignal updateQuantitationTypePackage(String name, String identifier, String scale, String dataType){

    boolean newQT = true;
    int quantitationTypeRef = 0;
    Boolean isBackground = new Boolean(false);

     //check if quantitationType already exists

    for(int i = 0; i < mageom.getQuantitationType_package().getQuantitationType_list().size(); i++){
      if(mageom.getQuantitationType_package().getFromQuantitationType_list(i).getIdentifier().equals(identifier)){
        newQT = false;
        quantitationTypeRef = i;
      }
    }

    DerivedSignal derivedSignal = new DerivedSignal();

    //if QT doesn't yet exists, make a new one

    if(newQT){
      derivedSignal.setIdentifier(identifier);
      derivedSignal.setName(name);
      derivedSignal.setIsBackground(isBackground);
      OntologyEntry scaleOE = new OntologyEntry();
      scaleOE.setCategory("Scale");
      scaleOE.setValue(scale);
      OntologyEntry dataTypeOE = new OntologyEntry();
      dataTypeOE.setCategory("DataType");
      dataTypeOE.setValue(dataType);
      derivedSignal.setScale(scaleOE);
      derivedSignal.setDataType(dataTypeOE);
      QuantitationType_package quantitationType_pack = mageom.
          getQuantitationType_package();
      quantitationType_pack.addToQuantitationType_list(derivedSignal);
    }

    //if QT does already exists, use that one

    else{
      derivedSignal = (DerivedSignal) mageom.getQuantitationType_package().getFromQuantitationType_list(quantitationTypeRef);
    }

    return derivedSignal;

  }

  public MAGEJava getMAGEOM(){
    return mageom;
  }

  /**
   * write MAGEML
   */

  public void writeMAGEML(String magemlOutFile, String directory){
    try {
      //File outFile = new File(directory + "/unindented.xml");
      FileWriter tmpFile = new FileWriter( directory + "/unindented.xml");
      mageom.writeMAGEML(tmpFile);
      tmpFile.flush();
      tmpFile.close();
      OutputFormat xmlFormat = new OutputFormat("xml", "utf-8", true);
      xmlFormat.setLineWidth(60);
      xmlFormat.setLineSeparator("\n");
      FileWriter endFile = new FileWriter( directory + "/" + magemlOutFile );

      XMLSerializer xmlWriterDocHndler =
          new XMLSerializer(endFile, xmlFormat);
      XMLReader xmlWriterParser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");

      xmlWriterParser.setContentHandler(xmlWriterDocHndler);
      xmlWriterParser.parse(new InputSource(new FileReader( directory + "/unindented.xml")));
      endFile.flush();
      endFile.close();
    }
    catch(Exception e){

    }
  }
}