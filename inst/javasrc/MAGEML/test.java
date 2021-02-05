package MAGEML;

/**
 * <p>Title: test for java</p>
 * <p>Description: This class allows testing the java part of the package without R </p>
 * <p>Date: 22/01/2004</p>
 * @author Steffen Durinck
 * @version 1.0
 */


import org.biomage.Common.MAGEJava;
import org.biomage.Experiment.ExperimentDesign;
import org.biomage.Experiment.*;
import java.io.*;


public class test {

  public void test() {
  }

  public  static void main(String args[]) {

    String directory = null;
      //try{
        RMAGESTK rmage = new RMAGESTK("/home/steffen/data/MEXP-14/");
        System.out.print("ok");
       // rmage.writeMAGEML("test.xml","/home/steffen/");
       // MAGEJava mageom = rmage.getMAGEOM();
     //  String expdes = mageom.getExperiment_package().getFromExperiment_list(0).getExperimentDesign().getFromExperimentalFactors(0).getIdentifier();
     //  String expdes = mageom.getExperiment_package().getFromExperiment_list(0).getFromExperimentDesigns(0).getFromExperimentalFactors(0).getIdentifier();
      // System.out.print(expdes);
     // }
      //catch(Exception e){
        //System.out.println("Error occured while trying to parse your Experiment data, the directory probably did not contain MAGEML data or unvalid MAGEML documents\n");
      //}


  }

}
