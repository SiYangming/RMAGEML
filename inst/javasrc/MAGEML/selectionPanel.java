package MAGEML;

/**
 * <p>Title: selectionPanel</p>
 * <p>Description: Panel to display selection</p>
 * <p>Date: 22/01/2004</p>
 * @author Steffen Durinck
 * @version 1.0
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class selectionPanel extends JPanel implements ActionListener {
  public String selectedItem = "";
  public MAGEGUI magegui;

  public selectionPanel(String[] selection, MAGEGUI magegui, String selectionLabel){
    super(new BorderLayout());
    this.magegui = magegui;
    JLabel label = new JLabel(selectionLabel);

    JPanel radioPanel = new JPanel(new GridLayout(0,1));
    ButtonGroup BGroup = new ButtonGroup();
    for (int i=0; i<selection.length; i++){
      JRadioButton ded = new JRadioButton(selection[i]);
      ded.setMnemonic(KeyEvent.VK_B);
      ded.setSelected(true);
      BGroup.add(ded);
      ded.addActionListener(this);
      radioPanel.add(ded);
    }
    add(label,BorderLayout.BEFORE_FIRST_LINE);
    add(radioPanel, BorderLayout.LINE_START);
    setBorder(BorderFactory.createEmptyBorder(20,20,20,20));


  }

public void actionPerformed(ActionEvent e){
    selectedItem =  e.getActionCommand();
    magegui.setCurrentSelection(selectedItem);
  }


}