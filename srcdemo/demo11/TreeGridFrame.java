package demo11;

import javax.swing.*;
import org.openswing.swing.client.*;
import java.awt.*;
import org.openswing.swing.table.columns.client.*;
import org.openswing.swing.lookup.client.LookupController;
import java.awt.event.*;
import org.openswing.swing.table.java.*;
import org.openswing.swing.tree.client.*;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Tree-table Frame</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author Mauro Carniel
 * @version 1.0
 */
public class TreeGridFrame extends JFrame {

  TreeGridPanel treePanel = new TreeGridPanel();

  public TreeGridFrame(TreeGridFrameController controller) {
    try {
      super.setDefaultCloseOperation(super.EXIT_ON_CLOSE);
      jbInit();
      setSize(750,300);
      setTitle("Bill of material");

      treePanel.setTreeController(controller);
      treePanel.setTreeDataLocator(new TreeGridDataLocator());
      treePanel.setFolderIconName("node.gif");
      treePanel.setLeavesImageName("node.gif");
      treePanel.addGridColumn("itemCode",100);
      treePanel.addGridColumn("description",200);
      treePanel.addGridColumn("qty",40);
      treePanel.addGridColumn("price",80);
      treePanel.setIconAttributeName("iconName");
      treePanel.setBackground(Color.orange);
      setVisible(true);

    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }



  private void jbInit() throws Exception {
    this.getContentPane().add(treePanel, BorderLayout.CENTER);
  }
  public TreeGridPanel getTreePanel() {
    return treePanel;
  }


}

