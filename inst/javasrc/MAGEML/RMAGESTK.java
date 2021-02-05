
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

package MAGEML;

import org.biomage.Common.MAGEJava;
import org.biomage.BioAssayData.*;
import org.biomage.DesignElement.*;
import org.biomage.BioAssay.*;
import org.biomage.BioMaterial.*;

import java.util.Hashtable;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xml.serialize.*;
import java.nio.charset.*;


public class RMAGESTK {

  public MAGEReaderDTDinc mr;
  public MAGEJava mageom;
  public String selectedDED="";
  public String selectedQTD="";
  public String [] selectedMBA;
  public String [] targets;
  public String [] bioSequenceNames;
  public String [] bioSequenceId;
  public String [] columns;
  public String [] rows;
  public String [] zones;
  public int numOfFeatures=0;
  public MAGEMLupdater mup;
  public MAGEGUI magegui=new MAGEGUI();


//variables for updating MAGEML

  public String [] qtID;
  public String [] qtName;
  public String [] qtScale;
  public String [] qtDataType;
  public int [] MBA;
  public int MBAindex = 0;
  public int QTindex = 0;
  public RMAGESTK(String directory) {

    MAGEReaderDTDinc mr = new MAGEReaderDTDinc(directory);
    mageom = mr.getMAGEobj();

  }

  public String[] getDesignElementDimension(){

    String [] DED = new String [mageom.getBioAssayData_package().getDesignElementDimension_list().size()];
    for( int i=0; i<mageom.getBioAssayData_package().getDesignElementDimension_list().size();i++){
      DED[i]=  mageom.getBioAssayData_package().getFromDesignElementDimension_list(i).getIdentifier();
    }
    return DED;
  }

  public String[] getFeatures(String DED) {

    String[] features = new String[10];
    try{
      String[] designElementDimension = new String[mageom.
          getBioAssayData_package().getDesignElementDimension_list().size()];
      int DEDposition = 0;
      for (int i = 0;
           i <
           mageom.getBioAssayData_package().getDesignElementDimension_list().size();
           i++) {
        if (mageom.getBioAssayData_package().getFromDesignElementDimension_list(
            i).getIdentifier().equals(DED)) {
          FeatureDimension fd = (FeatureDimension) mageom.
              getBioAssayData_package().getDesignElementDimension_list().
              elementAt(i);
          features = new String[fd.getContainedFeatures().size()];
          for (int j = 0; j < fd.getContainedFeatures().size(); j++) {
            features[j] = fd.getFromContainedFeatures(j).getIdentifier();
          }
        }
      }
      numOfFeatures = features.length;

    }
    catch(Exception e){

      System.out.println("No BioAssayData_package present in MAGE-ML, check if MAGE-ML document containing the experiment information is present");
    }
    return features;
    }

    public Hashtable getFeatureReporterHash(){

      String reporter;
      Hashtable featureReporterHash =  new Hashtable();
      try{
        for (int i = 0;
             i <
             mageom.getDesignElement_package().getFeatureReporterMap_list().size();
             i++) {
          reporter = mageom.getDesignElement_package().
              getFromFeatureReporterMap_list(i).getReporter().getIdentifier();
          for (int j = 0;
               j <
               mageom.getDesignElement_package().getFromFeatureReporterMap_list(i).
               getFeatureInformationSources().size(); j++) {
            featureReporterHash.put(mageom.getDesignElement_package().
                                    getFromFeatureReporterMap_list(i).
                                    getFromFeatureInformationSources(j).
                                    getFeature().getIdentifier(), reporter);
          }
        }
      }
      catch(Exception e){
        String [] array = getArrayIDs();
        String arrayError = "RMAGEML Error:  No DesignElement_package present in MAGE-ML, check if MAGE-ML document containing the array information is present.  For this dataset you need array accession number(s): ";
        for( int i =0; i<array.length;i++){
         arrayError= arrayError + array[i] + " , ";
        }
        System.out.println(arrayError);
      }
      return featureReporterHash;
    }

    public Hashtable getBioSequenceDBHash(){

      Hashtable bioSequenceDBHash =  new Hashtable();

      for(int i=0;i<mageom.getBioSequence_package().bioSequence_list.size();i++){
        String[] nameId = new String [2];
        try{
          nameId[0]=mageom.getBioSequence_package().getFromBioSequence_list(i).getFromSequenceDatabases(0).getAccession();
        }
        catch(Exception e){
          nameId[0] = "none";
        }

        try{
          nameId[1]=mageom.getBioSequence_package().getFromBioSequence_list(i).getName();
          if(nameId[1].equals(""))nameId[1]="none";
        }
        catch(Exception e){
          nameId[1] ="none";
        }
        bioSequenceDBHash.put(mageom.getBioSequence_package().getFromBioSequence_list(i).getIdentifier(),nameId);
      }
      return bioSequenceDBHash;
    }

    public Hashtable getReporterBioSequenceHash(){

      Hashtable  reporterBioSequenceHash =  new Hashtable();
      for(int i=0;i<mageom.getDesignElement_package().getReporter_list().size();i++){
        try{
          reporterBioSequenceHash.put(mageom.getDesignElement_package().getFromReporter_list(i).getIdentifier(),mageom.getDesignElement_package().getFromReporter_list(i).getFromImmobilizedCharacteristics(0).getIdentifier());
        }
        catch(Exception e){
          reporterBioSequenceHash.put(mageom.getDesignElement_package().getFromReporter_list(i).getIdentifier(),"none");

        }
      }
      return reporterBioSequenceHash;
    }

    public int [] getGridDimensions(){

      int [] dim = new int [4];
      int columns=0;
      int rows=0;
      int zones=0;
      int num=0;

      num = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFeatures().size() - 1;
      dim[0] = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(num).getFeatureLocation().getRow().intValue();
      dim[1] = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(num).getFeatureLocation().getColumn().intValue();
      dim[2] = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(num).getZone().getColumn().intValue();
      dim[3] = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(num).getZone().getRow().intValue();

      return dim;
    }

    public Hashtable getFullGridDimensions(){  //used for limma package....might have to split zone in zone_col and zone_row

          Hashtable dimHash = new Hashtable();
          int num = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFeatures().size() - 1;
          int lay = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(num).getZone().getRow().intValue();
          for(int i=0;i<mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFeatures().size();i++){
            String [] dim = new String [3];
            dim[0] = ""+mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(i).getFeatureLocation().getRow().intValue()+"";
            dim[1] = ""+mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(i).getFeatureLocation().getColumn().intValue()+"";
            int zone = mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(i).getZone().getColumn().intValue()+ ((mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(i).getZone().getRow().intValue()-1)*lay);
            dim[2] = ""+zone+"";
            dimHash.put(mageom.getArrayDesign_package().getFromArrayDesign_list(0).getFromFeatureGroups(0).getFromFeatures(i).getIdentifier(), dim);
        }
        return dimHash;
    }

    /**
     * Function to select DED
     *
     */

    public void selectDED(String knownDED){
      if( knownDED.equals( "none" )){
             String[] DED = getDesignElementDimension();
             if(DED.length > 1){
               selectedDED = magegui.getUserInput(DED,
                                                  "Select Design Element Dimension");
             }
             else{
               selectedDED = DED[0];
             }
           }
           else{
             selectedDED = knownDED;
           }
    }


    /**
     * importFeatures allows to select a DesignElementDimension from which the
     * BioSequence identifier of the corresponding features will be returned as an array
     */

    public boolean importFeatures(String bioCpackage, String knownDED){

      boolean succes = false;
      selectDED( knownDED );
      String[] features = getFeatures(selectedDED);
      Hashtable FRHash = getFeatureReporterHash();
      if( FRHash.size() > 2){
        succes = true;
      }
      if(succes){
        Hashtable BioSeqDBHash = getBioSequenceDBHash();
        Hashtable reporterBioSequenceHash = getReporterBioSequenceHash();
        bioSequenceNames = new String[features.length];
        bioSequenceId = new String[features.length];
        columns =  new String[features.length];
        rows =  new String[features.length];
        zones =  new String[features.length];

        if(bioCpackage.equals("marray")){
          for(int i=0;i<features.length;i++){

            try{
              String [] nameId = (String []) BioSeqDBHash.get(reporterBioSequenceHash.get(FRHash.get(features[i])));
              bioSequenceNames[i] = nameId[1];
              bioSequenceId[i] = nameId[0];
            }
            catch(Exception e){
              bioSequenceNames[i]="none";
              bioSequenceId[i] = "none";
            }
          }
        }
        if(bioCpackage.equals("limma")){

          Hashtable dimHash = getFullGridDimensions();

          for(int i=0;i<features.length;i++){


            try{
              String [] nameId = (String []) BioSeqDBHash.get(reporterBioSequenceHash.get(FRHash.get(features[i])));
              String [] dim = (String []) dimHash.get(features[i]);
              bioSequenceNames[i] = nameId[1];
              bioSequenceId[i] = nameId[0];
              columns[i] = dim [0];
              rows[i] = dim [1];
              zones[i] = dim [2];

            }

            catch(Exception e){
              bioSequenceNames[i]="none";
              bioSequenceId[i] = "none";
              columns[i] = "0";
              rows[i] = "0";
              zones[i] = "0";

            }
          }
        }
      }
      return succes;
    }


    public String [] getBioSequenceNames(){
      return bioSequenceNames;
    }

    public String [] getBioSequenceID(){
      return bioSequenceId;
    }

    public String [] getColumns(){
      return columns;
    }
    public String [] getRows(){
      return rows;
    }
    public String [] getZones(){
      return zones;
    }

    public String getBioSeqDB(){
      String db = "none";
      try{
        db = mageom.getBioSequence_package().getFromBioSequence_list(0).getFromSequenceDatabases(0).getDatabase().getIdentifier();
      }
      catch(Exception e){

      }
      return db;
    }

    /**
     * selectQTypes uses the function getQuantitationTypes to get the QuantitationTypes and
     * then uses the MAGEGUI class to display the available QuantitationTypes and allow
     * selection by the user.  Once a selection has been made, an array of integers pointing to the
     * the columns in the external data file that contain the desired QuantitationType
     * Note:  the current implementation is specific for cDNA arrays
     *
     */

    public int [] selectQTypes(String knownQTD, String nameRf, String nameRb, String nameGf, String nameGb){

      String[] qTypes= getQuantitationTypes(knownQTD);
      String Rb, Gb, Rf, Gf ="";
      int[] intQT = new int [4];
      if(nameRf.equals("none")){
        Gf = magegui.getUserInput(qTypes,
                                  "Select QuantitationType for green foreground");
        Gb = magegui.getUserInput(qTypes,
                                  "Select QuantitationType for green background");
        Rf = magegui.getUserInput(qTypes,
                                  "Select QuantitationType for red foreground");
        Rb = magegui.getUserInput(qTypes,
                                  "Select QuantitationType for red background");
      }
      else{
        Rb = nameRb;
        Gb = nameGb;
        Rf = nameRf;
        Gf = nameGf;
      }
      for (int i=0;i<qTypes.length;i++){
        if(Gf.equals(qTypes[i])){
          intQT[0]=i+1;
        }
        if(Gb.equals(qTypes[i])){
          intQT[1]=i+1;
        }
        if(Rf.equals(qTypes[i])){
          intQT[2]=i+1;
        }
        if(Rb.equals(qTypes[i])){
          intQT[3]=i+1;
        }
      }
      return intQT ;
    }

    public String[] getTargets(){

      return targets;
    }

    public void setTargets(String [] selectedMBA){

      String sampleDetails="";
      Hashtable sample = new Hashtable();
      String currentMBA = "";
      String [] mbaLabelSample = new String[selectedMBA.length];
      Hashtable MBAtoPBA = new Hashtable();

      for (int i=0; i<mageom.getBioAssay_package().getBioAssay_list().size();i++){
      String bioAssay = mageom.getBioAssay_package().getFromBioAssay_list(i).getModelClassName();
      if (bioAssay.equals("MeasuredBioAssay")){
        if( mageom.getBioAssay_package().getFromBioAssay_list(i).getModelClassName().equals("MeasuredBioAssay")){
          MeasuredBioAssay mb =(MeasuredBioAssay) mageom.getBioAssay_package().getBioAssay_list().elementAt(i);
          MBAtoPBA.put(mb.getFromMeasuredBioAssayData(0).getIdentifier(),mb.getFeatureExtraction().getPhysicalBioAssaySource().getIdentifier());
        }
      }
      if (bioAssay.equals("PhysicalBioAssay")){
         PhysicalBioAssay PBA = (PhysicalBioAssay) mageom.getBioAssay_package().getBioAssay_list().elementAt(i);
         String [] labeledExtract = new String[2];
         for (int j=0;j<PBA.getBioAssayCreation().getSourceBioMaterialMeasurements().size();j++){
           labeledExtract[j]=PBA.getBioAssayCreation().getFromSourceBioMaterialMeasurements(j).getBioMaterial().getIdentifier();
         }
         sample.put(PBA.getIdentifier(),labeledExtract);
       }
      }

      Hashtable labExtToLabel= new Hashtable();
      Hashtable bioSampleIDToName= new Hashtable();
      Hashtable labExtToBioMat= new Hashtable();
      Hashtable  compoundHash= new Hashtable();



      for(int i=0;i<mageom.getBioMaterial_package().getBioMaterial_list().size();i++){

        String LabeledExtract = mageom.getBioMaterial_package().getFromBioMaterial_list(i).getIdentifier();
        String BioMaterial = mageom.getBioMaterial_package().getFromBioMaterial_list(i).getModelClassName();

        if (BioMaterial.equals("LabeledExtract")){
          LabeledExtract le = (LabeledExtract) mageom.getBioMaterial_package().getBioMaterial_list().elementAt(i);
          labExtToBioMat.put(le.getIdentifier(),le.getFromTreatments(0).getFromSourceBioMaterialMeasurements(0).getBioMaterial().getIdentifier());
          labExtToLabel.put(le.getIdentifier(),le.getFromLabels(0).getIdentifier());
        }

        if (BioMaterial.equals("BioSample")){
          BioSample bioSample = (BioSample) mageom.getBioMaterial_package().getBioMaterial_list().elementAt(i);
          bioSampleIDToName.put(bioSample.getIdentifier(),bioSample.getName());
        }
      }
      for(int i=0;i<mageom.getBioMaterial_package().getCompound_list().size();i++){
        compoundHash.put(mageom.getBioMaterial_package().getFromCompound_list(i).getIdentifier(),mageom.getBioMaterial_package().getFromCompound_list(i).getName());
      }

      for(int i=0;i<selectedMBA.length;i++){
        String [] targetInfo = new String[6];
        String [] target=(String []) sample.get(MBAtoPBA.get(selectedMBA[i]));
        targetInfo[0]=target[0];
        targetInfo[1]=(String)bioSampleIDToName.get(labExtToBioMat.get(target[0]));
        targetInfo[2]=(String) compoundHash.get(labExtToLabel.get(target[0]));
        targetInfo[3]=target[1];
        targetInfo[4]=(String)bioSampleIDToName.get(labExtToBioMat.get(target[1]));
        targetInfo[5]=(String) compoundHash.get(labExtToLabel.get(target[1]));
        mbaLabelSample[i]= targetInfo[1]+"$"+targetInfo[2]+"$"+targetInfo[4]+"$"+targetInfo[5];
      }
     targets = mbaLabelSample;
    }

    public String[] getExternalData(String bioCpackage, String knownDED){

       selectDED( knownDED );
      //if(selectedDED.equals("")){   //getExternalData needs a DesignElementDimension and QuantitationType Dimension to be selected first!!
       // importFeatures(bioCpackage, knownDED);
     // }
      //if(selectedQTD.equals("")){
       // selectQTypes();
      //}
      int index=0;
      int count=0;
      for(int i=0;i<mageom.getBioAssayData_package().getBioAssayData_list().size();i++){
        if(mageom.getBioAssayData_package().getFromBioAssayData_list(i).getQuantitationTypeDimension().getIdentifier().equals(selectedQTD)
           && mageom.getBioAssayData_package().getFromBioAssayData_list(i).getDesignElementDimension().getIdentifier().equals(selectedDED)){
          count++;
        }
      }
      String[] externalDataRefs= new String[count];

      for(int i=0;i<mageom.getBioAssayData_package().getBioAssayData_list().size();i++){
        if(mageom.getBioAssayData_package().getFromBioAssayData_list(i).getQuantitationTypeDimension().getIdentifier().equals(selectedQTD)
           && mageom.getBioAssayData_package().getFromBioAssayData_list(i).getDesignElementDimension().getIdentifier().equals(selectedDED)){
          //select the external data filenames
          BioDataCube bdc = (BioDataCube) mageom.getBioAssayData_package().getFromBioAssayData_list(i).getBioDataValues();

        //  if(bdc.getValueOrder()==2 || bdc.getValueOrder()==0){  //commented out order check!!
            externalDataRefs[i] = (String) bdc.getDataExternal().getFilenameURI();
            index++;
        //  }

         // else{
         //   System.out.println("Order of BioDataCube is not BDQ, other orderings are not supported yet");
          //  System.exit(0);
          //}
        }
      }
      String [] selectedMBA = new String [index];
      index=0;
      for(int i=0;i<mageom.getBioAssayData_package().getBioAssayData_list().size();i++){
        if(mageom.getBioAssayData_package().getFromBioAssayData_list(i).getQuantitationTypeDimension().getIdentifier().equals(selectedQTD)
           && mageom.getBioAssayData_package().getFromBioAssayData_list(i).getDesignElementDimension().getIdentifier().equals(selectedDED)){
          //select the BioAssayData type in order to add target informatiom later on
          selectedMBA[index]=mageom.getBioAssayData_package().getFromBioAssayData_list(i).getIdentifier();
          index++;
        }
      }

      setTargets(selectedMBA);
      return externalDataRefs;
    }

    public String[] getQuantitationTypes(String knownQTD){
      int index = 0;  //to store which QTDimsension was selected
      String[] QTDimensions = new String[mageom.getBioAssayData_package().getQuantitationTypeDimension_list().size()];

      for(int i=0;i<mageom.getBioAssayData_package().getQuantitationTypeDimension_list().size();i++){
        QTDimensions[i]=mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(i).getIdentifier();
      }
      if(knownQTD.equals("none")){
        if(QTDimensions.length > 1){
          selectedQTD = magegui.getUserInput(QTDimensions,
              "Select the QuantitationType Dimension");
        }
        else{
          selectedQTD = QTDimensions[0];
        }
      }
      else{
        selectedQTD=knownQTD;
      }
      for(int i=0;i<mageom.getBioAssayData_package().getQuantitationTypeDimension_list().size();i++){
        if(selectedQTD.equals(mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(i).getIdentifier())) index=i;
      }

      String[] qTypes = new String[mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(index).getQuantitationTypes().size()];
      for(int i=0;i<mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(index).getQuantitationTypes().size();i++){
        qTypes[i]=mageom.getBioAssayData_package().getFromQuantitationTypeDimension_list(index).getFromQuantitationTypes(i).getIdentifier();
      }
      return qTypes;
    }


//////////////////////////methods for extracting additional information from MAGEML///////////////////////////////////

    public int getSizeDED ( String knownDED ){

      selectDED ( knownDED );
      String[] features = getFeatures(selectedDED);
      int size = features.length;
      return size;
    }

   public String getOrganization(){

     int positionOfOrganizationContact = 0;
     String organization = "none";
     boolean present = false;
     for(int i = 0; i < mageom.auditAndSecurity_package.getContact_list().size(); i++){
       if(mageom.auditAndSecurity_package.getFromContact_list(i).getModelClassName().equals("Organization")){
         positionOfOrganizationContact = i;
         present = true;
       }
     }
     if( present ){
       organization = mageom.auditAndSecurity_package.getFromContact_list(
           positionOfOrganizationContact).getIdentifier();
     }
     return organization;
   }

   public String[] getArrayIDs(){

     int arrays = mageom.array_package.array_list.size();
     String [] arrayIDs = new String[arrays];
     String currentArray ="";
     String newArray = "";
     int index = 0;

     for (int i = 0; i<arrays; i++){
       newArray = mageom.array_package.getFromArray_list(i).getArrayDesign().getIdentifier();
       if (!newArray.equals(currentArray)){
         currentArray = newArray;
         arrayIDs[index] = newArray;
         index++;
       }
     }
     String [] uniqueArrayIDs = new String[index];
     for(int i = 0; i<index; i++){
       uniqueArrayIDs[ i ] = arrayIDs[ i ];
     }
     return uniqueArrayIDs;
   }

  public String getQuantitationTypeDescription(String QuantitationTypeID){
    String description = "No description available";
    int index = 0;

    for(int i =  0; i < mageom.quantitationType_package.getQuantitationType_list().size(); i++){

      if(mageom.quantitationType_package.getFromQuantitationType_list(i).getIdentifier().equals(QuantitationTypeID)){
        index = i;
      }
    }
    try{
      description = mageom.quantitationType_package.
          getFromQuantitationType_list(index).getFromDescriptions(0).getText();

    }
    catch(Exception e){

    }
    return description;

  }


////////////////////////////////////////below here all update functions////////////////////////////////////////////////

    public void updateMAGEML ( String file, String protocolID, String protocol, String date, String qtDimID, String transID, int numberOfFeatures, String knownDED, String BAD, String derivedBioAssayID,String derivedBioAssayDataID){

      mup = new MAGEMLupdater( mageom );
      selectDED ( knownDED );
      mageom = mup.updateBioAssayData( file, protocolID, protocol, date ,qtID, qtName, qtScale, qtDataType ,qtDimID, transID,selectedDED, BAD, derivedBioAssayID, derivedBioAssayDataID, MBA );

    }


   public void addQuantitationTypeInfo ( String qtIDs, String qtNames, String qtScales, String qtDataTypes ){
      qtID[ QTindex ] = qtIDs;
      qtName[ QTindex ] = qtNames;
      qtScale[ QTindex ] = qtScales;
      qtDataType[ QTindex ] = qtDataTypes;
      QTindex++;
   }

   public void createNewQTDim( int size ){
     QTindex = 0;
     qtID = new String [ size ];
     qtName = new String [ size ];
     qtScale = new String [ size ];
     qtDataType = new String [ size ];
   }

   public void createNewMBASet( int size ){
     MBAindex = 0;
     MBA = new int[ size ];
   }

   public void setMBA( int MBAind ){
     MBA[ MBAindex ] =  MBAind;
     MBAindex++;
   }

   public void writeMAGEML(String magemlOutFile, String directory){
     try {
  //   File outFile = new File(directory + "/unindented.xml");
//     FileOutputStream fstream = new FileOutputStream(outFile);

//       PrintWriter tmpFile = new PrintWriter(fstream);
       //OutputStreamWriter tmpFile = new OutputStreamWriter(fstream, Charset.forName("UTF-8"));
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

   public MAGEJava getMAGEOM(){
     return mageom;
   }
 }