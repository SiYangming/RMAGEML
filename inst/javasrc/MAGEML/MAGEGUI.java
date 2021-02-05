package MAGEML;

/**
 * <p>Title: MAGEGUI</p>
 * <p>Description: Graphical user interface for MAGEML package</p>
 * <p>Date: 22/01/2004</p>
 * @author Steffen Durinck
 * @version 1.0
 */
import javax.swing.*;

public class MAGEGUI {

  String currentSelection = "";
  boolean selected = false;

  public MAGEGUI() {
         }

public String getUserInput(String [] selection, String selectionLabel){

    JFrame frame = new JFrame("Bioconductor and MAGEML");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setDefaultLookAndFeelDecorated(true);
    frame.pack();
    JComponent contentPane = new selectionPanel(selection, this, selectionLabel);
    contentPane.setOpaque(true);
    frame.setContentPane(contentPane);
    frame.setSize(500, (selection.length*20)+90);
    frame.setVisible(true);

    while(!selected){
    frame.repaint();
    }
    frame.dispose();
    selected=false;
    return currentSelection;
  }

public void setCurrentSelection(String currentSelection){
    this.currentSelection=currentSelection;
    selected=true;
  }
}