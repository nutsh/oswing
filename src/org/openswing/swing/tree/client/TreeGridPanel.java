package org.openswing.swing.tree.client;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import org.openswing.swing.util.client.ClientSettings;
import org.openswing.swing.message.receive.java.Response;
import org.openswing.swing.message.receive.java.VOResponse;
import org.openswing.swing.message.receive.java.ValueObject;
import java.lang.reflect.*;
import java.text.Format;
import javax.swing.tree.DefaultMutableTreeNode;
import org.openswing.swing.tree.java.OpenSwingTreeNode;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Panel that contains an expandable tree+grid.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 *
 * <p> This file is part of OpenSwing Framework.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the (LGPL) Lesser General Public
 * License as published by the Free Software Foundation;
 *
 *                GNU LESSER GENERAL PUBLIC LICENSE
 *                 Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *       The author may be contacted at:
 *           maurocarniel@tin.it</p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class TreeGridPanel extends JPanel {

  /** attribute names to used to show grid columns */
  private ArrayList gridColumns = new ArrayList();

  /** grid columns sizes */
  private ArrayList gridColumnSizes = new ArrayList();

  /** expandable tree */
  private TreeGrid tree = new TreeGrid(new TreeGridModel(),"",gridColumnSizes,ClientSettings.PERC_TREE_FOLDER,ClientSettings.PERC_TREE_NODE);

  /** tree root */
  private DefaultMutableTreeNode treeRoot;

  /** tree container */
  private JScrollPane treePane = new JScrollPane();

  /** data source used to fill in the tree */
  private TreeDataLocator treeDataLocator;

  /** tree controller: it manages tree events */
  private TreeController treeController;

  /** pop-up menu related to right mouse click on a tree node (optional) */
  private JPopupMenu popup = new JPopupMenu();

  /** collection of pairs: menu item description (not yet translated), menu item object; used to change the menu item abilitation */
  private Hashtable menuItems = new Hashtable();

  /** flag used inside addNotify method */
  private boolean firstTime = true;

  /** image icon used for leaves; default value: as for folders */
  private String leavesImageName = ClientSettings.getInstance().PERC_TREE_FOLDER;

  /** define if tree will be filled on viewing this panel; default value: true */
  private boolean loadWhenVisibile = true;

  /** define if all tree nodes must be expanded after loading */
  private boolean expandAllNodes = false;

  /** folder node image name */
  private String folderIconName = ClientSettings.PERC_TREE_FOLDER;

  /** getter methods in v.o. inside the tree node related to gridColumns */
  private Method[] getterMethods = null;;

  /** column headers; as default value are setted as attribute names translations */
  private ArrayList columnHeaders = new ArrayList();

  /** column data formatters; as default settings all columns are converted into String objects */
  private ArrayList columnFormatters = new ArrayList();


  /**
   * Constructor.
   */
  public TreeGridPanel() {
    try {
      jbInit();
      treePane.getViewport().setBackground(ClientSettings.GRID_CELL_BACKGROUND);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  public void addNotify() {
    super.addNotify();
    if (firstTime && loadWhenVisibile) {
      firstTime = false;
      new Thread() {
        public void run() {
          createTree();
        }
      }.start();
    }
  }


  /**
   * Force tree reloading.
   */
  public final void reloadTree() {
    new Thread() {
      public void run() {
        if (!loadWhenVisibile && firstTime) {
          firstTime = false;
          createTree();
          if (expandAllNodes)
            expandAllNodes();
        }
        else {
          Response response = treeDataLocator.getTreeModel(tree.getTree());
          if (response.isError())
            treeRoot = new OpenSwingTreeNode();
          else
            treeRoot = (DefaultMutableTreeNode)((DefaultTreeModel)((VOResponse)response).getVo()).getRoot();
          recreateTree();
          if (expandAllNodes)
            expandAllNodes();
        }
      }
    }.start();
  }


  /**
   * Expand all tree nodes.
   */
  private final void expandAllNodes() {
    int i=0;
    while(i<tree.getRowCount())
      tree.getTree().expandRow(i++);
  }


  void jbInit() throws Exception {
    this.setLayout(new java.awt.BorderLayout());
    treePane.getViewport().add(tree,null);
    this.add(treePane,BorderLayout.CENTER);
    treePane.setBorder(BorderFactory.createCompoundBorder(
                         BorderFactory.createRaisedBevelBorder(),
                         BorderFactory.createLoweredBevelBorder()
                        ));
    treePane.setAutoscrolls(true);
  }


  /**
   * Redraw the tree. Used when the tree model has been modified.
   */
  public final void repaintTree() {
    recreateTree();
    TreePath selPath = tree.getTree().getSelectionPath();
    tree.repaint();
    try {
      tree.getTree().setSelectionPath(selPath.getParentPath());
    }
    catch (Exception ex) {
    }
    try {
      tree.getTree().setSelectionPath(selPath);
    }
    catch (Exception ex) {
    }
  }


  /**
   * Remove all nodes (expept the root node) from the tree.
   */
  public final void clearTree() {
    treeRoot = new OpenSwingTreeNode();
    repaintTree();
  }


  /**
   * Fill in the tree.
   */
  private void createTree() {
    Response response = treeDataLocator.getTreeModel(tree.getTree());
    if (response.isError())
      treeRoot = new OpenSwingTreeNode();
    else
      treeRoot = (DefaultMutableTreeNode)((DefaultTreeModel)((VOResponse)response).getVo()).getRoot();
    recreateTree();
  }


  private void recreateTree() {

    treePane.getViewport().remove(tree);
    tree = new TreeGrid(new TreeGridModel(treeRoot),gridColumns.get(0).toString(),gridColumnSizes,folderIconName,leavesImageName);
    tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

//    tree.getTree().setShowsRootHandles(true);
//    try {
//      TreeNodeRenderer renderer = new TreeNodeRenderer(this,folderIconName,leavesImageName);
//      tree.setCellRenderer(renderer);
//    } catch (Exception ex) {
//      ex.printStackTrace();
//    }

    treePane.getViewport().add(tree);
    tree.setSize(new Dimension((int)this.getPreferredSize().getWidth()/2,
                               (int)this.getPreferredSize().getHeight()));

    tree.setMinimumSize(new Dimension(0,200));
    tree.revalidate();

    MouseListener ml = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e))
          treeLeftClick(e, tree.getTree());
        else if(e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e))
          treeRightClick(e, tree.getTree());
        if(e.getClickCount() == 2)
          treeDoubleClick(e, tree.getTree());
      }
    };
    tree.addMouseListener(ml);
  }


  /**
   * @return selected node or null if no node is selected
   */
  public final DefaultMutableTreeNode getSelectedNode() {
    try {
      javax.swing.tree.TreePath selPath = tree.getTree().getSelectionPath();
      if (selPath != null)
        return (DefaultMutableTreeNode)(selPath.getPathComponent(selPath.getPathCount()-1));
      else
        return null;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


  /**
   * Method called when user has double clicked.
   * @param e double click event
   * @param tree tree
   */
  public final void treeDoubleClick(MouseEvent e,JTree tree) {
    try {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      javax.swing.tree.TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if (selPath != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)(selPath.getPathComponent(selPath.getPathCount()-1));
        treeController.doubleClick(node);

      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   * Method called when user has clicked on the left mouse button.
   * @param e left mouse button click event
   * @param tree tree
   */
  public final void treeLeftClick(MouseEvent e,JTree tree) {
    try {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      javax.swing.tree.TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if (selPath != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)(selPath.getPathComponent(selPath.getPathCount()-1));
        treeController.leftClick(node);

      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    treePane.setEnabled(enabled);
  }


  /**
   * Method called when user has clicked on the right mouse button.
   * @param e right mouse button click event
   * @param tree tree
   */
  public final void treeRightClick(MouseEvent e,JTree tree) {
    try {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      javax.swing.tree.TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if (selPath != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)(selPath.getPathComponent(selPath.getPathCount()-1));
        if (treeController.rightClick(node) && popup.getComponentCount()>0 && treePane.isEnabled()) {
          // visualizzazione del menu' a pop-up associato al nodo dell'albero,
          // SOLO se il metodo rightClick ha ritornato valore "true" e c'e almeno un elemento nel menu' a pop-up
          popup.show(e.getComponent(), e.getX(), e.getY());
        }

      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   * Add a pop-up menu item.
   * @param menuName menu item description (not yet translated)
   * @param mnemonic mnemonic value
   * @param enabled flag used to set menu item abilitation
   * @param menuListener listener used to capture menu item selection
   */
  public final void addPopupMenuItem(String menuName, int mnemonic,boolean enabled,ActionListener menuListener) {
    JMenuItem cbMenuItem = new JMenuItem(ClientSettings.getInstance().getResources().getResource(menuName));
    cbMenuItem.setMnemonic(mnemonic);
    cbMenuItem.setEnabled(enabled);
    cbMenuItem.addActionListener(menuListener);
    popup.add(cbMenuItem);
    menuItems.put(menuName,cbMenuItem);
  }


  /**
   * Set menu item abilitation.
   * @param menuName menu item description (not yet translated)
   * @param enabled flag used to enable the menu item
   */
  public final void setMenuItemEnabled(String menuName,boolean enabled) {
    JMenuItem menu = (JMenuItem)menuItems.get(menuName);
    if (menu!=null)
      menu.setEnabled(enabled);
  }


  /**
   * @return tree controller: it manages tree events
   */
  public final TreeController getTreeController() {
    return treeController;
  }


  /**
   * @return data source used to fill in the tree
   */
  public final TreeDataLocator getTreeDataLocator() {
    return treeDataLocator;
  }


  /**
   * Set the data source used to fill in the tree
   * @param treeDataLocator data source used to fill in the tree
   */
  public final void setTreeDataLocator(TreeDataLocator treeDataLocator) {
    this.treeDataLocator = treeDataLocator;
  }


  /**
   * Set the tree controller: it manages tree events.
   * @param treeController tree controller: it manages tree events.
   */
  public final void setTreeController(TreeController treeController) {
    this.treeController = treeController;
  }


  /**
   * @return image icon used for leaves
   */
  public final String getLeavesImageName() {
    return leavesImageName;
  }


  /**
   * Set image icon used for leaves.
   * @param leavesImageName image icon used for leaves
   */
  public final void setLeavesImageName(String leavesImageName) {
    this.leavesImageName = leavesImageName;
  }


  /**
   * @return define if tree will be filled on viewing this panel
   */
  public final boolean isLoadWhenVisibile() {
    return loadWhenVisibile;
  }


  /**
   * Define if tree will be filled on viewing this panel.
   * @param loadWhenVisibile define if tree will be filled on viewing this panel
   */
  public final void setLoadWhenVisibile(boolean loadWhenVisibile) {
    this.loadWhenVisibile = loadWhenVisibile;
  }


  /**
   * @return boolean define if all tree nodes must be expanded after loading
   */
  public final boolean isExpandAllNodes() {
    return expandAllNodes;
  }


  /**
   * Define if all tree nodes must be expanded after loading.
   * @param expandAllNodes boolean define if all tree nodes must be expanded after loading
   */
  public final void setExpandAllNodes(boolean expandAllNodes) {
    this.expandAllNodes = expandAllNodes;
  }


  /**
   * @return folder icon name
   */
  public final String getFolderIconName() {
    return folderIconName;
  }


  /**
   * Set the folder icon name.
   * @param treeFolderName folder icon name
   */
  public final void setFolderIconName(String folderIconName) {
    this.folderIconName = folderIconName;
  }



  /**
   * Set the attribute names to used to show grid columns.
   * @param gridColumns attribute names to used to show grid columns
   */
  public final void addGridColumn(String attributeName,int colSize) {
    this.gridColumns.add(attributeName);
    this.gridColumnSizes.add(new Integer(colSize));
    this.columnHeaders.add(attributeName);
    this.columnFormatters.add(null);
  }


  /**
   * Set the column format for the column identified by the specified attribute name.
   * @param attributeName column identifier
   * @param formatter Format object to used for data showed inside the specified column
   */
  public void setColumnFormatter(String attributeName,Format formatter) {
    int index = gridColumns.indexOf(attributeName);
    if (index!=-1)
      columnFormatters.set(index,formatter);
  }


  /**
   * Set the column header for the column identified by the specified attribute name.
   * @param attributeName column identifier
   * @param description description to translate and set as column header
   */
  public final void setColumnHeader(String attributeName,String description) {
    int index = gridColumns.indexOf(attributeName);
    if (index!=-1)
      columnHeaders.set(index,description);
 }


  /**
   * <p>Title: OpenSwing Framework</p>
   * <p>Description: Inner class used to create the tree+grid table model.</p>
   * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
   * <p> </p>
   * @author Mauro Carniel
   * @version 1.0
   */
  class TreeGridModel extends AbstractTreeTableModel {


    public TreeGridModel() {
      this(new OpenSwingTreeNode());
    }


    public TreeGridModel(DefaultMutableTreeNode root) {
      super(root);
    }


    public Object getValueAt(Object node, int column) {
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)node;
      ValueObject vo = (ValueObject)treeNode.getUserObject();

      if (getterMethods==null) {
        try {
          getterMethods = new Method[gridColumns.size()];
          for (int i = 0; i < gridColumns.size(); i++) {
            getterMethods[i] = vo.getClass().getMethod(
                "get" +
                gridColumns.get(i).toString().substring(0, 1).toUpperCase() +
                gridColumns.get(i).toString().substring(1), new Class[0]
            );
          }
        }
        catch (Throwable ex) {
          ex.printStackTrace();
        }
      }
      if (getterMethods==null || getterMethods[column]==null)
        return null;
      try {
        Object value = getterMethods[column].invoke(vo, new Object[0]);
        if (value!=null && columnFormatters.get(column)!=null) {
          Format formatter = (Format)columnFormatters.get(column);
          value = formatter.format(value);
        }
        return value;
      }
      catch (Throwable ex) {
        ex.printStackTrace();
        return null;
      }
    }


    public int getChildCount(Object node) {
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)node;
      return treeNode.getChildCount();
    }


    public Object getChild(Object node, int childNumber) {
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)node;
      return treeNode.getChildAt(childNumber);
    }


    public int getColumnCount() {
      return gridColumns.size();
    }


    public String getColumnName(int col) {
      return ClientSettings.getInstance().getResources().getResource(columnHeaders.get(col).toString());
    }


    /**
     * Returns the class for the particular column.
     */
    public Class getColumnClass(int column) {
      if (column==0)
        return TreeTableModel.class;
      else {
        if (getterMethods==null)
          return super.getColumnClass(column);
        else {
          return getterMethods[column].getReturnType();
        }
      }
    }




  }




}

