package org.openswing.swing.tree.client;

import java.beans.*;

/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author not attributable
 * @version 1.0
 */

public class TreePanelBeanInfo extends SimpleBeanInfo {
  Class beanClass = TreePanel.class;
  String iconColor16x16Filename = "TreePanel16.gif";
  String iconColor32x32Filename = "TreePanel.gif";
  String iconMono16x16Filename = "TreePanel16.gif";
  String iconMono32x32Filename = "TreePanel.gif";

  public TreePanelBeanInfo() {
  }
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor _expandAllNodes = new PropertyDescriptor("expandAllNodes", beanClass, "isExpandAllNodes", "setExpandAllNodes");
      PropertyDescriptor _folderIconName = new PropertyDescriptor("folderIconName", beanClass, "getFolderIconName", "setFolderIconName");
      PropertyDescriptor _iconImageName = new PropertyDescriptor("iconImageName", beanClass, "getIconImageName", "setIconImageName");
      PropertyDescriptor _leavesImageName = new PropertyDescriptor("leavesImageName", beanClass, "getLeavesImageName", "setLeavesImageName");
      PropertyDescriptor _loadWhenVisibile = new PropertyDescriptor("loadWhenVisibile", beanClass, "isLoadWhenVisibile", "setLoadWhenVisibile");
      PropertyDescriptor _rootVisibile = new PropertyDescriptor("rootVisibile", beanClass, "isRootVisibile", "setRootVisibile");
      PropertyDescriptor _rowHeight = new PropertyDescriptor("rowHeight", beanClass, "getRowHeight", "setRowHeight");
      PropertyDescriptor _selectionBackground = new PropertyDescriptor("selectionBackground", beanClass, "getSelectionBackground", "setSelectionBackground");
      PropertyDescriptor _selectionForeground = new PropertyDescriptor("selectionForeground", beanClass, "getSelectionForeground", "setSelectionForeground");
      PropertyDescriptor _showsRootHandles = new PropertyDescriptor("showsRootHandles", beanClass, "getShowsRootHandles", "setShowsRootHandles");
      PropertyDescriptor _tooltipAttributeName = new PropertyDescriptor("tooltipAttributeName", beanClass, "getTooltipAttributeName", "setTooltipAttributeName");
      PropertyDescriptor _treeController = new PropertyDescriptor("treeController", beanClass, "getTreeController", "setTreeController");
      PropertyDescriptor _treeDataLocator = new PropertyDescriptor("treeDataLocator", beanClass, "getTreeDataLocator", "setTreeDataLocator");
      PropertyDescriptor _selectionMode = new PropertyDescriptor("selectionMode", beanClass, "getSelectionMode", "setSelectionMode");
      PropertyDescriptor[] pds = new PropertyDescriptor[] {
        _expandAllNodes,
        _folderIconName,
        _iconImageName,
        _leavesImageName,
        _loadWhenVisibile,
        _rootVisibile,
        _rowHeight,
        _selectionBackground,
        _selectionForeground,
        _showsRootHandles,
        _tooltipAttributeName,
        _treeController,
        _treeDataLocator,
        _selectionMode
      };
      return pds;
    }
    catch(IntrospectionException ex) {
      ex.printStackTrace();
      return null;
    }
  }
  public java.awt.Image getIcon(int iconKind) {
    switch (iconKind) {
      case BeanInfo.ICON_COLOR_16x16:
        return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
      case BeanInfo.ICON_COLOR_32x32:
        return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
      case BeanInfo.ICON_MONO_16x16:
        return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
      case BeanInfo.ICON_MONO_32x32:
        return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
    }
    return null;
  }
}
